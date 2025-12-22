package com.trainingcenter.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Variant Questions
 * Maps specific questions to each exam variant
 * Ensures each variant has unique questions
 */
@Entity
@Table(name = "variant_questions", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"variant_id", "question_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VariantQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "variant_id", nullable = false)
    private ExamVariant variant;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Column(name = "question_order", nullable = false)
    private Integer questionOrder; // Order in which question appears in this variant

    @Column(nullable = false)
    private Integer points = 1;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
