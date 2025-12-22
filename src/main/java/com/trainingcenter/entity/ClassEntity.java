package com.trainingcenter.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Class Entity (ClassEntity to avoid Java keyword conflict)
 * Represents a specific class/learning group for a course
 */
@Entity
@Table(name = "classes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClassEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(name = "class_code", unique = true, nullable = false, length = 50)
    private String classCode; // e.g., TOPIK1-2025-01

    @Column(name = "class_name", nullable = false, length = 200)
    private String className;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(nullable = false)
    private Integer capacity = 30;

    @Column(name = "current_enrollment", nullable = false)
    private Integer currentEnrollment = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ClassStatus status = ClassStatus.PLANNED;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum ClassStatus {
        PLANNED,    // Class planned but not started
        ONGOING,    // Currently in progress
        COMPLETED,  // Class finished
        CANCELLED   // Class cancelled
    }
}
