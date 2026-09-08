package com.peersphere.dto.response;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProgressEntryResponse {
    private Long id;
    private String topicName;
    private String description;
    private Integer studyMinutes;
    private LocalDate studyDate;
    private String difficulty;
    private Long groupId;
    private String groupName;
    private LocalDateTime createdAt;
}