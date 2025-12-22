package com.trainingcenter.repository;

import com.trainingcenter.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findBySenderIdOrderByCreatedAtDesc(Long senderId);
    List<Notification> findByNotificationTypeOrderByCreatedAtDesc(Notification.NotificationType type);
}
