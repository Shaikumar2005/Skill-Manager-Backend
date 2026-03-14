package com.example.skillmanagement.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class EmployeeSkillRequest {
    @NotNull
    private Long skillId;
    @NotNull @Min(1) @Max(5)
    private Integer proficiencyLevel;
    @NotNull
    private Integer yearsOfExperience;

    // getters/setters
    public Long getSkillId(){ return skillId; }
    public void setSkillId(Long skillId){ this.skillId = skillId; }
    public Integer getProficiencyLevel(){ return proficiencyLevel; }
    public void setProficiencyLevel(Integer proficiencyLevel){ this.proficiencyLevel = proficiencyLevel; }
    public Integer getYearsOfExperience(){ return yearsOfExperience; }
    public void setYearsOfExperience(Integer yearsOfExperience){ this.yearsOfExperience = yearsOfExperience; }
}