package com.peersphere.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Each row represents ONE study session log entry.
 * A student might create multiple entries per day.
 * The weekly/total stats are computed by aggregating these rows.
 */
@Entity
@Table(name = "progress_entries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProgressEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String topicName;

    @Column(length = 1000)
    private String description; // what was studied

    @Column(nullable = false)
    private Integer studyMinutes; // logged in minutes for precision

    /**
     * LocalDate (not LocalDateTime) — we care about the date,
     * not the exact time, for weekly/daily grouping.
     */
    @Column(nullable = false)
    private LocalDate studyDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DifficultyLevel difficulty;

    /**
     * Optional link to a study group context.
     * Was this studied for a specific group's topic? Or just general?
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private StudyGroup studyGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}