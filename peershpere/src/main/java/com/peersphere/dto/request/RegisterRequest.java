package com.peersphere.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * This DTO carries the data a user sends when registering.
 * @NotBlank — field cannot be null or empty string
 * @Email — validates email format (must contain @ etc.)
 * @Size — enforces min/max character limits
 *
 * These annotations work with Spring's @Valid annotation in the controller.
 * If validation fails, our GlobalExceptionHandler catches it automatically.
 */
@Data
public class RegisterRequest {

    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    private String department;

    private String semester;
}