package com.peersphere.controller;

import com.peersphere.dto.response.NotificationResponse;
import com.peersphere.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getMyNotifications(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                notificationService.getMyNotifications(userDetails.getUsername()));
    }

    @GetMapping("/unread")
    public ResponseEntity<List<NotificationResponse>> getUnreadNotifications(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                notificationService.getUnreadNotifications(userDetails.getUsername()));
    }

    /**
     * GET /api/notifications/count
     * Returns just the unread count — lightweight endpoint
     * that the frontend polls every 30 seconds for the notification badge.
     */
    @GetMapping("/count")
    public ResponseEntity<Map<String, Integer>> getUnreadCount(
            @AuthenticationPrincipal UserDetails userDetails) {
        int count = notificationService.getUnreadCount(userDetails.getUsername());
        return ResponseEntity.ok(Map.of("unreadCount", count));
    }

    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<NotificationResponse> markAsRead(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long notificationId) {
        return ResponseEntity.ok(
                notificationService.markAsRead(
                        userDetails.getUsername(), notificationId));
    }

    @PatchMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead(
            @AuthenticationPrincipal UserDetails userDetails) {
        notificationService.markAllAsRead(userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{notificationId}")
    public ResponseEntity<Void> deleteNotification(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long notificationId) {
        notificationService.deleteNotification(
                userDetails.getUsername(), notificationId);
        return ResponseEntity.noContent().build();
    }
}