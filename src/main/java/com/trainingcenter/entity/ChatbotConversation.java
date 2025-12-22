package com.trainingcenter.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Chatbot Conversation
 * Requirement: AI chatbot hỗ trợ tư vấn khóa/chứng chỉ theo kịch bản có sẵn
 */
@Entity
@Table(name = "chatbot_conversations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatbotConversation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // Can be NULL for anonymous users

    @Column(name = "session_id", nullable = false, length = 100)
    private String sessionId;

    @CreationTimestamp
    @Column(name = "started_at", nullable = false, updatable = false)
    private LocalDateTime startedAt;

    @Column(name = "ended_at")
    private LocalDateTime endedAt;
}
