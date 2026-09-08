package com.peersphere.service.impl;

import com.peersphere.dto.request.LoginRequest;
import com.peersphere.dto.request.RegisterRequest;
import com.peersphere.dto.response.AuthResponse;
import com.peersphere.entity.Role;
import com.peersphere.entity.User;
import com.peersphere.exception.ResourceNotFoundException;
import com.peersphere.repository.UserRepository;
import com.peersphere.security.JwtService;
import com.peersphere.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * @Service — marks this as a Spring service component.
 * @RequiredArgsConstructor — Lombok generates a constructor with all
 * final fields, which Spring uses for dependency injection.
 * This replaces @Autowired on every field.
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthResponse register(RegisterRequest request) {

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email is already registered");
        }

        // Build a new User entity from the request DTO
        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                // NEVER store plain text — always encode the password
                .password(passwordEncoder.encode(request.getPassword()))
                .department(request.getDepartment())
                .semester(request.getSemester())
                .role(Role.ROLE_USER) // all new users start as regular users
                .build();

        // Save to database — @PrePersist sets createdAt automatically
        userRepository.save(user);

        // Generate a JWT token for immediate login after registration
        String token = jwtService.generateToken(user);

        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole().name())
                .message("Registration successful! Welcome to PeerSphere.")
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {

        /**
         * authenticate() does two things automatically:
         * 1. Loads the user from DB using UserDetailsService (by email)
         * 2. Checks the raw password against the BCrypt hash
         * If credentials are wrong, it throws BadCredentialsException.
         */
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // If we reach here, authentication succeeded
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String token = jwtService.generateToken(user);

        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole().name())
                .message("Login successful!")
                .build();
    }
}