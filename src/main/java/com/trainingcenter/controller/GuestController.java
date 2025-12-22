package com.trainingcenter.controller;

import com.trainingcenter.entity.ExamAttempt;
import com.trainingcenter.service.ExamAttemptService;
import com.trainingcenter.service.ExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/guest")
public class GuestController {

    @Autowired
    private ExamAttemptService attemptService;

    @Autowired
    private ExamService examService;

    @PostMapping("/exams/{id}/start")
    public ResponseEntity<ExamAttempt> startGuestExam(@PathVariable Long id) {
        // Pass null as studentId to indicate Guest mode
        return ResponseEntity.ok(attemptService.startExam(id, null));
    }

    @GetMapping("/exams")
    public ResponseEntity<java.util.List<com.trainingcenter.entity.Exam>> getGuestExams() {
        return ResponseEntity.ok(examService.getGuestExams());
    }
}
