package com.peersphere.dto.request;

import com.peersphere.entity.ResourceType;
import lombok.Data;

@Data
public class UpdateResourceRequest {
    private String title;
    private String description;
    private String tags;
    private ResourceType resourceType;
}