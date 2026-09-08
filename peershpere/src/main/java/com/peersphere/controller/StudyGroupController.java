package com.peersphere.controller;

import com.peersphere.dto.request.CreateGroupRequest;
import com.peersphere.dto.request.UpdateGroupRequest;
import com.peersphere.dto.response.StudyGroupResponse;
import com.peersphere.dto.response.StudyGroupSummaryResponse;
import com.peersphere.service.StudyGroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class StudyGroupController {

    private final StudyGroupService studyGroupService;

    @PostMapping
    public ResponseEntity<StudyGroupResponse> createGroup(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CreateGroupRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(studyGroupService.createGroup(userDetails.getUsername(), request));
    }

    @GetMapping("/{groupId}")
    public ResponseEntity<StudyGroupResponse> getGroupById(@PathVariable Long groupId) {
        return ResponseEntity.ok(studyGroupService.getGroupById(groupId));
    }

    @GetMapping
    public ResponseEntity<List<StudyGroupSummaryResponse>> getAllActiveGroups() {
        return ResponseEntity.ok(studyGroupService.getAllActiveGroups());
    }

    @GetMapping("/search")
    public ResponseEntity<List<StudyGroupSummaryResponse>> searchGroups(
            @RequestParam String keyword) {
        return ResponseEntity.ok(studyGroupService.searchGroups(keyword));
    }

    @GetMapping("/my-groups")
    public ResponseEntity<List<StudyGroupSummaryResponse>> getMyGroups(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(studyGroupService.getMyGroups(userDetails.getUsername()));
    }

    @PostMapping("/{groupId}/join")
    public ResponseEntity<StudyGroupResponse> joinGroup(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long groupId) {
        return ResponseEntity.ok(studyGroupService.joinGroup(userDetails.getUsername(), groupId));
    }

    @DeleteMapping("/{groupId}/leave")
    public ResponseEntity<Void> leaveGroup(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long groupId) {
        studyGroupService.leaveGroup(userDetails.getUsername(), groupId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{groupId}")
    public ResponseEntity<StudyGroupResponse> updateGroup(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long groupId,
            @Valid @RequestBody UpdateGroupRequest request) {
        return ResponseEntity.ok(
                studyGroupService.updateGroup(userDetails.getUsername(), groupId, request));
    }

    @DeleteMapping("/{groupId}")
    public ResponseEntity<Void> deleteGroup(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long groupId) {
        studyGroupService.deleteGroup(userDetails.getUsername(), groupId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{groupId}/members/{userId}")
    public ResponseEntity<Void> removeMember(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long groupId,
            @PathVariable Long userId) {
        studyGroupService.removeMember(userDetails.getUsername(), groupId, userId);
        return ResponseEntity.noContent().build();
    }
}