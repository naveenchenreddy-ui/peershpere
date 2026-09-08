package com.peersphere.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudySessionResponse {

    private Long id;
    private String title;
    private String description;
    private LocalDateTime scheduledAt;
    private Integer durationMinutes;
    private String platform;
    private String meetingLink;
    private String status;          // "SCHEDULED", "CANCELLED", "COMPLETED"

    // Group info — just enough context without embedding the whole group
    private Long groupId;
    private String groupName;
    private String groupSubject;

    // Organizer info
    private Long createdById;
    private String createdByName;

    private LocalDateTime createdAt;
}