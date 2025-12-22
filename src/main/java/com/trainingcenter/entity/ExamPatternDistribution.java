package com.trainingcenter.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Exam Pattern Distribution
 * Defines how many questions of each pattern appear in an exam
 * Example: For a 40-question exam, might have N1(3 questions), N2(3), N3(2), R1(4), R2(3)...
 */
@Entity
@Table(name = "exam_pattern_distribution", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"exam_id", "pattern_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExamPatternDistribution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pattern_id", nullable = false)
    private QuestionPattern pattern;

    @Column(name = "question_count", nullable = false)
    private Integer questionCount; // How many questions of this pattern in this exam

    @Column(name = "points_per_question", nullable = false)
    private Integer pointsPerQuestion = 1;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
