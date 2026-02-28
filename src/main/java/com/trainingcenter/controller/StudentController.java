package com.trainingcenter.controller;

import com.trainingcenter.entity.*;
import com.trainingcenter.service.ExamAttemptService;
import com.trainingcenter.service.ExamService;
import com.trainingcenter.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/student")
@PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
public class StudentController {

    @Autowired
    private ExamService examService;

    @Autowired
    private ExamAttemptService attemptService;

    @Autowired
    private UserService userService;

    // View Available Exams
    @GetMapping("/exams")
    public ResponseEntity<List<Exam>> getAvailableExams() {
        return ResponseEntity.ok(examService.getPublishedExams());
    }

    @GetMapping("/exams/{id}")
    public ResponseEntity<Exam> getExamDetails(@PathVariable Long id) {
        return ResponseEntity.ok(examService.getExamById(id));
    }

    // Exam Taking
    @PostMapping("/exams/{id}/start")
    public ResponseEntity<ExamAttempt> startExam(
            @PathVariable Long id,
            Authentication authentication) {
        User student = userService.getUserByUsername(authentication.getName());
        ExamAttempt attempt = attemptService.startExam(id, student.getId());

        // Include questions
        attempt.setExam(examService.getExamById(id));
        attempt.getExam().setExamQuestions(examService.getExamQuestions(id));

        return ResponseEntity.ok(attempt);
    }

    @PostMapping("/attempts/{id}/answer")
    public ResponseEntity<StudentAnswer> submitAnswer(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request) {
        Long examQuestionId = ((Number) request.get("examQuestionId")).longValue();
        String answerText = (String) request.get("answerText");
        String answerFileUrl = (String) request.get("answerFileUrl");

        return ResponseEntity.ok(attemptService.submitAnswer(
                id, examQuestionId, answerText, answerFileUrl));
    }

    @PostMapping("/attempts/{id}/submit")
    public ResponseEntity<ExamAttempt> submitExam(@PathVariable Long id) {
        return ResponseEntity.ok(attemptService.submitExam(id));
    }

    // View Results
    @GetMapping("/results")
    public ResponseEntity<List<ExamAttempt>> getMyResults(Authentication authentication) {
        User student = userService.getUserByUsername(authentication.getName());
        return ResponseEntity.ok(attemptService.getStudentAttempts(student.getId()));
    }

    @GetMapping("/results/{attemptId}")
    public ResponseEntity<Map<String, Object>> getDetailedResult(@PathVariable Long attemptId) {
        ExamAttempt attempt = attemptService.getAttemptById(attemptId);
        List<StudentAnswer> answers = attemptService.getAttemptAnswers(attemptId);

        return ResponseEntity.ok(Map.of(
                "attempt", attempt,
                "answers", answers,
                "exam", attempt.getExam()));
    }

    @GetMapping("/attempts/{id}")
    public ResponseEntity<ExamAttempt> getAttemptDetails(@PathVariable Long id) {
        ExamAttempt attempt = attemptService.getAttemptById(id);
        attempt.setAnswers(attemptService.getAttemptAnswers(id));
        return ResponseEntity.ok(attempt);
    }
}
