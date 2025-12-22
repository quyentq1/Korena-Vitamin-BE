package com.trainingcenter.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "registration_forms")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationForm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "form_number", unique = true, length = 50)
    private String formNumber;

    @Column(name = "student_name", length = 100)
    private String studentName;

    @Column(length = 100)
    private String email;

    @Column(length = 20)
    private String phone;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(name = "course_code", length = 20)
    private String courseCode;

    @Column(name = "form_image_url", length = 500)
    private String formImageUrl;

    @Column(name = "ocr_text", columnDefinition = "TEXT")
    private String ocrText;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FormStatus status = FormStatus.PENDING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "processed_by")
    private User processedBy;

    @Column(name = "scanned_at")
    private LocalDateTime scannedAt;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum FormStatus {
        PENDING, PROCESSED, APPROVED, REJECTED
    }
}
