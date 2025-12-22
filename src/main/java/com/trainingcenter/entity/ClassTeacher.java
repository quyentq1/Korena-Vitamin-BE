package com.trainingcenter.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Class Teachers
 * Maps teachers to classes
 * Supports primary teacher vs assistant teacher distinction
 * Requirement: Education Manager assigns teachers to classes
 */
@Entity
@Table(name = "class_teachers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClassTeacher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "class_id", nullable = false)
    private ClassEntity classEntity;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "teacher_id", nullable = false)
    private User teacher; // Must have role TEACHER

    @Column(name = "assigned_date")
    private LocalDate assignedDate;

    @Column(name = "is_primary", nullable = false)
    private Boolean isPrimary = false; // Primary teacher vs assistant

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
