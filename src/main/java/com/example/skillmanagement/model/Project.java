package com.example.skillmanagement.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "projects")
public class Project {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "project_name", unique = true)
    private String projectName;

    public Project() {}
    public Project(String projectName){ this.projectName = projectName; }

    // getters/setters
    public Long getId(){ return id; }
    public void setId(Long id){ this.id = id; }
    public String getProjectName(){ return projectName; }
    public void setProjectName(String projectName){ this.projectName = projectName; }
}