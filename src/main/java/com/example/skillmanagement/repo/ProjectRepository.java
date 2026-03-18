package com.example.skillmanagement.repo;

import com.example.skillmanagement.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    // Check for duplicate project name
    boolean existsByProjectNameIgnoreCase(String projectName);

    // ⭐ REQUIRED by ProjectService — returns last project ID
    @Query("SELECT MAX(p.id) FROM Project p")
    Long findMaxProjectId();
}