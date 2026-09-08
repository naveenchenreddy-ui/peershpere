package com.peersphere.service.impl;

import com.peersphere.dto.request.CreateSessionRequest;
import com.peersphere.dto.request.UpdateSessionRequest;
import com.peersphere.dto.response.StudySessionResponse;
import com.peersphere.entity.*;
import com.peersphere.exception.ResourceNotFoundException;
import com.peersphere.exception.UnauthorizedException;
import com.peersphere.repository.GroupMemberRepository;
import com.peersphere.repository.StudyGroupRepository;
import com.peersphere.repository.StudySessionRepository;
import com.peersphere.repository.UserRepository;
import com.peersphere.service.StudySessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StudySessionServiceImpl implements StudySessionService {

    private final StudySessionRepository sessionRepository;
    private final StudyGroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public StudySessionResponse createSession(String email, CreateSessionRequest request) {
        User organizer = getUserByEmail(email);
        StudyGroup group = getGroupOrThrow(request.getGroupId());

        // Rule 1 — Only group members can create sessions
        boolean isMember = groupMemberRepository.existsByStudyGroupIdAndUserId(
                group.getId(), organizer.getId());

        if (!isMember) {
            throw new UnauthorizedException(
                    "You must be a member of this group to schedule a session"
            );
        }

        // Rule 2 — Group must be active
        if (!group.getIsActive()) {
            throw new IllegalArgumentException("Cannot schedule a session for an inactive group");
        }

        // Note: @Future on the DTO already validates scheduledAt is in the future
        // We don't need to check it manually here — Spring Validation handles it

        StudySession session = StudySession.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .scheduledAt(request.getScheduledAt())
                .durationMinutes(request.getDurationMinutes())
                .platform(request.getPlatform())
                .meetingLink(request.getMeetingLink())
                .status(SessionStatus.SCHEDULED)
                .studyGroup(group)
                .createdBy(organizer)
                .build();

        return mapToResponse(sessionRepository.save(session));
    }

    @Override
    public StudySessionResponse getSessionById(Long sessionId) {
        return mapToResponse(getSessionOrThrow(sessionId));
    }

    @Override
    public List<StudySessionResponse> getSessionsByGroup(Long groupId) {
        getGroupOrThrow(groupId); // verify group exists first
        return sessionRepository
                .findByStudyGroupIdOrderByScheduledAtAsc(groupId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<StudySessionResponse> getUpcomingSessionsByGroup(Long groupId) {
        getGroupOrThrow(groupId);
        return sessionRepository
                .findByStudyGroupIdAndStatusOrderByScheduledAtAsc(
                        groupId, SessionStatus.SCHEDULED)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<StudySessionResponse> getMyUpcomingSessions(String email) {
        User user = getUserByEmail(email);

        /**
         * We pass LocalDateTime.now() into the query so the database
         * filters out sessions that have already passed.
         * This is more reliable than filtering in Java after fetching —
         * less data transferred, faster response.
         */
        return sessionRepository
                .findUpcomingSessionsForUser(user.getId(), LocalDateTime.now())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public StudySessionResponse updateSession(
            String email, Long sessionId, UpdateSessionRequest request) {

        User requester = getUserByEmail(email);
        StudySession session = getSessionOrThrow(sessionId);

        // Rule — only the organizer can edit
        verifyOrganizer(session, requester.getId());

        // Rule — cannot edit cancelled or completed sessions
        verifySessionIsEditable(session);

        // Partial update — only change fields that were sent
        if (request.getTitle() != null)           session.setTitle(request.getTitle());
        if (request.getDescription() != null)     session.setDescription(request.getDescription());
        if (request.getScheduledAt() != null)     session.setScheduledAt(request.getScheduledAt());
        if (request.getDurationMinutes() != null) session.setDurationMinutes(request.getDurationMinutes());
        if (request.getPlatform() != null)        session.setPlatform(request.getPlatform());
        if (request.getMeetingLink() != null)     session.setMeetingLink(request.getMeetingLink());

        return mapToResponse(sessionRepository.save(session));
    }

    @Override
    @Transactional
    public void cancelSession(String email, Long sessionId) {
        User requester = getUserByEmail(email);
        StudySession session = getSessionOrThrow(sessionId);

        verifyOrganizer(session, requester.getId());
        verifySessionIsEditable(session);

        session.setStatus(SessionStatus.CANCELLED);
        sessionRepository.save(session);

        /**
         * We save with CANCELLED status but do NOT delete the row.
         * Reasons:
         * 1. Progress tracker may reference past sessions
         * 2. Audit trail — students can see the session was cancelled
         * 3. Notifications — Module 9 will send "session cancelled" alerts
         */
    }

    @Override
    @Transactional
    public StudySessionResponse markAsCompleted(String email, Long sessionId) {
        User requester = getUserByEmail(email);
        StudySession session = getSessionOrThrow(sessionId);

        verifyOrganizer(session, requester.getId());
        verifySessionIsEditable(session);

        session.setStatus(SessionStatus.COMPLETED);
        return mapToResponse(sessionRepository.save(session));
    }

    // ── Private helpers ────────────────────────────────────────────

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private StudyGroup getGroupOrThrow(Long groupId) {
        return groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Study group not found with id: " + groupId));
    }

    private StudySession getSessionOrThrow(Long sessionId) {
        return sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Session not found with id: " + sessionId));
    }

    /**
     * Checks the logged-in user is the one who created this session.
     * If not, throws 403 immediately — the rest of the method never runs.
     */
    private void verifyOrganizer(StudySession session, Long userId) {
        if (!session.getCreatedBy().getId().equals(userId)) {
            throw new UnauthorizedException(
                    "Only the session organizer can perform this action"
            );
        }
    }

    /**
     * A session can only be modified if it is still SCHEDULED.
     * CANCELLED and COMPLETED are terminal states — no going back.
     */
    private void verifySessionIsEditable(StudySession session) {
        if (session.getStatus() == SessionStatus.CANCELLED) {
            throw new IllegalArgumentException("Cannot modify a cancelled session");
        }
        if (session.getStatus() == SessionStatus.COMPLETED) {
            throw new IllegalArgumentException("Cannot modify a completed session");
        }
    }

    private StudySessionResponse mapToResponse(StudySession session) {
        return StudySessionResponse.builder()
                .id(session.getId())
                .title(session.getTitle())
                .description(session.getDescription())
                .scheduledAt(session.getScheduledAt())
                .durationMinutes(session.getDurationMinutes())
                .platform(session.getPlatform())
                .meetingLink(session.getMeetingLink())
                .status(session.getStatus().name())
                .groupId(session.getStudyGroup().getId())
                .groupName(session.getStudyGroup().getName())
                .groupSubject(session.getStudyGroup().getSubject())
                .createdById(session.getCreatedBy().getId())
                .createdByName(session.getCreatedBy().getFullName())
                .createdAt(session.getCreatedAt())
                .build();
    }
}