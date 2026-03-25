package com.example.skillmanagement.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "projects")
public class Project {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String projectName;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectRequiredSkill> requiredSkills = new ArrayList<>();

    public Project() {}

    public Project(String projectName) {
        this.projectName = projectName;
    }

    // getters/setters
    public Long getId(){ return id; }
    public void setId(Long id){ this.id = id; }

    public String getProjectName(){ return projectName; }
    public void setProjectName(String projectName){ this.projectName = projectName; }

    public List<ProjectRequiredSkill> getRequiredSkills(){ return requiredSkills; }
    public void setRequiredSkills(List<ProjectRequiredSkill> requiredSkills){ this.requiredSkills = requiredSkills; }
}
