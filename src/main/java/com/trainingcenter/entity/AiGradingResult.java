package com.trainingcenter.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * AI Grading Result
 * Requirement: AI sửa đáp án + nêu điểm yếu của học sinh sau khi luyện
 */
@Entity
@Table(name = "ai_grading_results")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AiGradingResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_answer_id", nullable = false)
    private StudentAnswer studentAnswer;

    @Column(name = "ai_score", precision = 5, scale = 2)
    private BigDecimal aiScore;

    @Column(name = "ai_model", length = 50)
    private String aiModel; // e.g., "GPT-4", "Claude", "Gemini"

    @Column(name = "confidence_score", precision = 5, scale = 2)
    private BigDecimal confidenceScore;

    @CreationTimestamp
    @Column(name = "graded_at", nullable = false, updatable = false)
    private LocalDateTime gradedAt;
}
