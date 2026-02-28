package com.trainingcenter.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * BUG-02 FIX: Lưu yêu cầu tư vấn từ khách (guest) sau khi hết 2 bài test miễn
 * phí
 */
@Entity
@Table(name = "consultation_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsultationRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(nullable = false, length = 100)
    private String email;

    @Column(nullable = false, length = 20)
    private String phone;

    @Column(name = "contact_time", length = 20)
    private String contactTime; // morning / afternoon / evening

    @Column(name = "test_interested", length = 50)
    private String testInterested;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConsultationStatus status = ConsultationStatus.NEW;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "handled_at")
    private LocalDateTime handledAt;

    public enum ConsultationStatus {
        NEW, // Mới gửi, chưa xử lý
        CONTACTED, // Đã liên hệ khách
        CLOSED // Kết thúc
    }
}
