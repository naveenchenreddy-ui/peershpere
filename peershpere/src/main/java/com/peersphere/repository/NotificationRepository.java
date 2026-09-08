package com.peersphere.repository;

import com.peersphere.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByRecipientIdOrderByCreatedAtDesc(Long recipientId);

    List<Notification> findByRecipientIdAndIsReadFalseOrderByCreatedAtDesc(
            Long recipientId);

    int countByRecipientIdAndIsReadFalse(Long recipientId);

    /**
     * @Modifying + @Transactional — required for UPDATE/DELETE queries
     * in Spring Data JPA. Without @Modifying, Spring treats it as a
     * SELECT and throws an exception.
     *
     * This marks all of a user's notifications as read in one
     * SQL statement — far more efficient than loading each one
     * and calling save() individually.
     */
    @Modifying
    @Transactional
    @Query("UPDATE Notification n SET n.isRead = true " +
            "WHERE n.recipient.id = :recipientId AND n.isRead = false")
    void markAllAsRead(@Param("recipientId") Long recipientId);
}