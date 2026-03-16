package com.example.skillmanagement.config; 

import com.example.skillmanagement.security.JwtAuthenticationFilter; import com.example.skillmanagement.service.UserDetailsServiceImpl; import org.springframework.context.annotation.Bean; import org.springframework.context.annotation.Configuration; import org.springframework.http.HttpMethod; import org.springframework.security.authentication.AuthenticationManager; import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration; import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity; import org.springframework.security.config.annotation.web.builders.HttpSecurity; import org.springframework.security.config.http.SessionCreationPolicy; import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; import org.springframework.security.crypto.password.PasswordEncoder; import org.springframework.security.web.SecurityFilterChain; import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter; import org.springframework.web.cors.*; 

import java.util.List; 

@Configuration @EnableMethodSecurity public class SecurityConfig { 

private final JwtAuthenticationFilter jwtFilter; 
private final UserDetailsServiceImpl userDetailsService; 
 
public SecurityConfig(JwtAuthenticationFilter jwtFilter, UserDetailsServiceImpl userDetailsService) { 
    this.jwtFilter = jwtFilter; 
    this.userDetailsService = userDetailsService; 
} 
 
@Bean 
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception { 
    http 
        // 1) Absolutely disable CSRF for stateless JWT 
        .csrf(csrf -> csrf.disable()) 
 
        // 2) Stateless session 
        .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) 
 
        // 3) Authorization rules 
        .authorizeHttpRequests(auth -> auth 
            // Permit authentication & registration endpoints 
            .requestMatchers("/auth/login", "/auth/register").permitAll() 
            // (Optional) If you kept old /login path as well 
            .requestMatchers("/login").permitAll() 
            .requestMatchers("/employee/me", "/employee/skills").authenticated() 
            // Allow GET /skills for everyone (authenticated only)? Leave it authenticated unless you want public. 
            .requestMatchers(HttpMethod.GET, "/skills").permitAll() 
            .requestMatchers(HttpMethod.GET, "/employee/skills").permitAll() 
 
            // everything else requires auth 
            .anyRequest().authenticated() 
        ) 
 
        // 4) CORS (for browsers; Postman usually doesn't send Origin) 
        .cors(cors -> cors.configurationSource(corsConfigurationSource())) 
 
        // 5) Plug in JWT filter 
        .userDetailsService(userDetailsService); 
 
    http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class); 
 
    return http.build(); 
} 
 
@Bean 
public CorsConfigurationSource corsConfigurationSource() { 
    CorsConfiguration config = new CorsConfiguration(); 
    // For quick testing, allow all. In prod, restrict to your Angular origin(s). 
    config.setAllowedOrigins(List.of("*")); 
    config.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS")); 
    config.setAllowedHeaders(List.of("Authorization","Content-Type")); 
    config.setAllowCredentials(false); // keep false when using "*" 
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource(); 
    source.registerCorsConfiguration("/**", config); 
    return source; 
} 
 
@Bean 
public PasswordEncoder passwordEncoder(){ return new BCryptPasswordEncoder(); } 
 
@Bean 
public AuthenticationManager authenticationManager(AuthenticationConfiguration cfg) throws Exception { 
    return cfg.getAuthenticationManager(); 
} 
  

} 

 