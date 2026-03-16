package com.example.skillmanagement.repo;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.skillmanagement.model.Skill;

public interface SkillRepository extends JpaRepository<Skill, Long> {
    Optional<Skill> findByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCase(String name);
}