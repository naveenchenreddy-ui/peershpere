package com.peersphere.dto.response;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AnswerResponse {
    private Long id;
    private String body;
    private Integer upvoteCount;
    private Boolean isAccepted;
    private Long answeredById;
    private String answeredByName;
    private String answeredByDepartment;
    private boolean upvotedByCurrentUser;
    private List<CommentResponse> comments;
    private LocalDateTime createdAt;
}