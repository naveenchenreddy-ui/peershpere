package com.peersphere.controller;

import com.peersphere.dto.request.CreateSessionRequest;
import com.peersphere.dto.request.UpdateSessionRequest;
import com.peersphere.dto.response.StudySessionResponse;
import com.peersphere.service.StudySessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
public class StudySessionController {

    private final StudySessionService sessionService;

    /**
     * POST /api/sessions
     * Schedule a new session.
     * groupId is inside the request body, not the URL —
     * because we're creating a new resource, not operating on an existing one.
     */
    @PostMapping
    public ResponseEntity<StudySessionResponse> createSession(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CreateSessionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(sessionService.createSession(userDetails.getUsername(), request));
    }

    @GetMapping("/{sessionId}")
    public ResponseEntity<StudySessionResponse> getSessionById(
            @PathVariable Long sessionId) {
        return ResponseEntity.ok(sessionService.getSessionById(sessionId));
    }

    /**
     * GET /api/sessions/group/{groupId}
     * All sessions (all statuses) for a group — for group history view.
     */
    @GetMapping("/group/{groupId}")
    public ResponseEntity<List<StudySessionResponse>> getSessionsByGroup(
            @PathVariable Long groupId) {
        return ResponseEntity.ok(sessionService.getSessionsByGroup(groupId));
    }

    /**
     * GET /api/sessions/group/{groupId}/upcoming
     * Only SCHEDULED future sessions — for group's upcoming sessions list.
     */
    @GetMapping("/group/{groupId}/upcoming")
    public ResponseEntity<List<StudySessionResponse>> getUpcomingSessionsByGroup(
            @PathVariable Long groupId) {
        return ResponseEntity.ok(sessionService.getUpcomingSessionsByGroup(groupId));
    }

    /**
     * GET /api/sessions/my-upcoming
     * Dashboard endpoint — all upcoming sessions across ALL the user's groups.
     * This is what powers the "Upcoming Sessions" widget on the dashboard.
     */
    @GetMapping("/my-upcoming")
    public ResponseEntity<List<StudySessionResponse>> getMyUpcomingSessions(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                sessionService.getMyUpcomingSessions(userDetails.getUsername()));
    }

    @PutMapping("/{sessionId}")
    public ResponseEntity<StudySessionResponse> updateSession(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long sessionId,
            @Valid @RequestBody UpdateSessionRequest request) {
        return ResponseEntity.ok(
                sessionService.updateSession(
                        userDetails.getUsername(), sessionId, request));
    }

    /**
     * PATCH /api/sessions/{sessionId}/cancel
     * PATCH is used here because we're partially updating the resource
     * (only the status field changes, nothing else).
     * Returns 204 No Content — cancelled, nothing to show.
     */
    @PatchMapping("/{sessionId}/cancel")
    public ResponseEntity<Void> cancelSession(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long sessionId) {
        sessionService.cancelSession(userDetails.getUsername(), sessionId);
        return ResponseEntity.noContent().build();
    }

    /**
     * PATCH /api/sessions/{sessionId}/complete
     * Marks session as completed — only organizer can do this.
     */
    @PatchMapping("/{sessionId}/complete")
    public ResponseEntity<StudySessionResponse> markAsCompleted(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long sessionId) {
        return ResponseEntity.ok(
                sessionService.markAsCompleted(userDetails.getUsername(), sessionId));
    }
}