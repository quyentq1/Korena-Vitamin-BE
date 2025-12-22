package com.trainingcenter.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * AI Feedback
 * Requirement: AI nêu điểm yếu của học sinh
 * Provides detailed feedback on grammar, vocabulary, structure, content
 */
@Entity
@Table(name = "ai_feedback")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AiFeedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ai_grading_id", nullable = false)
    private AiGradingResult aiGrading;

    @Enumerated(EnumType.STRING)
    @Column(name = "feedback_type", nullable = false)
    private FeedbackType feedbackType;

    @Column(name = "feedback_text", nullable = false, columnDefinition = "TEXT")
    private String feedbackText;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Severity severity = Severity.SUGGESTION;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public enum FeedbackType {
        GRAMMAR,        // Grammar errors/suggestions
        VOCABULARY,     // Vocabulary usage
        STRUCTURE,      // Sentence/paragraph structure
        CONTENT,        // Content relevance
        PRONUNCIATION,  // For speaking tests
        OTHER
    }

    public enum Severity {
        ERROR,      // Critical error
        WARNING,    // Important issue
        SUGGESTION  // Improvement suggestion
    }
}
