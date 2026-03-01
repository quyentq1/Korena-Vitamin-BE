package com.trainingcenter.controller;

import com.trainingcenter.entity.ConsultationRequest;
import com.trainingcenter.repository.ConsultationRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * BUG-02 FIX: Public endpoint để khách gửi form tư vấn sau khi hết 2 bài test
 * miễn phí
 */
@RestController
public class ConsultationController {

    @Autowired
    private ConsultationRequestRepository consultationRepository;

    @Autowired
    private com.trainingcenter.service.EmailService emailService;

    /**
     * POST /api/public/consultation
     * Public endpoint — không cần JWT token
     * Guest điền form sau khi hết 2 bài test miễn phí
     */
    @PostMapping("/public/consultation")
    public ResponseEntity<Map<String, Object>> submitConsultation(
            @RequestBody @Valid ConsultationRequestDto dto) {

        ConsultationRequest req = new ConsultationRequest();
        req.setFullName(dto.fullName());
        req.setEmail(dto.email());
        req.setPhone(dto.phone());
        req.setContactTime(dto.contactTime());
        req.setTestInterested(dto.testInterested());
        req.setMessage(dto.message());
        req.setStatus(ConsultationRequest.ConsultationStatus.NEW);

        ConsultationRequest saved = consultationRepository.save(req);

        if (req.getEmail() != null && !req.getEmail().isEmpty()) {
            emailService.sendWelcomeEmail(req.getEmail(), req.getFullName());
        }

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Yêu cầu tư vấn đã được ghi nhận. Chúng tôi sẽ liên hệ trong 24h!",
                "id", saved.getId()));
    }

    /**
     * GET /api/admin/consultations
     * Admin/Staff xem danh sách yêu cầu tư vấn
     */
    @GetMapping("/admin/consultations")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<List<ConsultationRequest>> getAllConsultations(
            @RequestParam(required = false) String status) {

        if (status != null) {
            try {
                ConsultationRequest.ConsultationStatus consultationStatus = ConsultationRequest.ConsultationStatus
                        .valueOf(status.toUpperCase());
                return ResponseEntity.ok(consultationRepository.findByStatusOrderByCreatedAtDesc(consultationStatus));
            } catch (IllegalArgumentException e) {
                return ResponseEntity.ok(consultationRepository.findAllByOrderByCreatedAtDesc());
            }
        }
        return ResponseEntity.ok(consultationRepository.findAllByOrderByCreatedAtDesc());
    }

    /**
     * PATCH /api/admin/consultations/{id}/status
     * Cập nhật trạng thái yêu cầu tư vấn
     */
    @PatchMapping("/admin/consultations/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ConsultationRequest> updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {

        ConsultationRequest req = consultationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Consultation request not found"));

        req.setStatus(ConsultationRequest.ConsultationStatus.valueOf(body.get("status").toUpperCase()));
        if (req.getStatus() == ConsultationRequest.ConsultationStatus.CONTACTED ||
                req.getStatus() == ConsultationRequest.ConsultationStatus.CLOSED) {
            req.setHandledAt(java.time.LocalDateTime.now());
        }

        return ResponseEntity.ok(consultationRepository.save(req));
    }

    // DTO record
    public record ConsultationRequestDto(
            String fullName,
            String email,
            String phone,
            String contactTime,
            String testInterested,
            String message) {
    }
}
