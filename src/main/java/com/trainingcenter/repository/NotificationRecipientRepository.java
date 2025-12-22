package com.trainingcenter.repository;

import com.trainingcenter.entity.NotificationRecipient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRecipientRepository extends JpaRepository<NotificationRecipient, Long> {
    List<NotificationRecipient> findByUserIdAndIsRead(Long userId, Boolean isRead);
    List<NotificationRecipient> findByUserIdOrderByCreatedAtDesc(Long userId);
    long countByUserIdAndIsRead(Long userId, Boolean isRead);
}
