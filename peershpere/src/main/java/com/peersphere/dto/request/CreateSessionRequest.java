package com.peersphere.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateSessionRequest {

    @NotBlank(message = "Session title is required")
    private String title;

    private String description;

    /**
     * @Future — Spring validation annotation.
     * Automatically rejects any date that is not in the future.
     * This saves us from writing that check manually in the service.
     */
    @NotNull(message = "Scheduled time is required")
    @Future(message = "Session must be scheduled for a future date and time")
    private LocalDateTime scheduledAt;

    @NotNull(message = "Duration is required")
    @Min(value = 15, message = "Session must be at least 15 minutes")
    @Max(value = 480, message = "Session cannot exceed 8 hours (480 minutes)")
    private Integer durationMinutes;

    private String platform;

    private String meetingLink;

    @NotNull(message = "Group ID is required")
    private Long groupId;
}