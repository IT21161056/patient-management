package com.pm.authservice.service;

import com.pm.authservice.dto.LoginRequestDTO;
import com.pm.authservice.util.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserService userService, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public Optional<String> authenticate(LoginRequestDTO loginRequestDTO) {
        // Step 1: Start with an Optional of User, obtained by email lookup
        Optional<String> token = userService
                .findByEmail(loginRequestDTO.getEmail()) // Returns Optional<User> from database

                // Step 2: Filter the Optional - this only continues if user exists
                .filter(u -> // 'u' is the User object from the Optional (if present)
                        // Step 3: Verify the provided password matches the stored hash
                        // If passwords don't match, filter() returns empty Optional (short-circuits)
                        passwordEncoder.matches(loginRequestDTO.getPassword(), u.getPassword())
                ) // After filter: Optional<User> if user exists AND password matches, else Optional.empty()

                // Step 4: Transform User to JWT token (only executes if filter passed)
                .map(u -> // 'u' is the now-validated User object
                        // Step 5: Generate JWT token containing user's email and role
                        jwtUtil.generateToken(u.getEmail(), u.getRole())
                ); // Result: Optional<String> containing token if auth successful, else Optional.empty()

        // Step 6: Return the Optional result (either token or empty)
        return token;
    }
}
