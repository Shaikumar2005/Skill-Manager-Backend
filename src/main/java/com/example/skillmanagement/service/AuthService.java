package com.example.skillmanagement.service;

import com.example.skillmanagement.dto.LoginRequest;
import com.example.skillmanagement.dto.LoginResponse;
import com.example.skillmanagement.dto.RegisterRequest;
import com.example.skillmanagement.dto.UserResponse;
import com.example.skillmanagement.model.Role;
import com.example.skillmanagement.model.User;
import com.example.skillmanagement.repo.UserRepository;
import com.example.skillmanagement.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepo;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthService(AuthenticationManager authenticationManager,
                       UserRepository userRepo,
                       JwtService jwtService,
                       PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.userRepo = userRepo;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Authenticates the user and returns a JWT plus basic user profile
     * so the frontend can route based on role.
     *
     * @param request contains email and password
     * @return LoginResponse with token, role, userId, name, email
     * @throws org.springframework.security.authentication.BadCredentialsException for invalid creds
     */
    public LoginResponse login(LoginRequest request) {
        // 1) Authenticate via Spring Security
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // 2) Fetch the user to include details & role in response and token claims
        User user = userRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 3) Build JWT with extra claims for convenience
        String token = jwtService.generateToken(
                user.getEmail(),
                Map.of(
                        "role", user.getRole().name(),
                        "name", user.getName(),
                        "userId", user.getId()
                )
        );

        // 4) Return response used by Angular to route by role
        return new LoginResponse(
                token,
                user.getRole().name(),
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    /**
     * Public self-registration. Always creates an EMPLOYEE user.
     */
    public UserResponse register(RegisterRequest req) {
        if (req == null) {
            throw new IllegalArgumentException("Registration payload is required");
        }
        if (userRepo.existsByEmail(req.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + req.getEmail());
        }

        User user = new User(
                req.getName().trim(),
                req.getEmail().toLowerCase().trim(),
                passwordEncoder.encode(req.getPassword()),
                Role.EMPLOYEE // ALWAYS create employee on self-registration
        );

        user = userRepo.save(user);

        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole().name()
        );
    }
}