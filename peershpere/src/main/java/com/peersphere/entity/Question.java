package com.peersphere.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "questions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    /**
     * @Column(columnDefinition = "TEXT") — stores up to 65,535 characters.
     * Regular VARCHAR(255) is too small for detailed questions.
     * We use TEXT for any field that could be long-form content.
     */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String body;

    private String tags; // comma-separated: "java,oop,recursion"

    @Column(nullable = false)
    private Integer upvoteCount;

    @Column(nullable = false)
    private Integer answerCount;

    @Column(nullable = false)
    private Boolean isSolved; // true when an answer is accepted

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asked_by", nullable = false)
    private User askedBy;

    /**
     * Optional group context.
     * If set — this question belongs to a specific study group's forum.
     * If null — it's a platform-wide question visible to everyone.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private StudyGroup studyGroup;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Answer> answers = new ArrayList<>();

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<QuestionComment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<QuestionUpvote> upvotes = new ArrayList<>();

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (upvoteCount == null) upvoteCount = 0;
        if (answerCount == null) answerCount = 0;
        if (isSolved == null) isSolved = false;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}