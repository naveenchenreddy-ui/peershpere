package com.peersphere.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Standard error response sent to clients when something goes wrong.
 * Every error in the API will follow this structure — consistent JSON.
 */
@Data
@Builder
public class ApiError {

    private int status;          // HTTP status code (400, 401, 404, 500...)
    private String message;      // Human-readable error message
    private List<String> errors; // Field-level validation errors (if any)

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
}