package com.peersphere.service.impl;

import com.peersphere.dto.response.NotificationResponse;
import com.peersphere.entity.Notification;
import com.peersphere.entity.NotificationType;
import com.peersphere.entity.User;
import com.peersphere.exception.ResourceNotFoundException;
import com.peersphere.exception.UnauthorizedException;
import com.peersphere.repository.NotificationRepository;
import com.peersphere.repository.UserRepository;
import com.peersphere.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    /**
     * Core method — creates and saves one notification.
     * Called internally by other services, never by the controller.
     *
     * We wrap in try-catch so that a notification failure NEVER
     * causes the main operation to fail.
     * e.g. If posting an answer succeeds but the notification fails,
     * the answer is still saved. Notifications are supplementary.
     */
    @Override
    public void createNotification(User recipient, User triggeredBy,
                                   NotificationType type, String title,
                                   String message, Long referenceId) {
        try {
            // Don't notify people about their own actions
            if (recipient.getId().equals(triggeredBy != null
                    ? triggeredBy.getId() : -1L)) {
                return;
            }

            Notification notification = Notification.builder()
                    .recipient(recipient)
                    .triggeredBy(triggeredBy)
                    .type(type)
                    .title(title)
                    .message(message)
                    .referenceId(referenceId)
                    .isRead(false)
                    .build();

            notificationRepository.save(notification);

        } catch (Exception e) {
            // Log but never propagate — notification failure is non-fatal
            log.error("Failed to create notification for user {}: {}",
                    recipient.getId(), e.getMessage());
        }
    }

    @Override
    public List<NotificationResponse> getMyNotifications(String email) {
        User user = getUserByEmail(email);
        return notificationRepository
                .findByRecipientIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<NotificationResponse> getUnreadNotifications(String email) {
        User user = getUserByEmail(email);
        return notificationRepository
                .findByRecipientIdAndIsReadFalseOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public int getUnreadCount(String email) {
        User user = getUserByEmail(email);
        return notificationRepository.countByRecipientIdAndIsReadFalse(user.getId());
    }

    @Override
    @Transactional
    public NotificationResponse markAsRead(String email, Long notificationId) {
        User user = getUserByEmail(email);
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));

        if (!notification.getRecipient().getId().equals(user.getId())) {
            throw new UnauthorizedException("You can only read your own notifications");
        }

        notification.setIsRead(true);
        return mapToResponse(notificationRepository.save(notification));
    }

    @Override
    @Transactional
    public void markAllAsRead(String email) {
        User user = getUserByEmail(email);
        notificationRepository.markAllAsRead(user.getId());
    }

    @Override
    @Transactional
    public void deleteNotification(String email, Long notificationId) {
        User user = getUserByEmail(email);
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));

        if (!notification.getRecipient().getId().equals(user.getId())) {
            throw new UnauthorizedException("You can only delete your own notifications");
        }

        notificationRepository.delete(notification);
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private NotificationResponse mapToResponse(Notification n) {
        return NotificationResponse.builder()
                .id(n.getId())
                .title(n.getTitle())
                .message(n.getMessage())
                .type(n.getType().name())
                .isRead(n.getIsRead())
                .referenceId(n.getReferenceId())
                .triggeredByName(n.getTriggeredBy() != null
                        ? n.getTriggeredBy().getFullName() : "System")
                .createdAt(n.getCreatedAt())
                .build();
    }
}