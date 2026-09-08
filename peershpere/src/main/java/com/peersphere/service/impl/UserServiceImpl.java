package com.peersphere.service.impl;

import com.peersphere.dto.request.UpdateProfileRequest;
import com.peersphere.dto.response.UserProfileResponse;
import com.peersphere.dto.response.UserSummaryResponse;
import com.peersphere.entity.User;
import com.peersphere.exception.ResourceNotFoundException;
import com.peersphere.repository.UserRepository;
import com.peersphere.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    /**
     * Gets the profile of whoever is currently logged in.
     * We identify them by their email (extracted from JWT in the controller).
     */
    @Override
    public UserProfileResponse getMyProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return mapToProfileResponse(user);
    }

    /**
     * Gets any user's profile by ID — used when viewing another student's profile.
     */
    @Override
    public UserProfileResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return mapToProfileResponse(user);
    }

    /**
     * @Transactional — wraps the entire method in a database transaction.
     * If anything fails midway, ALL changes are rolled back automatically.
     * Essential when modifying data — you never want a half-saved record.
     */
    @Override
    @Transactional
    public UserProfileResponse updateProfile(String email, UpdateProfileRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }
        if (request.getBio() != null) {
            user.setBio(request.getBio());
        }
        if (request.getDepartment() != null) {
            user.setDepartment(request.getDepartment());
        }
        if (request.getSemester() != null) {
            user.setSemester(request.getSemester());
        }
        if (request.getProfilePicture() != null) {
            user.setProfilePicture(request.getProfilePicture());
        }
        // skills and interests removed from here — they have their own endpoints now

        User savedUser = userRepository.save(user);
        return mapToProfileResponse(savedUser);
    }
    /**
     * Adds a single skill to the user's existing list.
     * Checks for duplicates first — no point storing "Java" twice.
     *
     * @Transactional ensures this read-modify-save sequence happens
     * as one atomic database operation.
     */
    @Override
    @Transactional
    public UserProfileResponse addSkill(String email, String skill) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Case-insensitive duplicate check — "java" and "Java" should count as the same skill
        boolean alreadyExists = user.getSkills().stream()
                .anyMatch(existing -> existing.equalsIgnoreCase(skill));

        if (!alreadyExists) {
            user.getSkills().add(skill);
        }

        User savedUser = userRepository.save(user);
        return mapToProfileResponse(savedUser);
    }

    /**
     * Removes a single skill from the user's list.
     * If the skill doesn't exist, this silently does nothing —
     * that's intentional, removing something that's already gone
     * shouldn't be treated as an error (idempotent DELETE).
     */
    @Override
    @Transactional
    public UserProfileResponse removeSkill(String email, String skill) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.getSkills().removeIf(existing -> existing.equalsIgnoreCase(skill));

        User savedUser = userRepository.save(user);
        return mapToProfileResponse(savedUser);
    }

    @Override
    @Transactional
    public UserProfileResponse addInterest(String email, String interest) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        boolean alreadyExists = user.getInterests().stream()
                .anyMatch(existing -> existing.equalsIgnoreCase(interest));

        if (!alreadyExists) {
            user.getInterests().add(interest);
        }

        User savedUser = userRepository.save(user);
        return mapToProfileResponse(savedUser);
    }

    @Override
    @Transactional
    public UserProfileResponse removeInterest(String email, String interest) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.getInterests().removeIf(existing -> existing.equalsIgnoreCase(interest));

        User savedUser = userRepository.save(user);
        return mapToProfileResponse(savedUser);
    }

    @Override
    public List<UserSummaryResponse> searchUsers(String keyword) {
        List<User> users = userRepository.searchByNameOrDepartment(keyword);
        return users.stream()
                .map(this::mapToSummaryResponse)
                .toList();
    }

    @Override
    @Transactional
    public void deleteAccount(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        userRepository.delete(user);
    }

    // ── Private helper methods ─────────────────────────────────────

    /**
     * Converts a User entity into a UserProfileResponse DTO.
     * This mapping is the core of the DTO pattern —
     * we choose exactly which fields to expose.
     *
     * @Builder (from Lombok) lets us construct objects cleanly
     * without a long constructor call.
     */
    private UserProfileResponse mapToProfileResponse(User user) {
        return UserProfileResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .bio(user.getBio())
                .department(user.getDepartment())
                .semester(user.getSemester())
                .profilePicture(user.getProfilePicture())
                .role(user.getRole().name())
                .skills(user.getSkills())
                .interests(user.getInterests())
                .createdAt(user.getCreatedAt())
                .build();
    }

    private UserSummaryResponse mapToSummaryResponse(User user) {
        return UserSummaryResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .department(user.getDepartment())
                .semester(user.getSemester())
                .profilePicture(user.getProfilePicture())
                .skills(user.getSkills())
                .interests(user.getInterests())
                .build();
    }
}