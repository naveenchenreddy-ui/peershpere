package com.peersphere.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "notes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String description;

    @Column(nullable = false)
    private String subject;

    /**
     * We store the file URL, not the file itself.
     * The file is uploaded to a storage service (Google Drive, AWS S3,
     * or even a local folder during development).
     * This keeps our database lightweight.
     */
    @Column(nullable = false)
    private String fileUrl;

    private String fileType;      // "PDF", "DOCX", "PPT" etc.

    private Long fileSizeKb;      // file size in KB

    @Column(nullable = false)
    private Integer downloadCount;

    @Column(nullable = false)
    private Integer likeCount;

    /**
     * Tags stored as a comma-separated string for simplicity.
     * e.g. "java,oop,design-patterns"
     * For search we'll use LIKE queries on this field.
     */
    private String tags;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by", nullable = false)
    private User uploadedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private StudyGroup studyGroup; // nullable — notes can be group-specific or public

    @OneToMany(mappedBy = "note", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<NoteLike> likes = new ArrayList<>();

    @OneToMany(mappedBy = "note", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Bookmark> bookmarks = new ArrayList<>();

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (downloadCount == null) downloadCount = 0;
        if (likeCount == null) likeCount = 0;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}