package com.peersphere.dto.response;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuestionResponse {
    private Long id;
    private String title;
    private String body;
    private String tags;
    private Integer upvoteCount;
    private Integer answerCount;
    private Boolean isSolved;
    private Long askedById;
    private String askedByName;
    private String askedByDepartment;
    private Long groupId;
    private String groupName;
    private boolean upvotedByCurrentUser;
    private List<AnswerResponse> answers;
    private List<CommentResponse> comments;
    private LocalDateTime createdAt;
}