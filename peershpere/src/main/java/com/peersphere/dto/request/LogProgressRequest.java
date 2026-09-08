package com.peersphere.dto.request;

import com.peersphere.entity.DifficultyLevel;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

@Data
public class LogProgressRequest {

    @NotBlank(message = "Topic name is required")
    private String topicName;

    private String description;

    @NotNull(message = "Study duration is required")
    @Min(value = 1, message = "Study duration must be at least 1 minute")
    @Max(value = 720, message = "Study duration cannot exceed 12 hours (720 minutes)")
    private Integer studyMinutes;

    @NotNull(message = "Study date is required")
    @PastOrPresent(message = "Study date cannot be in the future")
    private LocalDate studyDate;

    @NotNull(message = "Difficulty level is required")
    private DifficultyLevel difficulty;

    private Long groupId; // optional context
}