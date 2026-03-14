package com.example.skillmanagement.service;

import com.example.skillmanagement.dto.EmployeeSkillRequest;
import com.example.skillmanagement.dto.EmployeeSkillResponse;
import com.example.skillmanagement.exception.ResourceNotFoundException;
import com.example.skillmanagement.model.EmployeeSkill;
import com.example.skillmanagement.model.Skill;
import com.example.skillmanagement.model.User;
import com.example.skillmanagement.repo.EmployeeSkillRepository;
import com.example.skillmanagement.repo.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
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

    /**
     * Get all skills for a given user as DTOs.
     */
    public List<EmployeeSkillResponse> getSkillsForUser(Long userId) {
        if (userId == null) throw new IllegalArgumentException("User id is required");
        return repo.findByUserId(userId)
                .stream()
                .map(es -> new EmployeeSkillResponse(
                        es.getId(),
                        es.getSkill().getId(),
                        es.getSkill().getName(),
                        es.getProficiencyLevel(),
                        es.getYearsOfExperience()
                ))
                .collect(Collectors.toList());
    }

    /**
     * Upsert (create/update) multiple employee skills for a user.
     * - If (user, skill) pair exists, update proficiency & experience.
     * - Else create a new EmployeeSkill record.
     */
    @Transactional
    public List<EmployeeSkillResponse> upsertEmployeeSkills(Long userId, List<EmployeeSkillRequest> list) {
        if (userId == null) throw new IllegalArgumentException("User id is required");
        if (list == null) throw new IllegalArgumentException("Payload list is required");

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<EmployeeSkillResponse> responses = new ArrayList<>();

        for (EmployeeSkillRequest req : list) {
            if (req.getSkillId() == null) {
                throw new IllegalArgumentException("skillId is required");
            }
            if (req.getProficiencyLevel() == null) {
                throw new IllegalArgumentException("proficiencyLevel is required");
            }
            if (req.getYearsOfExperience() == null) {
                throw new IllegalArgumentException("yearsOfExperience is required");
            }

            Skill skill = skillService.getById(req.getSkillId());

            EmployeeSkill es = repo.findByUserIdAndSkillId(userId, req.getSkillId())
                    .orElse(new EmployeeSkill(user, skill, req.getProficiencyLevel(), req.getYearsOfExperience()));

            es.setUser(user);
            es.setSkill(skill);
            es.setProficiencyLevel(req.getProficiencyLevel());
            es.setYearsOfExperience(req.getYearsOfExperience());

            es = repo.save(es);

            responses.add(new EmployeeSkillResponse(
                    es.getId(),
                    skill.getId(),
                    skill.getName(),
                    es.getProficiencyLevel(),
                    es.getYearsOfExperience()
            ));
        }

        return responses;
    }
}