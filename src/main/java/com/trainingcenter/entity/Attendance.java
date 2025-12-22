package com.trainingcenter.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Attendance
 * Tracks student attendance for each lesson
 * Used for generating attendance reports
 */
@Entity
@Table(name = "attendance", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"schedule_id", "student_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "schedule_id", nullable = false)
    private ClassSchedule schedule;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AttendanceStatus status = AttendanceStatus.PRESENT;

    @Column(columnDefinition = "TEXT")
    private String notes; // Optional notes about attendance

    @CreationTimestamp
    @Column(name = "marked_at", nullable = false, updatable = false)
    private LocalDateTime markedAt;

    public enum AttendanceStatus {
        PRESENT,    // Student attended
        ABSENT,     // Student was absent
        LATE,       // Student was late
        EXCUSED     // Absence excused (sick leave, etc.)
    }
}
