package com.trainingcenter.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Question Patterns: N1-N8 (Listening), R1-R8 (Reading), W1-W8 (Writing), S1-S8 (Speaking)
 * Each skill has 8 different question patterns
 * Example: N1 = "Listen to short dialogue and choose matching picture" (typically 3 questions)
 */
@Entity
@Table(name = "question_patterns", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"skill_id", "pattern_code"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionPattern {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "skill_id", nullable = false)
    private ExamSkill skill;

    @Column(name = "pattern_code", nullable = false, length = 10)
    private String patternCode; // N1, N2, ..., N8, R1, R2, ..., R8, etc.

    @Column(name = "pattern_name", nullable = false, length = 100)
    private String patternName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "typical_question_count", nullable = false)
    private Integer typicalQuestionCount = 0; // How many questions typically use this pattern in a 40-question exam

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
