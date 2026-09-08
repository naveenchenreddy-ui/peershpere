package com.peersphere.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponse {
    private Long id;
    private String body;
    private Long commentedById;
    private String commentedByName;
    private LocalDateTime createdAt;
}