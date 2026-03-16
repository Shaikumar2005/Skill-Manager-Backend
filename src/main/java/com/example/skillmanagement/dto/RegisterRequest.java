package com.example.skillmanagement.dto;

import com.example.skillmanagement.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class RegisterRequest {

    @NotBlank
    private String name;

    @Email @NotBlank
    private String email;

    @NotBlank
    private String password;

    // Optional (used only by Admin endpoint). Ignore on public register.
    private Role role;

    public String getName() { return name; }
    public void setName(String name){ this.name = name; }

    public String getEmail(){ return email; }
    public void setEmail(String email){ this.email = email; }

    public String getPassword(){ return password; }
    public void setPassword(String password){ this.password = password; }

    public Role getRole(){ return role; }
    public void setRole(Role role){ this.role = role; }
}