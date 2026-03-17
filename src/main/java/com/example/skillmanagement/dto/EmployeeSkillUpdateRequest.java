package com.example.skillmanagement.dto;

public class EmployeeSkillUpdateRequest {
    private int proficiencyLevel;
    private int yearsOfExperience;
    // getters/setters
	public int getProficiencyLevel() {
		return proficiencyLevel;
	}
	public void setProficiencyLevel(int proficiencyLevel) {
		this.proficiencyLevel = proficiencyLevel;
	}
	public int getYearsOfExperience() {
		return yearsOfExperience;
	}
	public void setYearsOfExperience(int yearsOfExperience) {
		this.yearsOfExperience = yearsOfExperience;
	}
}
