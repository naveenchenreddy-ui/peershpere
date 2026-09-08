package com.peersphere.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * Used when a user wants to update their profile.
 * Notice — no email or password here.
 * Email changes and password changes are separate sensitive operations.
 * We keep profile updates simple and safe.
 */
@Data
public class UpdateProfileRequest {

    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    private String fullName;

    @Size(max = 500, message = "Bio cannot exceed 500 characters")
    private String bio;

    private String department;

    private String semester;

    private String profilePicture;

    // Lists can be null (not updating) or empty (clearing all)

}