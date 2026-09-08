package com.peersphere.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudyGroupResponse {

    private Long id;
    private String name;
    private String description;
    private String subject;
    private Integer maxMembers;
    private Integer currentMemberCount;  // computed — not stored in DB
    private Boolean isActive;
    private String createdByName;        // just the name, not the full User object
    private Long createdById;
    private List<GroupMemberResponse> members;
    private LocalDateTime createdAt;
}