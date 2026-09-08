package com.peersphere.service;

import com.peersphere.dto.request.CreateSessionRequest;
import com.peersphere.dto.request.UpdateSessionRequest;
import com.peersphere.dto.response.StudySessionResponse;

import java.util.List;

public interface StudySessionService {

    // Create a new session inside a group
    StudySessionResponse createSession(String email, CreateSessionRequest request);

    // Get one session by ID
    StudySessionResponse getSessionById(Long sessionId);

    // Get all sessions for a specific group
    List<StudySessionResponse> getSessionsByGroup(Long groupId);

    // Get only SCHEDULED upcoming sessions for a group
    List<StudySessionResponse> getUpcomingSessionsByGroup(Long groupId);

    // Dashboard — get all upcoming sessions across all groups the user belongs to
    List<StudySessionResponse> getMyUpcomingSessions(String email);

    // Update session details — organizer only
    StudySessionResponse updateSession(String email, Long sessionId, UpdateSessionRequest request);

    // Cancel a session — organizer only
    void cancelSession(String email, Long sessionId);

    // Mark session as completed — organizer only
    StudySessionResponse markAsCompleted(String email, Long sessionId);
}