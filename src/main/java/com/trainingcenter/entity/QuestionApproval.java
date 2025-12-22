package com.trainingcenter.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Question Approval
 * Requirement: Education Manager approves question bank
 * Tool to check duplicate content between questions
 */
@Entity
@Table(name = "question_approvals")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionApproval {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "submitted_by", nullable = false)
    private User submittedBy; // Teacher who created the question

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by")
    private User reviewedBy; // Education Manager

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApprovalStatus status = ApprovalStatus.PENDING;

    @Column(columnDefinition = "TEXT")
    private String feedback;

    /**
     * AI/Tool check for duplicate content
     * Requirement: "áp dụng tool để check trùng nội dung giữa các câu hỏi và đáp án trong cùng 1 dạng câu hỏi"
     */
    @Column(name = "duplicate_check_passed")
    private Boolean duplicateCheckPassed;

    @CreationTimestamp
    @Column(name = "submitted_at", nullable = false, updatable = false)
    private LocalDateTime submittedAt;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    public enum ApprovalStatus {
        PENDING,
        APPROVED,
        REJECTED,
        REVISION_NEEDED
    }
}
