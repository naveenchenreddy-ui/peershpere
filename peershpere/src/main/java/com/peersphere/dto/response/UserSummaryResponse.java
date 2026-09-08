package com.peersphere.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * A lightweight version of the profile — used in search results
 * and peer recommendations where we don't need every field.
 * Sending less data = faster responses.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserSummaryResponse {

    private Long id;
    private String fullName;
    private String email;
    private String department;
    private String semester;
    private String profilePicture;
    private List<String> skills;
    private List<String> interests;
}