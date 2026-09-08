package com.peersphere.repository;

import com.peersphere.entity.SessionStatus;
import com.peersphere.entity.StudySession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StudySessionRepository extends JpaRepository<StudySession, Long> {

    /**
     * Get all sessions for a specific group.
     * Ordered by scheduledAt so upcoming sessions appear first.
     */
    List<StudySession> findByStudyGroupIdOrderByScheduledAtAsc(Long groupId);

    /**
     * Get sessions for a specific group filtered by status.
     * e.g. only SCHEDULED sessions for "Upcoming" view.
     */
    List<StudySession> findByStudyGroupIdAndStatusOrderByScheduledAtAsc(
            Long groupId, SessionStatus status);

    /**
     * Dashboard query — find all SCHEDULED sessions for groups
     * the user belongs to, that haven't happened yet.
     *
     * This is a JOIN query:
     * - Start from StudySession
     * - Join to GroupMember on matching group_id
     * - Filter to sessions where the user is a member
     * - Only future sessions with SCHEDULED status
     */
    @Query("SELECT s FROM StudySession s " +
            "JOIN GroupMember gm ON gm.studyGroup.id = s.studyGroup.id " +
            "WHERE gm.user.id = :userId " +
            "AND s.status = 'SCHEDULED' " +
            "AND s.scheduledAt > :now " +
            "ORDER BY s.scheduledAt ASC")
    List<StudySession> findUpcomingSessionsForUser(
            @Param("userId") Long userId,
            @Param("now") LocalDateTime now);

    /**
     * Sessions created by a specific user.
     * Useful for "Sessions I organised" view.
     */
    List<StudySession> findByCreatedByIdOrderByScheduledAtDesc(Long userId);
}