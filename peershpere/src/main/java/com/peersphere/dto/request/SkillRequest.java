package com.peersphere.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Used for both adding a skill and adding an interest —
 * the payload shape is identical: a single string value.
 * Reusing one DTO for both keeps the codebase smaller.
 */
@Data
public class SkillRequest {

    @NotBlank(message = "Value cannot be empty")
    private String value;
}