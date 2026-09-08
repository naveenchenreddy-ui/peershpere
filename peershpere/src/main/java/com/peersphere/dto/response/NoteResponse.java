package com.peersphere.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NoteResponse {

    private Long id;
    private String title;
    private String description;
    private String subject;
    private String fileUrl;
    private String fileType;
    private Long fileSizeKb;
    private Integer downloadCount;
    private Integer likeCount;
    private String tags;

    // Uploader info
    private Long uploadedById;
    private String uploadedByName;

    // Group info (null if public note)
    private Long groupId;
    private String groupName;

    // Computed fields — based on the logged-in user
    private boolean likedByCurrentUser;
    private boolean bookmarkedByCurrentUser;

    private LocalDateTime createdAt;
}