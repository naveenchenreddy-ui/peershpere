package com.peersphere.dto.request;

import com.peersphere.entity.ResourceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateResourceRequest {

    @NotBlank(message = "Resource title is required")
    private String title;

    private String description;

    @NotBlank(message = "URL is required")
    private String url;

    @NotNull(message = "Resource type is required")
    private ResourceType resourceType;

    private String tags;

    private Long groupId; // optional
}