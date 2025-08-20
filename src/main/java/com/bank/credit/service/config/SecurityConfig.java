package com.bank.credit.service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableMethodSecurity     // Enables @PreAuthorize, @Secured, etc.
public class SecurityConfig {


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Disable CSRF (important for H2 and Postman)
                .headers(headers -> headers.frameOptions().disable()) // Allow H2 console to be embedded in a frame
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/h2-console/**").permitAll() // Allow H2 console
                        .anyRequest().hasRole("ADMIN") // Protect all other endpoints
                )
                .httpBasic(Customizer.withDefaults()); // Enable Basic Auth

        return http.build();
    }
}






