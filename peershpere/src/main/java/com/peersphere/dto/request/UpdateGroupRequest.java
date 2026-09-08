package com.peersphere.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class UpdateGroupRequest {

    private String name;

    private String description;

    private String subject;

    @Min(value = 2, message = "Group must allow at least 2 members")
    @Max(value = 50, message = "Group cannot exceed 50 members")
    private Integer maxMembers;

    private Boolean isActive;
}