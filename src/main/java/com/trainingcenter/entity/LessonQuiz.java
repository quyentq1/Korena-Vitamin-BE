package com.trainingcenter.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Lesson Quiz
 * Test Type 3: Quiz after each lesson
 * Requirements:
 * - Short quiz at end of each lesson
 * - Created by teacher
 * - Optional: Questions from QB with lesson tag OR teacher-created
 */
@Entity
@Table(name = "lesson_quizzes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LessonQuiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "class_id", nullable = false)
    private ClassEntity classEntity;

    @Column(name = "lesson_number", nullable = false)
    private Integer lessonNumber; // Which lesson this quiz is for

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy; // Teacher

    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes = 15; // Default 15 minutes

    @Column(name = "total_points", nullable = false)
    private Integer totalPoints = 10; // Default 10 points

    @Column(name = "quiz_date")
    private LocalDate quizDate;

    @Column(nullable = false)
    private Boolean active = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
