package com.example.skillmanagement.dto;

import java.util.List;

public class ProjectResponse {
    private Long id;
    private String projectName;
    private List<RequiredSkillDTO> requiredSkills;

    // Constructor used in ProjectService
    public ProjectResponse(Long id, String projectName, List<RequiredSkillDTO> requiredSkills) {
        this.id = id;
        this.projectName = projectName;
        this.requiredSkills = requiredSkills;
    }

    // getters
    public Long getId(){ return id; }
    public String getProjectName(){ return projectName; }
    public List<RequiredSkillDTO> getRequiredSkills(){ return requiredSkills; }
}
