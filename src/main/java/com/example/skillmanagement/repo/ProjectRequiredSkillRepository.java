package com.example.skillmanagement.repo;

import com.example.skillmanagement.model.ProjectRequiredSkill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectRequiredSkillRepository extends JpaRepository<ProjectRequiredSkill, Long> {
    List<ProjectRequiredSkill> findByProjectId(Long projectId);
    boolean existsByProjectIdAndSkillId(Long projectId, Long skillId);
}