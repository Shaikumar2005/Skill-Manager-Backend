package com.example.skillmanagement.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "employee_skills",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "skill_id"}))
public class EmployeeSkill {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false) @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(optional = false) @JoinColumn(name = "skill_id")
    private Skill skill;

    @NotNull @Min(1) @Max(5)
    @Column(name = "proficiency_level")
    private Integer proficiencyLevel;

    @NotNull
    @Column(name = "years_of_experience")
    private Integer yearsOfExperience;

    public EmployeeSkill() {}

    public EmployeeSkill(User user, Skill skill, Integer proficiencyLevel, Integer yearsOfExperience) {
        this.user = user; this.skill = skill; this.proficiencyLevel = proficiencyLevel; this.yearsOfExperience = yearsOfExperience;
    }

    // getters/setters
    public Long getId(){ return id; }
    public void setId(Long id){ this.id = id; }
    public User getUser(){ return user; }
    public void setUser(User user){ this.user = user; }
    public Skill getSkill(){ return skill; }
    public void setSkill(Skill skill){ this.skill = skill; }
    public Integer getProficiencyLevel(){ return proficiencyLevel; }
    public void setProficiencyLevel(Integer proficiencyLevel){ this.proficiencyLevel = proficiencyLevel; }
    public Integer getYearsOfExperience(){ return yearsOfExperience; }
    public void setYearsOfExperience(Integer yearsOfExperience){ this.yearsOfExperience = yearsOfExperience; }
}