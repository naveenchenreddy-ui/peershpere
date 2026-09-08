package com.peersphere.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateGroupRequest {

    @NotBlank(message = "Group name is required")
    private String name;

    private String description;

    @NotBlank(message = "Subject is required")
    private String subject;

    @NotNull(message = "Maximum members is required")
    @Min(value = 2, message = "Group must allow at least 2 members")
    @Max(value = 50, message = "Group cannot exceed 50 members")
    private Integer maxMembers;
}