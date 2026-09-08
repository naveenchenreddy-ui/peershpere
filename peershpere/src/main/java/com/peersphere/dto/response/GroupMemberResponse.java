package com.peersphere.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Represents one member inside a group response.
 * We don't embed the full UserProfileResponse here —
 * only what's useful in the context of a group.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupMemberResponse {

    private Long userId;
    private String fullName;
    private String email;
    private String department;
    private String profilePicture;
    private String role;       // ADMIN or MEMBER
    private LocalDateTime joinedAt;
}