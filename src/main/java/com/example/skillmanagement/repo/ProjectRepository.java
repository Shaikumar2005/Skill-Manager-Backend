package com.example.skillmanagement.repo;

import com.example.skillmanagement.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    boolean existsByProjectNameIgnoreCase(String name);
}