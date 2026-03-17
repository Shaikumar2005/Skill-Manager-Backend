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

    public List<SkillDTO> getAll() {
        return repo.findAll()
                .stream()
                .map(s -> new SkillDTO(s.getId(), s.getName(), s.getCategory()))
                .collect(Collectors.toList());
    }

    public SkillDTO create(SkillDTO dto) {
        if (dto == null) throw new IllegalArgumentException("Skill payload is required");
        if (dto.getName() == null || dto.getName().isBlank())
            throw new IllegalArgumentException("Skill name is required");

        if (repo.existsByNameIgnoreCase(dto.getName()))
            throw new IllegalArgumentException("Skill already exists: " + dto.getName());

        Skill skill = new Skill(dto.getName().trim(), dto.getCategory());
        Skill saved = repo.save(skill);

        return new SkillDTO(saved.getId(), saved.getName(), saved.getCategory());
    }

    public SkillDTO update(Long id, SkillDTO dto) {
        if (id == null) throw new IllegalArgumentException("Skill id required");
        if (dto == null) throw new IllegalArgumentException("Skill payload required");

        Skill existing = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found"));

        if (dto.getName() == null || dto.getName().isBlank())
            throw new IllegalArgumentException("Skill name is required");

        existing.setName(dto.getName().trim());
        existing.setCategory(dto.getCategory());

        Skill saved = repo.save(existing);
        return new SkillDTO(saved.getId(), saved.getName(), saved.getCategory());
    }

    public void delete(Long id) {
        if (id == null) throw new IllegalArgumentException("Skill id required");
        if (!repo.existsById(id)) throw new ResourceNotFoundException("Skill not found");
        repo.deleteById(id);
    }

    public Skill getById(Long id) {
        if (id == null) throw new IllegalArgumentException("Skill id required");
        return repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found"));
    }
}