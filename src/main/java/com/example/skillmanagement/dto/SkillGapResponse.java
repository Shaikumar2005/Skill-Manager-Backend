package com.example.skillmanagement.dto;

import java.util.List;

public class SkillGapResponse {

    public static class InsufficientSkill {
        private Long skillId;
        private String skillName;
        private Integer employeeLevel;
        private Integer requiredLevel;

        public InsufficientSkill(Long skillId, String skillName, Integer employeeLevel, Integer requiredLevel) {
            this.skillId = skillId; this.skillName = skillName; this.employeeLevel = employeeLevel; this.requiredLevel = requiredLevel;
        }
        public Long getSkillId(){ return skillId; }
        public String getSkillName(){ return skillName; }
        public Integer getEmployeeLevel(){ return employeeLevel; }
        public Integer getRequiredLevel(){ return requiredLevel; }
    }

    private List<SkillDTO> missingSkills;
    private List<InsufficientSkill> insufficientSkills;

    public SkillGapResponse(List<SkillDTO> missingSkills, List<InsufficientSkill> insufficientSkills) {
        this.missingSkills = missingSkills; this.insufficientSkills = insufficientSkills;
    }

    public List<SkillDTO> getMissingSkills(){ return missingSkills; }
    public List<InsufficientSkill> getInsufficientSkills(){ return insufficientSkills; }
}