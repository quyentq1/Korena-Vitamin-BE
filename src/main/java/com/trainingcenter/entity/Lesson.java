package com.trainingcenter.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "lessons")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Lesson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    @JsonIgnoreProperties({"lessons", "hibernateLazyInitializer", "handler"})
    private Course course;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 500)
    private String videoUrl;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "lesson_order", nullable = false)
    private Integer lessonOrder;

    /**
     * isPreview = true means this lesson is available for guest users to preview
     * Typically first 1-2 lessons are marked as preview
     */
    @Column(name = "is_preview", nullable = false)
    private Boolean isPreview = false;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Column(length = 500)
    private String thumbnailUrl;

    @Column(nullable = false)
    private Boolean active = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
