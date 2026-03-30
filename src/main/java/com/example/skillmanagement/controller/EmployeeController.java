package com.example.skillmanagement.controller;

import com.example.skillmanagement.dto.EmployeeSkillRequest;
import com.example.skillmanagement.dto.EmployeeSkillResponse;
import com.example.skillmanagement.dto.EmployeeSkillUpdateRequest;
import com.example.skillmanagement.model.User;
import com.example.skillmanagement.repo.UserRepository;
import com.example.skillmanagement.service.EmployeeSkillService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@CrossOrigin
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    private final EmployeeSkillService service;
    private final UserRepository userRepo;

    public EmployeeController(EmployeeSkillService service, UserRepository userRepo) {
        this.service = service;
        this.userRepo = userRepo;
    }

    private Long currentUserId(Authentication auth) {
        String email = auth.getName();
        return userRepo.findByEmail(email).map(User::getId).orElseThrow();
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentEmployee(Authentication auth) {
        String email = auth.getName();
        Optional<User> userOpt = userRepo.findByEmail(email);

        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = userOpt.get();
        Map<String, Object> response = new HashMap<>();
        response.put("id", user.getId());
        response.put("name", user.getName());
        response.put("email", user.getEmail());
        response.put("role", user.getRole());

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping("/skills")
    public ResponseEntity<List<EmployeeSkillResponse>> mySkills(Authentication auth) {
        return ResponseEntity.ok(service.getSkillsForUser(currentUserId(auth)));
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @PostMapping("/skills/add")
    public ResponseEntity<EmployeeSkillResponse> addMySkill(
            Authentication auth,
            @Valid @RequestBody EmployeeSkillRequest req) {
        return ResponseEntity.ok(service.addEmployeeSkill(currentUserId(auth), req));
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @PutMapping("/skills/{id}")
    public ResponseEntity<EmployeeSkillResponse> updateMySkill(
            Authentication auth,
            @PathVariable("id") Long id,
            @Valid @RequestBody EmployeeSkillUpdateRequest req) {
        return ResponseEntity.ok(service.updateEmployeeSkill(currentUserId(auth), id, req));
    }

    /** ✅ Delete endpoint matches Angular call */
    @PreAuthorize("hasRole('EMPLOYEE')")
    @DeleteMapping("/skills/delete/{id}")
    public ResponseEntity<Void> deleteMySkill(Authentication auth,
                                              @PathVariable("id") Long id) {
        service.deleteEmployeeSkill(currentUserId(auth), id);
        return ResponseEntity.noContent().build();
    }
}
