package com.peersphere.controller;
import com.peersphere.dto.request.SkillRequest;
import com.peersphere.dto.request.UpdateProfileRequest;
import com.peersphere.dto.response.UserProfileResponse;
import com.peersphere.dto.response.UserSummaryResponse;
import com.peersphere.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @AuthenticationPrincipal — this is the magic annotation that extracts
 * the currently logged-in user from the JWT token automatically.
 *
 * When our JwtAuthenticationFilter validates a token, it puts the user
 * into the SecurityContext. @AuthenticationPrincipal pulls it back out.
 *
 * So we never need to parse the token manually in the controller —
 * Spring Security does it for us.
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * GET /api/users/me
     * Returns the profile of whoever is currently logged in.
     * The JWT tells us who "me" is — no need to pass a user ID.
     */
    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getMyProfile(
            @AuthenticationPrincipal UserDetails userDetails) {
        UserProfileResponse profile = userService.getMyProfile(userDetails.getUsername());
        return ResponseEntity.ok(profile);
    }

    /**
     * GET /api/users/{id}
     * Returns any student's profile — for viewing peers.
     * @PathVariable extracts the {id} from the URL.
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserProfileResponse> getUserById(@PathVariable Long id) {
        UserProfileResponse profile = userService.getUserById(id);
        return ResponseEntity.ok(profile);
    }

    /**
     * PUT /api/users/me
     * Updates the logged-in user's profile.
     * PUT = replace/update an existing resource.
     */
    @PutMapping("/me")
    public ResponseEntity<UserProfileResponse> updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UpdateProfileRequest request) {
        UserProfileResponse updated = userService.updateProfile(
                userDetails.getUsername(), request);
        return ResponseEntity.ok(updated);
    }
    /**
     * POST /api/users/me/skills
     * Adds ONE skill to the logged-in user's profile.
     * POST is correct here because we're adding a new item to a
     * collection, not replacing the collection.
     */
    @PostMapping("/me/skills")
    public ResponseEntity<UserProfileResponse> addSkill(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody SkillRequest request) {
        UserProfileResponse updated = userService.addSkill(
                userDetails.getUsername(), request.getValue());
        return ResponseEntity.ok(updated);
    }

    /**
     * DELETE /api/users/me/skills/{skill}
     * Removes ONE skill by name.
     * The skill name is part of the URL path itself —
     * this is standard REST practice for identifying the resource to delete.
     */
    @DeleteMapping("/me/skills/{skill}")
    public ResponseEntity<UserProfileResponse> removeSkill(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String skill) {
        UserProfileResponse updated = userService.removeSkill(
                userDetails.getUsername(), skill);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/me/interests")
    public ResponseEntity<UserProfileResponse> addInterest(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody SkillRequest request) {
        UserProfileResponse updated = userService.addInterest(
                userDetails.getUsername(), request.getValue());
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/me/interests/{interest}")
    public ResponseEntity<UserProfileResponse> removeInterest(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String interest) {
        UserProfileResponse updated = userService.removeInterest(
                userDetails.getUsername(), interest);
        return ResponseEntity.ok(updated);
    }

    /**
     * GET /api/users/search?keyword=computer
     * Searches students by name or department.
     * @RequestParam reads the ?keyword= part of the URL.
     */
    @GetMapping("/search")
    public ResponseEntity<List<UserSummaryResponse>> searchUsers(
            @RequestParam String keyword) {
        List<UserSummaryResponse> results = userService.searchUsers(keyword);
        return ResponseEntity.ok(results);
    }

    /**
     * DELETE /api/users/me
     * Deletes the currently logged-in user's account.
     * Returns 204 No Content — successful but nothing to return.
     */
    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteAccount(
            @AuthenticationPrincipal UserDetails userDetails) {
        userService.deleteAccount(userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}