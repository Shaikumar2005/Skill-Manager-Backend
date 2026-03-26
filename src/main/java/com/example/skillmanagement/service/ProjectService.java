package com.example.skillmanagement.service;

import com.example.skillmanagement.dto.ProjectRequest;
import com.example.skillmanagement.dto.ProjectResponse;
import com.example.skillmanagement.dto.RequiredSkillDTO;
import com.example.skillmanagement.dto.SkillDTO;
import com.example.skillmanagement.dto.SkillGapResponse;
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

    @Transactional
    public ProjectResponse createProject(ProjectRequest req) {
        if (req == null) throw new IllegalArgumentException("Project payload is required");
        if (req.getProjectName() == null || req.getProjectName().isBlank()) {
            throw new IllegalArgumentException("Project name is required");
        }
        if (req.getRequiredSkills() == null || req.getRequiredSkills().isEmpty()) {
            throw new IllegalArgumentException("requiredSkills is required");
        }

        if (projectRepo.existsByProjectNameIgnoreCase(req.getProjectName())) {
            throw new IllegalArgumentException("Project already exists: " + req.getProjectName());
        }

        long distinctCount = req.getRequiredSkills().stream()
                .map(RequiredSkillDTO::getSkillId)
                .filter(Objects::nonNull)
                .distinct()
                .count();
        if (distinctCount != req.getRequiredSkills().size()) {
            throw new IllegalArgumentException("Duplicate skills are not allowed in a project");
        }

        Project p = new Project(req.getProjectName().trim());
        p = projectRepo.save(p);

        for (RequiredSkillDTO rs : req.getRequiredSkills()) {
            Skill s = skillService.getById(rs.getSkillId());
            prsRepo.save(new ProjectRequiredSkill(p, s, rs.getRequiredLevel()));
        }

        return getProjectResponse(p.getId());
    }

    public List<ProjectResponse> getAllProjects() {
        return projectRepo.findAll()
                .stream()
                .map(proj -> getProjectResponse(proj.getId()))
                .collect(Collectors.toList());
    }

    public ProjectResponse getProjectResponse(Long projectId) {
        Project p = projectRepo.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        List<RequiredSkillDTO> reqs = prsRepo.findByProjectId(projectId)
                .stream()
                .map(prs -> new RequiredSkillDTO(
                        prs.getSkill().getId(),
                        prs.getSkill().getName(),        // ✅ include skill name
                        prs.getSkill().getCategory(),
                        prs.getRequiredLevel()
                ))
                .collect(Collectors.toList());

        return new ProjectResponse(p.getId(), p.getProjectName(), reqs);  // ✅ use reqs
    }

    public SkillGapResponse computeSkillGap(Long projectId, Long userId) {
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

    @Transactional
    public void deleteProject(Long projectId) {
        Project p = projectRepo.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        prsRepo.deleteByProjectId(projectId);
        projectRepo.delete(p);
    }
    
    public List<User> getEmployeesFullyQualified(Long projectId) {
        Project p = projectRepo.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        List<ProjectRequiredSkill> requiredSkills = prsRepo.findByProjectId(projectId);

        List<User> allEmployees = userRepo.findByRole(Role.EMPLOYEE);// adjust query as needed

        return allEmployees.stream()
                .filter(emp -> {
                    List<EmployeeSkill> empSkills = employeeSkillRepo.findByUserId(emp.getId());
                    Map<Long, EmployeeSkill> empSkillMap = empSkills.stream()
                            .collect(Collectors.toMap(es -> es.getSkill().getId(), es -> es));

                    // Check every required skill
                    return requiredSkills.stream().allMatch(req -> {
                        EmployeeSkill es = empSkillMap.get(req.getSkill().getId());
                        return es != null && es.getProficiencyLevel() >= req.getRequiredLevel();
                    });
                })
                .collect(Collectors.toList());
    }

}
