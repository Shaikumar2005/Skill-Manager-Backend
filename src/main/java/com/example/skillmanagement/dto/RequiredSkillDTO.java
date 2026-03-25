package com.example.skillmanagement.dto;

public class RequiredSkillDTO {
    private Long skillId;
    private String skillName;
    private String category;
    private Integer requiredLevel;

    public RequiredSkillDTO(Long skillId, String skillName, String category, Integer requiredLevel) {
        this.skillId = skillId;
        this.skillName = skillName;
        this.category = category;
        this.requiredLevel = requiredLevel;
    }

    // getters
    public Long getSkillId(){ return skillId; }
    public String getSkillName(){ return skillName; }
    public String getCategory(){ return category; }
    public Integer getRequiredLevel(){ return requiredLevel; }
}
