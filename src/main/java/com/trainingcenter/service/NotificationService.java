package com.trainingcenter.service;

import com.trainingcenter.entity.*;
import com.trainingcenter.exception.BadRequestException;
import com.trainingcenter.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Notification Service
 * Sends and manages notifications to users
 */
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationRecipientRepository recipientRepository;
    private final UserRepository userRepository;

    /**
     * Send notification to specific users
     */
    @Transactional
    public Notification sendNotification(String title, String message, 
                                        Notification.NotificationType type,
                                        Long senderId, List<Long> recipientIds) {
        User sender = senderId != null ? userRepository.findById(senderId).orElse(null) : null;

        Notification notification = new Notification();
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setNotificationType(type);
        notification.setSender(sender);

        Notification savedNotification = notificationRepository.save(notification);

        // Create recipient records
        for (Long recipientId : recipientIds) {
            User recipient = userRepository.findById(recipientId)
                .orElseThrow(() -> new BadRequestException("Recipient not found: " + recipientId));

            NotificationRecipient nr = new NotificationRecipient();
            nr.setNotification(savedNotification);
            nr.setUser(recipient);
            nr.setIsRead(false);

            recipientRepository.save(nr);
        }

        return savedNotification;
    }

    /**
     * Send notification to all users with specific role
     */
    @Transactional
    public Notification sendNotificationToRole(String title, String message,
                                              Notification.NotificationType type,
                                              User.UserRole targetRole) {
        List<User> users = userRepository.findByRole(targetRole);
        List<Long> recipientIds = users.stream().map(User::getId).collect(Collectors.toList());

        return sendNotification(title, message, type, null, recipientIds);
    }

    /**
     * Get user's notifications
     */
    public List<NotificationRecipient> getUserNotifications(Long userId) {
        return recipientRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    /**
     * Get unread notifications count
     */
    public long getUnreadCount(Long userId) {
        return recipientRepository.countByUserIdAndIsRead(userId, false);
    }

    /**
     * Mark notification as read
     */
    @Transactional
    public void markAsRead(Long notificationRecipientId) {
        NotificationRecipient recipient = recipientRepository.findById(notificationRecipientId)
            .orElseThrow(() -> new BadRequestException("Notification not found"));

        recipient.setIsRead(true);
        recipient.setReadAt(java.time.LocalDateTime.now());
        recipientRepository.save(recipient);
    }

    /**
     * Mark all notifications as read for a user
     */
    @Transactional
    public void markAllAsRead(Long userId) {
        List<NotificationRecipient> unreadNotifications = 
            recipientRepository.findByUserIdAndIsRead(userId, false);

        for (NotificationRecipient nr : unreadNotifications) {
            nr.setIsRead(true);
            nr.setReadAt(java.time.LocalDateTime.now());
            recipientRepository.save(nr);
        }
    }
}
