package com.example.skillmanagement.dto;

import com.example.skillmanagement.model.EmployeeSkill;

public class EmployeeSkillResponse {
    private Long id;
    private Long skillId;
    private String skillName;
    private String category;
    private Integer proficiencyLevel;
    private Integer yearsOfExperience;

    public EmployeeSkillResponse(EmployeeSkill es) {
        this.id = es.getId();
        this.skillId = es.getSkill().getId();
        this.skillName = es.getSkill().getName();     // ✅ include skill name
        this.category = es.getSkill().getCategory();
        this.proficiencyLevel = es.getProficiencyLevel();
        this.yearsOfExperience = es.getYearsOfExperience();
    }

    // getters
    public Long getId(){ return id; }
    public Long getSkillId(){ return skillId; }
    public String getSkillName(){ return skillName; }
    public String getCategory(){ return category; }
    public Integer getProficiencyLevel(){ return proficiencyLevel; }
    public Integer getYearsOfExperience(){ return yearsOfExperience; }
}
