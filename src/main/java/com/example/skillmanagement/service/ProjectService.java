package com.example.skillmanagement.service;

import com.example.skillmanagement.dto.*;
import com.example.skillmanagement.exception.ResourceNotFoundException;
import com.example.skillmanagement.model.*;
import com.example.skillmanagement.repo.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProjectService {

    private final ProjectRepository projectRepo;
    private final ProjectRequiredSkillRepository prsRepo;
    private final SkillService skillService;
    private final EmployeeSkillRepository employeeSkillRepo;
    private final UserRepository userRepo;

    public ProjectService(ProjectRepository projectRepo,
                          ProjectRequiredSkillRepository prsRepo,
                          SkillService skillService,
                          EmployeeSkillRepository employeeSkillRepo,
                          UserRepository userRepo) {
        this.projectRepo = projectRepo;
        this.prsRepo = prsRepo;
        this.skillService = skillService;
        this.employeeSkillRepo = employeeSkillRepo;
        this.userRepo = userRepo;
    }

    

    /**
     * Create a new Project along with its required skills.
     */
    @Transactional
    public ProjectResponse createProject(ProjectRequest req) {
        if (req == null) throw new IllegalArgumentException("Project payload is required");
        if (req.getProjectName() == null || req.getProjectName().isBlank()) {
            throw new IllegalArgumentException("Project name is required");
        }
        if (req.getRequiredSkills() == null) {
            throw new IllegalArgumentException("requiredSkills is required");
        }

        if (projectRepo.existsByProjectNameIgnoreCase(req.getProjectName())) {
            throw new IllegalArgumentException("Project already exists: " + req.getProjectName());
        }

        Project p = new Project(req.getProjectName().trim());
        p = projectRepo.save(p);

        for (RequiredSkillDTO rs : req.getRequiredSkills()) {
            if (rs.getSkillId() == null) {
                throw new IllegalArgumentException("requiredSkills.skillId is required");
            }
            if (rs.getRequiredLevel() == null) {
                throw new IllegalArgumentException("requiredSkills.requiredLevel is required");
            }
            Skill s = skillService.getById(rs.getSkillId());
            if (!prsRepo.existsByProjectIdAndSkillId(p.getId(), s.getId())) {
                prsRepo.save(new ProjectRequiredSkill(p, s, rs.getRequiredLevel()));
            }
        }

        return getProjectResponse(p.getId());
    }

    /**
     * Return all projects.
     */
    public List<ProjectResponse> getAllProjects() {
        return projectRepo.findAll()
                .stream()
                .map(proj -> getProjectResponse(proj.getId()))
                .collect(Collectors.toList());
    }

    /**
     * Build a ProjectResponse for given project ID.
     */
    public ProjectResponse getProjectResponse(Long projectId) {
        Project p = projectRepo.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        List<RequiredSkillDTO> reqs = prsRepo.findByProjectId(projectId)
                .stream()
                .map(prs -> {
                    RequiredSkillDTO dto = new RequiredSkillDTO();
                    dto.setSkillId(prs.getSkill().getId());
                    dto.setRequiredLevel(prs.getRequiredLevel());
                    return dto;
                })
                .collect(Collectors.toList());

        return new ProjectResponse(p.getId(), p.getProjectName(), reqs);
    }

    /**
     * Compute skill gap.
     */
    public SkillGapResponse computeSkillGap(Long projectId, Long userId) {
        if (projectId == null) throw new IllegalArgumentException("projectId is required");
        if (userId == null) throw new IllegalArgumentException("userId is required");

        Project p = projectRepo.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<ProjectRequiredSkill> required = prsRepo.findByProjectId(projectId);
        Map<Long, ProjectRequiredSkill> requiredMap = required.stream()
                .collect(Collectors.toMap(prs -> prs.getSkill().getId(), prs -> prs));

        List<EmployeeSkill> empSkills = employeeSkillRepo.findByUserId(userId);
        Map<Long, EmployeeSkill> empMap = empSkills.stream()
                .collect(Collectors.toMap(es -> es.getSkill().getId(), es -> es));

        List<SkillDTO> missing = new ArrayList<>();
        List<SkillGapResponse.InsufficientSkill> insufficient = new ArrayList<>();

        for (ProjectRequiredSkill prs : required) {
            Long skillId = prs.getSkill().getId();
            if (!empMap.containsKey(skillId)) {
                missing.add(new SkillDTO(skillId, prs.getSkill().getName(), prs.getSkill().getCategory()));
            } else {
                EmployeeSkill es = empMap.get(skillId);
                if (es.getProficiencyLevel() < prs.getRequiredLevel()) {
                    insufficient.add(new SkillGapResponse.InsufficientSkill(
                            skillId,
                            prs.getSkill().getName(),
                            es.getProficiencyLevel(),
                            prs.getRequiredLevel()
                    ));
                }
            }
        }

        return new SkillGapResponse(missing, insufficient);
    }
}
