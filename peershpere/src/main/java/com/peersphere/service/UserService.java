package com.peersphere.service;

import com.peersphere.dto.request.UpdateProfileRequest;
import com.peersphere.dto.response.UserProfileResponse;
import com.peersphere.dto.response.UserSummaryResponse;

import java.util.List;

public interface UserService {

    UserProfileResponse getMyProfile(String email);

    UserProfileResponse getUserById(Long id);

    UserProfileResponse updateProfile(String email, UpdateProfileRequest request);

    List<UserSummaryResponse> searchUsers(String keyword);

    void deleteAccount(String email);

    // ── New methods for Module 3 (Option B) ──────────────────────
    UserProfileResponse addSkill(String email, String skill);

    UserProfileResponse removeSkill(String email, String skill);

    UserProfileResponse addInterest(String email, String interest);

    UserProfileResponse removeInterest(String email, String interest);
}