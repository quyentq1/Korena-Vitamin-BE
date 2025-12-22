package com.trainingcenter.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Exam Variants
 * Supports 20 different variants (A-T) of the same exam
 * Each variant has different questions but same pattern distribution
 */
@Entity
@Table(name = "exam_variants", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"exam_id", "variant_code"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExamVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;

    @Column(name = "variant_code", nullable = false, length = 10)
    private String variantCode; // A, B, C, ..., T (20 variants)

    @Column(name = "variant_name", length = 100)
    private String variantName;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
