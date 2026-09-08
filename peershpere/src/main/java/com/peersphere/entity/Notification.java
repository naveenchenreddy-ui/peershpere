package com.peersphere.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @Column(nullable = false)
    private Boolean isRead;

    /**
     * Reference ID — the ID of the related entity.
     * e.g. if type = NEW_ANSWER, referenceId = questionId
     * The frontend uses this to navigate to the right page.
     */
    private Long referenceId;

    /**
     * Who receives this notification.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id", nullable = false)
    private User recipient;

    /**
     * Who triggered this notification.
     * e.g. the person who posted the answer.
     * Can be null for system-generated notifications.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "triggered_by_id")
    private User triggeredBy;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (isRead == null) isRead = false;
    }
}