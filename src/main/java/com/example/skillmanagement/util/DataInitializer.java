package com.example.skillmanagement.util;

import com.example.skillmanagement.model.*;
import com.example.skillmanagement.model.Role;
import com.example.skillmanagement.repo.SkillRepository;
import com.example.skillmanagement.repo.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner init(UserRepository userRepo, PasswordEncoder encoder, SkillRepository skillRepo) {
        return args -> {
            if (!userRepo.existsByEmail("admin@company.com")) {
                User admin = new User("Admin User", "admin@company.com", encoder.encode("Admin@123"), Role.ADMIN);
                userRepo.save(admin);
            }
            if (!userRepo.existsByEmail("employee@company.com")) {
                User emp = new User("Employee One", "employee@company.com", encoder.encode("Employee@123"), Role.EMPLOYEE);
                userRepo.save(emp);
            }

           
        };
    }
}