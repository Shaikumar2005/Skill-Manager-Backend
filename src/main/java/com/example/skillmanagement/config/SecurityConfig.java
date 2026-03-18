package com.example.skillmanagement.config;

import com.example.skillmanagement.security.JwtAuthenticationFilter;
import com.example.skillmanagement.service.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.*;

import java.util.List;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;
    private final UserDetailsServiceImpl userDetailsService;

    public SecurityConfig(JwtAuthenticationFilter jwtFilter,
                          UserDetailsServiceImpl userDetailsService) {
        this.jwtFilter = jwtFilter;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            .authorizeHttpRequests(auth -> auth

                // Public endpoints (no login)
                .requestMatchers("/auth/login", "/auth/register").permitAll()

                // Swagger allowed publicly
                .requestMatchers(
                        "/swagger-ui.html",
                        "/swagger-ui/**",
                        "/v3/api-docs/**"
                ).permitAll()

                // FIX: allow all skills endpoints BEFORE restrictions
                .requestMatchers("/skills/all").permitAll()
                .requestMatchers("/skills/**").permitAll()

                // Employee profile & skills
                .requestMatchers("/employee/me").hasRole("EMPLOYEE")
                .requestMatchers(HttpMethod.GET, "/employee/skills").hasAnyRole("EMPLOYEE","ADMIN")
                .requestMatchers(HttpMethod.POST, "/employee/skills").hasRole("EMPLOYEE")
                .requestMatchers(HttpMethod.PUT, "/employee/skills/**").hasRole("EMPLOYEE")
                .requestMatchers(HttpMethod.DELETE, "/employee/skills/**").hasRole("EMPLOYEE")

                // Skills catalog (original rules – left unchanged)
                .requestMatchers(HttpMethod.GET, "/skills/**").hasAnyRole("EMPLOYEE","ADMIN")
                .requestMatchers(HttpMethod.PUT, "/skills/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/skills/**").hasRole("ADMIN")

                // Project rules (unchanged)
                .requestMatchers(HttpMethod.GET, "/projects").hasAnyRole("EMPLOYEE","ADMIN")
                .requestMatchers(HttpMethod.POST, "/projects").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/projects/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/projects/**").hasRole("ADMIN")

                // Everything else requires authentication
                .anyRequest().authenticated()
            )

            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .userDetailsService(userDetailsService);

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:4200"));
        config.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization","Content-Type"));
        config.setAllowCredentials(true); 

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration cfg) throws Exception {
        return cfg.getAuthenticationManager();
    }
}