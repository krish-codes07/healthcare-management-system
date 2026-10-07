package com.codehunters.healthcare.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .authorizeHttpRequests(auth -> auth
        .requestMatchers("/", "/login", "/emergency", "/emergency/**", "/css/**", "/js/**", "/images/**").permitAll()            .requestMatchers("/admin/**").hasRole("ADMIN")
            .requestMatchers("/doctor/**").hasRole("DOCTOR")
            .requestMatchers("/patient/**").hasRole("PATIENT")
            .anyRequest().authenticated()
        )
        .formLogin(form -> form
            .loginPage("/login")
            .successHandler((request, response, authentication) -> {
                String role = authentication.getAuthorities().iterator().next().getAuthority();
                if (role.equals("ROLE_ADMIN")) {
                    response.sendRedirect("/admin");
                } else if (role.equals("ROLE_DOCTOR")) {
                    response.sendRedirect("/doctor");
                } else {
                    response.sendRedirect("/patient");
                }
            })
            .permitAll()
        );
    return http.build();
}
}