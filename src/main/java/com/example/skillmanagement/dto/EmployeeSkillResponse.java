package com.example.skillmanagement.dto;

public class EmployeeSkillResponse {
    private Long id;
    private Long skillId;
    private String skillName;
    private Integer proficiencyLevel;
    private Integer yearsOfExperience;

    public EmployeeSkillResponse(Long id, Long skillId, String skillName, Integer proficiencyLevel, Integer yearsOfExperience) {
        this.id = id; this.skillId = skillId; this.skillName = skillName; this.proficiencyLevel = proficiencyLevel; this.yearsOfExperience = yearsOfExperience;
    }

    // getters
    public Long getId(){ return id; }
    public Long getSkillId(){ return skillId; }
    public String getSkillName(){ return skillName; }
    public Integer getProficiencyLevel(){ return proficiencyLevel; }
    public Integer getYearsOfExperience(){ return yearsOfExperience; }
}