package com.peersphere.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResourceResponse {

    private Long id;
    private String title;
    private String description;
    private String url;
    private String resourceType;
    private String tags;
    private Integer viewCount;
    private Integer likeCount;

    private Long sharedById;
    private String sharedByName;

    private Long groupId;
    private String groupName;

    private boolean likedByCurrentUser;
    private boolean bookmarkedByCurrentUser;

    private LocalDateTime createdAt;
}