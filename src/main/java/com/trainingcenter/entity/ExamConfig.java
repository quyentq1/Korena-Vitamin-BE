package com.trainingcenter.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Exam Config
 * Configuration for each exam type defining behavior:
 * - Test Type 1 (FREE_PAID): allow_duplicate_questions = FALSE, randomize = TRUE
 * - Test Type 2 (UNLIMITED): allow_duplicate_questions = TRUE, randomize = TRUE
 * - Test Type 3 (LESSON_QUIZ): smaller config
 */
@Entity
@Table(name = "exam_configs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExamConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "exam_id", nullable = false, unique = true)
    private Exam exam;

    @Column(name = "total_questions", nullable = false)
    private Integer totalQuestions;

    /**
     * CRITICAL LOGIC:
     * - Test Type 1 (FREE_PAID): FALSE - each user gets unique questions
     * - Test Type 2 (UNLIMITED): TRUE - questions can repeat across attempts
     */
    @Column(name = "allow_duplicate_questions", nullable = false)
    private Boolean allowDuplicateQuestions = true;

    @Column(name = "randomize_questions", nullable = false)
    private Boolean randomizeQuestions = true;

    @Column(name = "randomize_options", nullable = false)
    private Boolean randomizeOptions = true;

    @Column(name = "show_result_immediately", nullable = false)
    private Boolean showResultImmediately = false;

    /**
     * Anti-cheating feature: Lock keyboard during exam
     */
    @Column(name = "enable_keyboard_lock", nullable = false)
    private Boolean enableKeyboardLock = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
