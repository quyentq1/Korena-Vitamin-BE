package com.trainingcenter.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Schedule Change Request
 * Requirement: Teacher requests schedule changes (via form in current system)
 * Education Manager approves/rejects the request
 */
@Entity
@Table(name = "schedule_change_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleChangeRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "schedule_id", nullable = false)
    private ClassSchedule schedule;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "requested_by", nullable = false)
    private User requestedBy; // Teacher

    @Enumerated(EnumType.STRING)
    @Column(name = "request_type", nullable = false)
    private RequestType requestType;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String reason;

    @Column(name = "proposed_date")
    private LocalDate proposedDate;

    @Column(name = "proposed_time")
    private LocalTime proposedTime;

    @Column(name = "proposed_room", length = 50)
    private String proposedRoom;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus status = RequestStatus.PENDING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by")
    private User reviewedBy; // Education Manager

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @Column(name = "review_notes", columnDefinition = "TEXT")
    private String reviewNotes;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum RequestType {
        RESCHEDULE,  // Change date/time
        CANCEL,      // Cancel the lesson
        ROOM_CHANGE  // Change room only
    }

    public enum RequestStatus {
        PENDING,
        APPROVED,
        REJECTED
    }
}
