package com.trainingcenter.controller;

import com.trainingcenter.entity.ExamAttempt;
import com.trainingcenter.entity.StudentAnswer;
import com.trainingcenter.entity.Exam;
import com.trainingcenter.service.ExamAttemptService;
import com.trainingcenter.service.ExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/guest")
public class GuestController {

    @Autowired
    private ExamAttemptService attemptService;

    @Autowired
    private ExamService examService;

    @GetMapping("/exams")
    public ResponseEntity<List<Exam>> getGuestExams() {
        return ResponseEntity.ok(examService.getGuestExams());
    }

    @GetMapping("/exams/{id}")
    public ResponseEntity<Exam> getGuestExamDetails(@PathVariable Long id) {
        Exam exam = examService.getExamById(id);
        exam.setExamQuestions(examService.getExamQuestions(id));
        return ResponseEntity.ok(exam);
    }

    @PostMapping("/exams/{id}/start")
    public ResponseEntity<ExamAttempt> startGuestExam(@PathVariable Long id) {
        // Pass null as studentId to indicate Guest mode. ExamAttemptService will create
        // temp user by IP.
        ExamAttempt attempt = attemptService.startExam(id, null);

        // Include questions
        attempt.setExam(examService.getExamById(id));
        attempt.getExam().setExamQuestions(examService.getExamQuestions(id));

        return ResponseEntity.ok(attempt);
    }

    @PostMapping("/attempts/{id}/answer")
    public ResponseEntity<StudentAnswer> submitGuestAnswer(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request) {
        Long examQuestionId = ((Number) request.get("examQuestionId")).longValue();
        String answerText = request.get("answerText") != null ? String.valueOf(request.get("answerText")) : null;

        return ResponseEntity.ok(attemptService.submitAnswer(
                id, examQuestionId, answerText, null));
    }

    @PostMapping("/attempts/{id}/submit")
    public ResponseEntity<ExamAttempt> submitGuestExam(@PathVariable Long id) {
        return ResponseEntity.ok(attemptService.submitExam(id));
    }
}
