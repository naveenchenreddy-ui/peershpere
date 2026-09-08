package com.peersphere.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AskQuestionRequest {

    @NotBlank(message = "Question title is required")
    private String title;

    @NotBlank(message = "Question body is required")
    private String body;

    private String tags;

    private Long groupId; // optional
}