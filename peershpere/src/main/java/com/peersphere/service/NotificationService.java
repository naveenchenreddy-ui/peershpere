package com.peersphere.service;

import com.peersphere.dto.response.NotificationResponse;
import com.peersphere.entity.NotificationType;
import com.peersphere.entity.User;

import java.util.List;

public interface NotificationService {

    // Called by other services to create notifications
    void createNotification(User recipient, User triggeredBy,
                            NotificationType type, String title,
                            String message, Long referenceId);

    // Called by the controller for user-facing endpoints
    List<NotificationResponse> getMyNotifications(String email);

    List<NotificationResponse> getUnreadNotifications(String email);

    int getUnreadCount(String email);

    NotificationResponse markAsRead(String email, Long notificationId);

    void markAllAsRead(String email);

    void deleteNotification(String email, Long notificationId);
}