package com.peersphere.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Lightweight version — used for lists and search results.
 * No member list here — loading all members for every group
 * in a list of 50 groups would be extremely slow (N+1 problem).
 * We only load member details when viewing one specific group.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudyGroupSummaryResponse {

    private Long id;
    private String name;
    private String subject;
    private String description;
    private Integer maxMembers;
    private Integer currentMemberCount;
    private Boolean isActive;
    private String createdByName;
    private LocalDateTime createdAt;
}