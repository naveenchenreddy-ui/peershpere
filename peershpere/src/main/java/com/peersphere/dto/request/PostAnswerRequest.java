package com.peersphere.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PostAnswerRequest {

    @NotBlank(message = "Answer body is required")
    private String body;
}