package com.peersphere.controller;

import com.peersphere.dto.request.LoginRequest;
import com.peersphere.dto.request.RegisterRequest;
import com.peersphere.dto.response.AuthResponse;
import com.peersphere.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @RestController — combines @Controller + @ResponseBody.
 * Every method automatically serializes the return value to JSON.
 *
 * @RequestMapping — all routes in this class start with /api/auth
 * So our endpoints become:
 *   POST /api/auth/register
 *   POST /api/auth/login
 *
 * @Valid — triggers validation of the DTO fields (@NotBlank, @Email etc.)
 * If validation fails, GlobalExceptionHandler sends a 400 response.
 *
 * ResponseEntity — gives us full control over the HTTP response:
 * status code, headers, and body. Clean and explicit.
 */
@RestController
@CrossOrigin(origins = "http://localhost:63342")
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        // 201 Created — the correct status for resource creation
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        // 200 OK — successful login
        return ResponseEntity.ok(response);
    }
}