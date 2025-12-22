package com.trainingcenter.controller;

import com.trainingcenter.dto.MessageResponse;
import com.trainingcenter.entity.CourseRegistration;
import com.trainingcenter.entity.User;
import com.trainingcenter.service.OcrService;
import com.trainingcenter.service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/staff")
@PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
public class StaffController {

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private OcrService ocrService;

    @Autowired
    private com.trainingcenter.service.UserService userService;

    // Student Management
    @GetMapping("/students")
    public ResponseEntity<List<User>> getStudents() {
        return ResponseEntity.ok(userService.getUsersByRole(User.UserRole.STUDENT));
    }

    @PostMapping("/students")
    public ResponseEntity<User> createStudent(@RequestBody User student) {
        student.setRole(User.UserRole.STUDENT);
        return ResponseEntity.ok(userService.createUser(student));
    }

    // Registration Management
    @PostMapping("/registrations")
    public ResponseEntity<CourseRegistration> createRegistration(
            @RequestBody CourseRegistration registration) {
        return ResponseEntity.ok(registrationService.createRegistration(registration));
    }

    @GetMapping("/registrations")
    public ResponseEntity<List<CourseRegistration>> getAllRegistrations() {
        return ResponseEntity.ok(registrationService.getAllRegistrations());
    }

    @GetMapping("/registrations/{id}")
    public ResponseEntity<CourseRegistration> getRegistrationById(@PathVariable Long id) {
        return ResponseEntity.ok(registrationService.getRegistrationById(id));
    }

    @GetMapping("/registrations/status/{status}")
    public ResponseEntity<List<CourseRegistration>> getRegistrationsByStatus(
            @PathVariable String status) {
        CourseRegistration.RegistrationStatus regStatus = CourseRegistration.RegistrationStatus.valueOf(status);
        return ResponseEntity.ok(registrationService.getRegistrationsByStatus(regStatus));
    }

    @PatchMapping("/registrations/{id}/approve")
    public ResponseEntity<CourseRegistration> approveRegistration(@PathVariable Long id) {
        return ResponseEntity.ok(registrationService.approveRegistration(id));
    }

    @PatchMapping("/registrations/{id}/reject")
    public ResponseEntity<CourseRegistration> rejectRegistration(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        return ResponseEntity.ok(registrationService.rejectRegistration(id, request.get("reason")));
    }

    @PostMapping("/registrations/{id}/create-account")
    public ResponseEntity<User> createStudentAccount(
            @PathVariable Long id,
            @RequestBody Map<String, String> studentInfo) {
        User student = registrationService.createStudentAccountFromRegistration(
                id,
                studentInfo.get("studentName"),
                studentInfo.get("email"),
                studentInfo.get("phone"));
        return ResponseEntity.ok(student);
    }

    // OCR Processing
    @PostMapping("/ocr/process")
    public ResponseEntity<Map<String, String>> processOcrImage(
            @RequestParam("file") MultipartFile file) {
        Map<String, String> result = ocrService.processFormImage(file);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/ocr/export-word")
    public ResponseEntity<Map<String, String>> exportToWord(
            @RequestBody Map<String, String> request) {
        String filePath = ocrService.exportToWord(
                request.get("ocrText"),
                request.get("studentName"));
        return ResponseEntity.ok(Map.of("filePath", filePath));
    }
}
