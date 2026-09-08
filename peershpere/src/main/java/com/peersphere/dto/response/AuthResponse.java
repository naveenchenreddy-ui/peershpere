package com.peersphere.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This is what we send BACK to the client after successful login/register.
 * Notice — no password field. The client only needs the token and basic info.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {

    private String token;        // JWT token — client stores this
    private String email;
    private String fullName;
    private String role;
    private String message;      // e.g. "Registration successful"
}