package com.example.skillmanagement.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class RequiredSkillDTO {
    @NotNull private Long skillId;
    @NotNull @Min(1) @Max(5) private Integer requiredLevel;

    public Long getSkillId(){ return skillId; }
    public void setSkillId(Long skillId){ this.skillId = skillId; }
    public Integer getRequiredLevel(){ return requiredLevel; }
    public void setRequiredLevel(Integer requiredLevel){ this.requiredLevel = requiredLevel; }
}