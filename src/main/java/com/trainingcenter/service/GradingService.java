package com.trainingcenter.service;

import com.trainingcenter.entity.*;
import com.trainingcenter.exception.BadRequestException;
import com.trainingcenter.exception.ResourceNotFoundException;
import com.trainingcenter.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class GradingService {

    @Autowired
    private ExamAttemptRepository attemptRepository;

    @Autowired
    private ExamQuestionRepository examQuestionRepository;

    @Autowired
    private StudentAnswerRepository answerRepository;

    @Autowired
    private ManualGradeRepository gradeRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Map<String, Object>> getPendingGradingItems() {
        List<ExamAttempt> pendingAttempts = attemptRepository.findByStatus(
                ExamAttempt.AttemptStatus.PENDING_MANUAL_GRADE
        );

        List<Map<String, Object>> gradingItems = new ArrayList<>();

        for (ExamAttempt attempt : pendingAttempts) {
            List<StudentAnswer> answers = answerRepository.findByAttemptId(attempt.getId());

            for (StudentAnswer answer : answers) {
                Question question = answer.getExamQuestion().getQuestion();

                // Only include questions that need manual grading
                if (question.getQuestionType() == Question.QuestionType.ESSAY ||
                        question.getQuestionType() == Question.QuestionType.SPEAKING ||
                        question.getQuestionType() == Question.QuestionType.LISTENING) {

                    // Check if already graded
                    if (gradeRepository.findByAttemptIdAndExamQuestionId(
                            attempt.getId(), answer.getExamQuestion().getId()).isEmpty()) {

                        Map<String, Object> item = new HashMap<>();
                        item.put("attemptId", attempt.getId());
                        item.put("studentName", attempt.getStudent().getFullName());
                        item.put("examTitle", attempt.getExam().getTitle());
                        item.put("questionId", question.getId());
                        item.put("questionText", question.getQuestionText());
                        item.put("questionType", question.getQuestionType());
                        item.put("maxPoints", answer.getExamQuestion().getPoints());
                        item.put("answerText", answer.getAnswerText());
                        item.put("answerFileUrl", answer.getAnswerFileUrl());
                        item.put("submittedAt", answer.getAnsweredAt());

                        gradingItems.add(item);
                    }
                }
            }
        }

        return gradingItems;
    }

    public ManualGrade submitManualGrade(Long attemptId, Long examQuestionId, Long gradedBy,
                                          BigDecimal score, String feedback) {
        ExamAttempt attempt = attemptRepository.findById(attemptId)
                .orElseThrow(() -> new ResourceNotFoundException("Attempt not found"));

        ExamQuestion examQuestion = examQuestionRepository.findById(examQuestionId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam question not found"));

        User grader = userRepository.findById(gradedBy)
                .orElseThrow(() -> new ResourceNotFoundException("Grader not found"));

        StudentAnswer answer = answerRepository.findByAttemptIdAndExamQuestionId(attemptId, examQuestionId)
                .orElseThrow(() -> new ResourceNotFoundException("Answer not found"));

        // Validate score
        if (score.compareTo(BigDecimal.ZERO) < 0 ||
                score.compareTo(BigDecimal.valueOf(examQuestion.getPoints())) > 0) {
            throw new BadRequestException("Score must be between 0 and " + examQuestion.getPoints());
        }

        // Check if already graded
        ManualGrade existingGrade = gradeRepository.findByAttemptIdAndExamQuestionId(attemptId, examQuestionId)
                .orElse(null);

        ManualGrade grade;
        if (existingGrade != null) {
            // Update existing grade
            grade = existingGrade;
            grade.setScore(score);
            grade.setFeedback(feedback);
            grade.setGradedBy(grader);
            grade.setGradedAt(LocalDateTime.now());
        } else {
            // Create new grade
            grade = new ManualGrade();
            grade.setAttempt(attempt);
            grade.setExamQuestion(examQuestion);
            grade.setGradedBy(grader);
            grade.setScore(score);
            grade.setFeedback(feedback);
            grade.setGradedAt(LocalDateTime.now());
        }

        grade = gradeRepository.save(grade);

        // Update student answer score
        answer.setScore(score);
        answerRepository.save(answer);

        // Check if all questions are graded and finalize
        checkAndFinalizeGrades(attemptId);

        return grade;
    }

    private void checkAndFinalizeGrades(Long attemptId) {
        ExamAttempt attempt = attemptRepository.findById(attemptId)
                .orElseThrow(() -> new ResourceNotFoundException("Attempt not found"));

        List<ExamQuestion> examQuestions = examQuestionRepository.findByExamIdOrderByQuestionOrder(attempt.getExam().getId());
        List<ManualGrade> manualGrades = gradeRepository.findByAttemptId(attemptId);

        // Count questions that need manual grading
        int needsManualGrading = 0;
        for (ExamQuestion eq : examQuestions) {
            Question.QuestionType type = eq.getQuestion().getQuestionType();
            if (type == Question.QuestionType.ESSAY ||
                    type == Question.QuestionType.SPEAKING ||
                    type == Question.QuestionType.LISTENING) {
                needsManualGrading++;
            }
        }

        // If all manual grades submitted, finalize
        if (manualGrades.size() >= needsManualGrading) {
            BigDecimal manualScore = manualGrades.stream()
                    .map(ManualGrade::getScore)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            attempt.setManualScore(manualScore);
            attempt.setTotalScore(attempt.getAutoScore().add(manualScore));
            attempt.setStatus(ExamAttempt.AttemptStatus.GRADED);

            attemptRepository.save(attempt);
        }
    }

    public Map<String, Object> getAttemptForGrading(Long attemptId) {
        ExamAttempt attempt = attemptRepository.findById(attemptId)
                .orElseThrow(() -> new ResourceNotFoundException("Attempt not found"));

        List<StudentAnswer> answers = answerRepository.findByAttemptId(attemptId);
        List<ManualGrade> grades = gradeRepository.findByAttemptId(attemptId);

        Map<String, Object> result = new HashMap<>();
        result.put("attempt", attempt);
        result.put("student", attempt.getStudent());
        result.put("exam", attempt.getExam());
        result.put("answers", answers);
        result.put("manualGrades", grades);

        return result;
    }

    public List<ManualGrade> getGradesByAttempt(Long attemptId) {
        return gradeRepository.findByAttemptId(attemptId);
    }
}
