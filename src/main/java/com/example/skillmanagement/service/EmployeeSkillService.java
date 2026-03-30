package com.example.skillmanagement.service;

import com.example.skillmanagement.dto.EmployeeSkillRequest;
import com.example.skillmanagement.dto.EmployeeSkillResponse;
import com.example.skillmanagement.dto.EmployeeSkillUpdateRequest;
import com.example.skillmanagement.exception.ResourceNotFoundException;
import com.example.skillmanagement.model.EmployeeSkill;
import com.example.skillmanagement.model.Skill;
import com.example.skillmanagement.model.User;
import com.example.skillmanagement.repo.EmployeeSkillRepository;
import com.example.skillmanagement.repo.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeeSkillService {

    private final EmployeeSkillRepository repo;
    private final SkillService skillService;
    private final UserRepository userRepo;

    public EmployeeSkillService(EmployeeSkillRepository repo, SkillService skillService, UserRepository userRepo) {
        this.repo = repo;
        this.skillService = skillService;
        this.userRepo = userRepo;
    }

    /** Get all skills for a user */
    public List<EmployeeSkillResponse> getSkillsForUser(Long userId) {
        return repo.findByUserId(userId)
                .stream()
                .map(EmployeeSkillResponse::new)
                .collect(Collectors.toList());
    }

    /** Add a new skill for an employee */
    @Transactional
    public EmployeeSkillResponse addEmployeeSkill(Long userId, EmployeeSkillRequest req) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Skill skill = skillService.getById(req.getSkillId());

        if (repo.findByUserIdAndSkillId(userId, req.getSkillId()).isPresent()) {
            throw new IllegalArgumentException("Skill already exists for this employee");
        }

        EmployeeSkill es = new EmployeeSkill(user, skill, req.getProficiencyLevel(), req.getYearsOfExperience());
        repo.save(es);

        return new EmployeeSkillResponse(es);
    }

    /** Update an employee skill */
    @Transactional
    public EmployeeSkillResponse updateEmployeeSkill(Long userId, Long id, EmployeeSkillUpdateRequest req) {
        EmployeeSkill es = repo.findById(id)
                .filter(s -> s.getUser().getId().equals(userId))
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found for this user"));

        es.setProficiencyLevel(req.getProficiencyLevel());
        es.setYearsOfExperience(req.getYearsOfExperience());
        repo.save(es);

        return new EmployeeSkillResponse(es);
    }

    /** ✅ Delete an employee skill */
    @Transactional
    public void deleteEmployeeSkill(Long userId, Long skillId) {
        EmployeeSkill es = repo.findById(skillId)
                .filter(s -> s.getUser().getId().equals(userId))
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found for this user"));
        repo.delete(es);
    }
}
