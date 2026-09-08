package com.peersphere.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * This is the junction table entity between User and StudyGroup.
 * It represents one membership — one user in one group.
 * It also carries extra data: the member's role and join date.
 *
 * Why not use @ManyToMany directly?
 * @ManyToMany generates a simple join table with just two FK columns.
 * We need extra columns (role, joinedAt), so we model it as its
 * own @Entity — this is the standard approach in real applications.
 */
@Entity
@Table(
        name = "group_members",
        // Composite unique constraint — one user can only appear once per group
        uniqueConstraints = @UniqueConstraint(columnNames = {"group_id", "user_id"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private StudyGroup studyGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * ADMIN — the person who created the group
     * MEMBER — everyone else who joined
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GroupRole role;

    @Column(nullable = false, updatable = false)
    private LocalDateTime joinedAt;

    @PrePersist
    protected void onCreate() {
        joinedAt = LocalDateTime.now();
    }
}