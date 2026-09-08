package com.peersphere.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PostCommentRequest {

    @NotBlank(message = "Comment cannot be empty")
    @Size(max = 500, message = "Comment cannot exceed 500 characters")
    private String body;
}