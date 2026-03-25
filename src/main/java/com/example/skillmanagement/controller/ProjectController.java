package com.example.skillmanagement.controller;

import com.example.skillmanagement.dto.ProjectRequest;
import com.example.skillmanagement.dto.ProjectResponse;
import com.example.skillmanagement.dto.SkillGapResponse;
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

    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProjectResponse> createProject(@RequestBody @Valid ProjectRequest req) {
        return new ResponseEntity<>(service.createProject(req), HttpStatus.CREATED);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProjectResponse> create(@RequestBody @Valid ProjectRequest req) {
        return new ResponseEntity<>(service.createProject(req), HttpStatus.CREATED);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ProjectResponse>> getAll() {
        // ✅ service.getAllProjects() should return ProjectResponse objects
        return ResponseEntity.ok(service.getAllProjects());
    }

    @GetMapping("/{projectId}/skill-gap")
    public ResponseEntity<SkillGapResponse> gapForCurrentUser(
            @PathVariable("projectId") Long projectId,
            Authentication auth) {
        User u = userRepo.findByEmail(auth.getName()).orElseThrow();
        return ResponseEntity.ok(service.computeSkillGap(projectId, u.getId()));
    }

    @GetMapping("/{projectId}/skill-gap/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SkillGapResponse> gapForUser(
            @PathVariable("projectId") Long projectId,
            @PathVariable("userId") Long userId) {
        return ResponseEntity.ok(service.computeSkillGap(projectId, userId));
    }

    @DeleteMapping("/{projectId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProject(@PathVariable("projectId") Long projectId) {
        service.deleteProject(projectId);
        return ResponseEntity.noContent().build();
    }
}
