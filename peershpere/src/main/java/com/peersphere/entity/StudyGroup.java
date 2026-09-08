package com.peersphere.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a study group in the database.
 *
 * @OneToMany — one group has many members (GroupMember rows).
 * mappedBy = "studyGroup" — tells JPA that the GroupMember entity
 * owns this relationship (it has the foreign key column).
 *
 * cascade = CascadeType.ALL — if we delete a StudyGroup, JPA
 * automatically deletes all its GroupMember rows too.
 * Without this, you'd get a foreign key constraint error.
 *
 * orphanRemoval = true — if a GroupMember is removed from this list,
 * it is automatically deleted from the database.
 */
@Entity
@Table(name = "study_groups")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudyGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private String subject;

    @Column(nullable = false)
    private Integer maxMembers;

    @Column(nullable = false)
    private Boolean isActive;

    /**
     * @ManyToOne — many groups can be created by one user.
     * @JoinColumn — the foreign key column in study_groups table
     * is called "created_by".
     * FetchType.LAZY — only load the User from DB when we
     * actually access group.getCreatedBy(). Avoids unnecessary joins.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @OneToMany(mappedBy = "studyGroup", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<GroupMember> members = new ArrayList<>();

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (isActive == null) isActive = true;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}