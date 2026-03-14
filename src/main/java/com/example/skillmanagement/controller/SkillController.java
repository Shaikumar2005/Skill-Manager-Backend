package com.example.skillmanagement.controller;

import com.example.skillmanagement.dto.SkillDTO;
import com.example.skillmanagement.service.SkillService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/skills")
public class SkillController {

    private final SkillService service;
    public SkillController(SkillService service){ this.service = service; }

    @GetMapping
    public ResponseEntity<List<SkillDTO>> getAll(){
        return ResponseEntity.ok(service.getAll());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SkillDTO> create(@RequestBody @Valid SkillDTO dto){
        return new ResponseEntity<>(service.create(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SkillDTO> update(@PathVariable Long id, @RequestBody @Valid SkillDTO dto){
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}