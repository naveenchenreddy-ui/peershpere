package com.peersphere.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * What we send back when someone asks for a user's profile.
 *
 * KEY POINT: No password field here.
 * DTOs are our security boundary — we choose exactly
 * what data leaves the server. The User entity has a password
 * field but this DTO does not, so it can never leak.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileResponse {

    private Long id;
    private String fullName;
    private String email;
    private String bio;
    private String department;
    private String semester;
    private String profilePicture;
    private String role;
    private List<String> skills;
    private List<String> interests;
    private LocalDateTime createdAt;
}