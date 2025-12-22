package com.trainingcenter.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Learning Report
 * Requirement: Teacher sends learning reports to students
 * Used by Education Manager for monitoring student progress
 */
@Entity
@Table(name = "learning_reports")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LearningReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "class_id", nullable = false)
    private ClassEntity classEntity;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "teacher_id", nullable = false)
    private User teacher;

    @Column(name = "report_date", nullable = false)
    private LocalDate reportDate;

    @Column(name = "attendance_rate", precision = 5, scale = 2)
    private BigDecimal attendanceRate;

    @Column(columnDefinition = "TEXT")
    private String progress; // Overall progress description

    @Column(columnDefinition = "TEXT")
    private String strengths; // Student strengths

    @Column(columnDefinition = "TEXT")
    private String weaknesses; // Student weaknesses

    @Column(columnDefinition = "TEXT")
    private String recommendations; // Teacher recommendations

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
