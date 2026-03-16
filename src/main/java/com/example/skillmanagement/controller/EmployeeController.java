package com.example.skillmanagement.controller; 

  

import com.example.skillmanagement.dto.EmployeeSkillRequest; 

import com.example.skillmanagement.dto.EmployeeSkillResponse; 

import com.example.skillmanagement.model.User; 

import com.example.skillmanagement.repo.UserRepository; 

import com.example.skillmanagement.service.EmployeeSkillService; 

import jakarta.validation.Valid; 

import org.springframework.http.ResponseEntity; 

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

        this.service = service; this.userRepo = userRepo; 

    } 

  

    private Long currentUserId(Authentication auth) { 

        String email = auth.getName(); 

        Optional<User> u = userRepo.findByEmail(email); 

        return u.map(User::getId).orElseThrow(); 

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

  

  

    @GetMapping("/skills") 

    public ResponseEntity<List<EmployeeSkillResponse>> mySkills(Authentication auth) { 

        return ResponseEntity.ok(service.getSkillsForUser(currentUserId(auth))); 

    } 

  

    @PostMapping("/skills") 

    public ResponseEntity<List<EmployeeSkillResponse>> upsertMySkills(Authentication auth, 

                                                                      @RequestBody List<@Valid EmployeeSkillRequest> list) { 

        return ResponseEntity.ok(service.upsertEmployeeSkills(currentUserId(auth), list)); 

    } 

} 
