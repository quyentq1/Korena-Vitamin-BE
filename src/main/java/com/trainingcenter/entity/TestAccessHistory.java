package com.trainingcenter.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Test Access History
 * Tracks free vs paid test access for users (Test Type 1: FREE_PAID)
 * Requirements:
 * - First 5 tests are FREE
 * - Additional tests require payment (100k, 200k, etc.)
 */
@Entity
@Table(name = "test_access_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestAccessHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;

    @Enumerated(EnumType.STRING)
    @Column(name = "access_type", nullable = false)
    private AccessType accessType = AccessType.FREE;

    @Column(name = "payment_amount", precision = 10, scale = 2)
    private BigDecimal paymentAmount = BigDecimal.ZERO;

    @CreationTimestamp
    @Column(name = "accessed_at", nullable = false, updatable = false)
    private LocalDateTime accessedAt;

    public enum AccessType {
        FREE,   // First 5 tests
        PAID    // Paid tests (100k, 200k, ...)
    }
}
