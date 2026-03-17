package com.example.skillmanagement.controller;

import com.example.skillmanagement.dto.SkillDTO;
import com.example.skillmanagement.service.SkillService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/skills")
@CrossOrigin(origins = "http://localhost:4200")
public class SkillController {

    private final SkillService service;

    public SkillController(SkillService service) {
        this.service = service;
    }

    @GetMapping("/all")
    public ResponseEntity<List<SkillDTO>> getAllSkills() {
        return ResponseEntity.ok(service.getAll());
    }

    @PostMapping("/add")
    public ResponseEntity<SkillDTO> addSkill(@RequestBody SkillDTO dto) {
        SkillDTO saved = service.create(dto);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<SkillDTO> updateSkill(
            @PathVariable Long id,
            @RequestBody SkillDTO dto
    ) {
        SkillDTO updated = service.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteSkill(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok("Skill deleted successfully");
    }
}
