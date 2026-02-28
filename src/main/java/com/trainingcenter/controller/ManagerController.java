package com.trainingcenter.controller;

import com.trainingcenter.entity.Question;
import com.trainingcenter.entity.User;
import com.trainingcenter.exception.ResourceNotFoundException;
import com.trainingcenter.repository.QuestionRepository;
import com.trainingcenter.repository.UserRepository;
import com.trainingcenter.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/manager")
@PreAuthorize("hasRole('ADMIN')") // Should be MANAGER role, using ADMIN for now implies Manager
public class ManagerController {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuestionService questionService;

    // --- Question Approval ---

    @GetMapping("/questions/pending")
    public ResponseEntity<List<Question>> getPendingQuestions() {
        return ResponseEntity.ok(questionRepository.findByVerificationStatus(Question.VerificationStatus.PENDING));
    }

    @PostMapping("/questions/{id}/approve")
    public ResponseEntity<?> approveQuestion(@PathVariable Long id) {
        Question q = questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found"));
        q.setVerificationStatus(Question.VerificationStatus.APPROVED);
        q.setActive(true);
        questionRepository.save(q);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/questions/{id}/reject")
    public ResponseEntity<?> rejectQuestion(@PathVariable Long id) {
        Question q = questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found"));
        q.setVerificationStatus(Question.VerificationStatus.REJECTED);
        q.setActive(false);
        questionRepository.save(q);
        return ResponseEntity.ok().build();
    }

    // --- Duplicate Check ---

    @PostMapping("/questions/check-duplicate")
    public ResponseEntity<?> checkDuplicate(@RequestBody Map<String, String> request) {
        String newText = request.get("text");
        // Simple distinct check or exact match
        // Real implementation would use Levenshtein or fuzzy search
        boolean exists = questionRepository.existsByQuestionText(newText);
        return ResponseEntity.ok(Map.of("isDuplicate", exists, "score", exists ? 100 : 0));
    }
}
