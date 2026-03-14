package com.example.skillmanagement.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "project_required_skills",
       uniqueConstraints = @UniqueConstraint(columnNames = {"project_id", "skill_id"}))
public class ProjectRequiredSkill {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false) @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne(optional = false) @JoinColumn(name = "skill_id")
    private Skill skill;

    @NotNull @Min(1) @Max(5)
    @Column(name = "required_level")
    private Integer requiredLevel;

    public ProjectRequiredSkill() {}
    public ProjectRequiredSkill(Project project, Skill skill, Integer requiredLevel) {
        this.project = project; this.skill = skill; this.requiredLevel = requiredLevel;
    }

    // getters/setters
    public Long getId(){ return id; }
    public void setId(Long id){ this.id = id; }
    public Project getProject(){ return project; }
    public void setProject(Project project){ this.project = project; }
    public Skill getSkill(){ return skill; }
    public void setSkill(Skill skill){ this.skill = skill; }
    public Integer getRequiredLevel(){ return requiredLevel; }
    public void setRequiredLevel(Integer requiredLevel){ this.requiredLevel = requiredLevel; }
}