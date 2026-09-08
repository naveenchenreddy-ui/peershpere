package com.peersphere.dto.response;

import lombok.*;
import java.util.List;

/**
 * Wraps a UserSummaryResponse with an extra field — the compatibility score.
 * The score tells the frontend how strong the match is.
 * Frontend can show it as "95% match" or just use it for ordering.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PeerRecommendationResponse {

    private Long userId;
    private String fullName;
    private String email;
    private String department;
    private String semester;
    private String profilePicture;
    private List<String> skills;
    private List<String> interests;

    // What they have in common with the current user
    private List<String> commonInterests;
    private List<String> commonSkills;
    private boolean sameGroup;
    private boolean sameDepartment;

    // Overall compatibility score — higher is better match
    private int compatibilityScore;

    // Human-readable reason for the recommendation
    private String matchReason;
}