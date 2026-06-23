package com.pm.authservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll())
                .csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }

    // This is a Spring configuration method (indicated by @Bean annotation)
    // It creates and returns a PasswordEncoder instance that will be managed by Spring container
    @Bean
    public PasswordEncoder passwordEncoder(){
        // Creates a new instance of BCryptPasswordEncoder
        // BCrypt is a strong hashing algorithm specifically designed for passwords
        // It includes built-in salt generation and is resistant to brute-force attacks
        // The encoder handles both hashing passwords for storage and verifying plain-text
        // passwords against stored hashes
        return new BCryptPasswordEncoder();
    }
}
