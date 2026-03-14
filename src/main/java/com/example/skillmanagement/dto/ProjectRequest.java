package com.example.skillmanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class ProjectRequest {
    @NotBlank
    private String projectName;

    @NotNull
    private List<RequiredSkillDTO> requiredSkills;

    public String getProjectName(){ return projectName; }
    public void setProjectName(String projectName){ this.projectName = projectName; }
    public List<RequiredSkillDTO> getRequiredSkills(){ return requiredSkills; }
    public void setRequiredSkills(List<RequiredSkillDTO> requiredSkills){ this.requiredSkills = requiredSkills; }
}