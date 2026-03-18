package com.example.skillmanagement.controller;

import com.example.skillmanagement.dto.*;
import com.example.skillmanagement.model.User;
import com.example.skillmanagement.repo.UserRepository;
import com.example.skillmanagement.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService service;
    private final UserRepository userRepo;

    public ProjectController(ProjectService service, UserRepository userRepo) {
        this.service = service;
        this.userRepo = userRepo;
    }

    // -----------------------------------------------------------
    // 🔥 ADDED: For Angular — POST /projects/add
    // -----------------------------------------------------------
    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProjectResponse> createProject(@RequestBody @Valid ProjectRequest req) {
        return new ResponseEntity<>(service.createProject(req), HttpStatus.CREATED);
    }

    // -----------------------------------------------------------
    // 🔥 ADDED: Get last project Id → auto increment in Angular
    // -----------------------------------------------------------
    @GetMapping("/last-id")
    public ResponseEntity<Long> getLastProjectId() {
        return ResponseEntity.ok(service.getLastProjectId());
    }

    // -----------------------------------------------------------
    // Existing create (POST /projects)
    // (Kept exactly as you wrote)
    // -----------------------------------------------------------
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProjectResponse> create(@RequestBody @Valid ProjectRequest req) {
        return new ResponseEntity<>(service.createProject(req), HttpStatus.CREATED);
    }

    // -----------------------------------------------------------
    // Existing GET all projects
    // -----------------------------------------------------------
    @GetMapping
    public ResponseEntity<List<ProjectResponse>> getAll() {
        return ResponseEntity.ok(service.getAllProjects());
    }

    // -----------------------------------------------------------
    // Skill gap for current user
    // -----------------------------------------------------------
    @GetMapping("/{projectId}/skill-gap")
    public ResponseEntity<SkillGapResponse> gapForCurrentUser(
            @PathVariable Long projectId,
            Authentication auth) {

        User u = userRepo.findByEmail(auth.getName()).orElseThrow();
        return ResponseEntity.ok(service.computeSkillGap(projectId, u.getId()));
    }

    // -----------------------------------------------------------
    // Admin can compute gap for any user
    // -----------------------------------------------------------
    @GetMapping("/{projectId}/skill-gap/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SkillGapResponse> gapForUser(
            @PathVariable Long projectId,
            @PathVariable Long userId) {
        return ResponseEntity.ok(service.computeSkillGap(projectId, userId));
    }
}