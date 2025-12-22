package com.trainingcenter.controller;

import com.trainingcenter.dto.MessageResponse;
import com.trainingcenter.entity.*;
import com.trainingcenter.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/teacher")
@PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
public class TeacherController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private ExamService examService;

    @Autowired
    private ExamAttemptService attemptService;

    @Autowired
    private GradingService gradingService;

    @Autowired
    private UserService userService;

    // Question Bank Management
    @GetMapping("/questions")
    public ResponseEntity<List<Question>> getAllQuestions() {
        return ResponseEntity.ok(questionService.getAllQuestions());
    }

    @GetMapping("/questions/{id}")
    public ResponseEntity<Question> getQuestionById(@PathVariable Long id) {
        Question question = questionService.getQuestionById(id);
        // Load options if multiple choice
        if (question.getQuestionType() == Question.QuestionType.MULTIPLE_CHOICE) {
            question.setOptions(questionService.getOptionsByQuestionId(id));
        }
        return ResponseEntity.ok(question);
    }

    @PostMapping("/questions")
    public ResponseEntity<Question> createQuestion(@RequestBody Map<String, Object> request) {
        Question question = (Question) request.get("question");
        List<QuestionOption> options = (List<QuestionOption>) request.get("options");
        return ResponseEntity.ok(questionService.createQuestion(question, options));
    }

    @PutMapping("/questions/{id}")
    public ResponseEntity<Question> updateQuestion(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request) {
        Question question = (Question) request.get("question");
        List<QuestionOption> options = (List<QuestionOption>) request.get("options");
        return ResponseEntity.ok(questionService.updateQuestion(id, question, options));
    }

    @DeleteMapping("/questions/{id}")
    public ResponseEntity<MessageResponse> deleteQuestion(@PathVariable Long id) {
        questionService.deleteQuestion(id);
        return ResponseEntity.ok(new MessageResponse("Question deleted successfully"));
    }

    @GetMapping("/questions/category/{categoryId}")
    public ResponseEntity<List<Question>> getQuestionsByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(questionService.getQuestionsByCategory(categoryId));
    }

    // Category Management
    @GetMapping("/categories")
    public ResponseEntity<List<QuestionCategory>> getAllCategories() {
        return ResponseEntity.ok(questionService.getAllCategories());
    }

    @PostMapping("/categories")
    public ResponseEntity<QuestionCategory> createCategory(@RequestBody QuestionCategory category) {
        return ResponseEntity.ok(questionService.createCategory(category));
    }

    // Exam Management
    @PostMapping("/exams/generate")
    public ResponseEntity<Exam> generateExam(
            @RequestBody Map<String, Object> request,
            Authentication authentication) {
        Long courseId = ((Number) request.get("courseId")).longValue();
        String title = (String) request.get("title");
        Integer duration = (Integer) request.get("durationMinutes");
        Map<Long, Integer> criteria = (Map<Long, Integer>) request.get("criteria");

        User teacher = userService.getUserByUsername(authentication.getName());

        Exam exam = examService.generateExam(courseId, teacher.getId(), title, duration, criteria);
        return ResponseEntity.ok(exam);
    }

    @PostMapping("/exams")
    public ResponseEntity<Exam> createExam(@RequestBody Map<String, Object> request) {
        Exam exam = (Exam) request.get("exam");
        List<ExamQuestion> questions = (List<ExamQuestion>) request.get("questions");
        return ResponseEntity.ok(examService.createExam(exam, questions));
    }

    @GetMapping("/exams")
    public ResponseEntity<List<Exam>> getAllExams() {
        return ResponseEntity.ok(examService.getPublishedExams());
    }

    @GetMapping("/exams/{id}")
    public ResponseEntity<Exam> getExamById(@PathVariable Long id) {
        Exam exam = examService.getExamById(id);
        exam.setExamQuestions(examService.getExamQuestions(id));
        return ResponseEntity.ok(exam);
    }

    @PutMapping("/exams/{id}")
    public ResponseEntity<Exam> updateExam(@PathVariable Long id, @RequestBody Exam exam) {
        return ResponseEntity.ok(examService.updateExam(id, exam));
    }

    @DeleteMapping("/exams/{id}")
    public ResponseEntity<MessageResponse> deleteExam(@PathVariable Long id) {
        examService.deleteExam(id);
        return ResponseEntity.ok(new MessageResponse("Exam deleted successfully"));
    }

    @PatchMapping("/exams/{id}/publish")
    public ResponseEntity<Exam> publishExam(
            @PathVariable Long id,
            @RequestBody Map<String, Boolean> request) {
        boolean published = request.getOrDefault("published", true);
        return ResponseEntity.ok(examService.publishExam(id, published));
    }

    @GetMapping("/exams/{id}/attempts")
    public ResponseEntity<List<ExamAttempt>> getExamAttempts(@PathVariable Long id) {
        return ResponseEntity.ok(attemptService.getAttemptsByExam(id));
    }

    // Grading
    @GetMapping("/grading/pending")
    public ResponseEntity<List<Map<String, Object>>> getPendingGrading() {
        return ResponseEntity.ok(gradingService.getPendingGradingItems());
    }

    @PostMapping("/grading/grade")
    public ResponseEntity<ManualGrade> submitGrade(
            @RequestBody Map<String, Object> request,
            Authentication authentication) {
        Long attemptId = ((Number) request.get("attemptId")).longValue();
        Long examQuestionId = ((Number) request.get("examQuestionId")).longValue();
        BigDecimal score = new BigDecimal(request.get("score").toString());
        String feedback = (String) request.get("feedback");

        User teacher = userService.getUserByUsername(authentication.getName());

        return ResponseEntity.ok(gradingService.submitManualGrade(
                attemptId, examQuestionId, teacher.getId(), score, feedback
        ));
    }

    @GetMapping("/attempts/{id}")
    public ResponseEntity<Map<String, Object>> getAttemptForGrading(@PathVariable Long id) {
        return ResponseEntity.ok(gradingService.getAttemptForGrading(id));
    }
}
