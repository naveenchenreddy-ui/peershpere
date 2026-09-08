package com.peersphere.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "study_sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudySession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String description;

    /**
     * The exact date and time the session is scheduled for.
     * LocalDateTime stores both date and time — perfect for scheduling.
     * Example: 2026-07-15T18:00:00
     */
    @Column(nullable = false)
    private LocalDateTime scheduledAt;

    /**
     * How long the session runs in minutes.
     * 90 = 1.5 hours, 120 = 2 hours etc.
     * Storing duration separately from end time is more flexible —
     * if the start time changes, duration stays the same.
     */
    @Column(nullable = false)
    private Integer durationMinutes;

    private String platform;    // "Zoom", "Google Meet", "Discord"

    private String meetingLink; // actual URL to join

    /**
     * @Enumerated(EnumType.STRING) — stores "SCHEDULED", "CANCELLED",
     * or "COMPLETED" as text in the database.
     * Never use EnumType.ORDINAL — if you reorder enum values,
     * all existing data in the DB becomes wrong silently.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SessionStatus status;

    /**
     * Which study group this session belongs to.
     * LAZY loading — we don't always need the full group object
     * just because we're loading a session.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private StudyGroup studyGroup;

    /**
     * Who created/organised this session.
     * This person has edit and cancel rights.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) status = SessionStatus.SCHEDULED;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}