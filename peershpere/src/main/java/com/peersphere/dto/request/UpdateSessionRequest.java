package com.peersphere.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * All fields optional — only send what you want to change.
 * Same partial-update pattern we used for user profile.
 */
@Data
public class UpdateSessionRequest {

    private String title;

    private String description;

    @Future(message = "Session must be scheduled for a future date and time")
    private LocalDateTime scheduledAt;

    @Min(value = 15, message = "Session must be at least 15 minutes")
    @Max(value = 480, message = "Session cannot exceed 8 hours")
    private Integer durationMinutes;

    private String platform;

    private String meetingLink;
}