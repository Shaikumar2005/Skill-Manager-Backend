package com.example.skillmanagement.service;

import com.example.skillmanagement.dto.SkillDTO;
import com.example.skillmanagement.exception.ResourceNotFoundException;
import com.example.skillmanagement.model.Skill;
import com.example.skillmanagement.repo.SkillRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SkillService {

    private final SkillRepository repo;

    public SkillService(SkillRepository repo) {
        this.repo = repo;
    }

    /**
     * Get all skills as DTOs.
     */
    public List<SkillDTO> getAll() {
        return repo.findAll()
                .stream()
                .map(s -> new SkillDTO(s.getId(), s.getName(), s.getCategory()))
                .collect(Collectors.toList());
    }

    /**
     * Create a new skill.
     * - Ensures no duplicate by name (case-insensitive).
     */
    public SkillDTO create(SkillDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Skill payload is required");
        }
        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new IllegalArgumentException("Skill name is required");
        }
        if (repo.existsByNameIgnoreCase(dto.getName())) {
            throw new IllegalArgumentException("Skill already exists with name: " + dto.getName());
        }
        Skill s = new Skill(dto.getName().trim(), dto.getCategory());
        s = repo.save(s);
        return new SkillDTO(s.getId(), s.getName(), s.getCategory());
    }

    /**
     * Update an existing skill by id.
     * - Throws ResourceNotFoundException if not present.
     * - Allows updating name and category.
     * - If you want to prohibit renaming to another existing name, add a duplicate check here.
     */
    public SkillDTO update(Long id, SkillDTO dto) {
        if (id == null) throw new IllegalArgumentException("Skill id is required");
        if (dto == null) throw new IllegalArgumentException("Skill payload is required");

        Skill s = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found"));

        // Optional: prevent renaming into an existing name (uncomment to enforce)
        // if (!s.getName().equalsIgnoreCase(dto.getName()) && repo.existsByNameIgnoreCase(dto.getName())) {
        //     throw new IllegalArgumentException("Another skill already exists with name: " + dto.getName());
        // }

        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new IllegalArgumentException("Skill name is required");
        }

        s.setName(dto.getName().trim());
        s.setCategory(dto.getCategory());
        s = repo.save(s);

        return new SkillDTO(s.getId(), s.getName(), s.getCategory());
    }

    /**
     * Delete a skill by id.
     */
    public void delete(Long id) {
        if (id == null) throw new IllegalArgumentException("Skill id is required");
        if (!repo.existsById(id)) {
            throw new ResourceNotFoundException("Skill not found");
        }
        repo.deleteById(id);
    }

    /**
     * Helper used by other services to fetch Skill entity by id.
     */
    public Skill getById(Long id) {
        if (id == null) throw new IllegalArgumentException("Skill id is required");
        return repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found"));
    }
}