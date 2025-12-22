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
import java.util.List;

@Service
@Transactional
public class ExamAttemptService {

    @Autowired
    private ExamAttemptRepository attemptRepository;

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private ExamQuestionRepository examQuestionRepository;

    @Autowired
    private StudentAnswerRepository answerRepository;

    @Autowired
    private QuestionOptionRepository optionRepository;

    @Autowired
    private UserRepository userRepository;

    public ExamAttempt startExam(Long examId, Long studentId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found"));

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        if (!exam.getPublished()) {
            throw new BadRequestException("Exam is not published yet");
        }

        // Check if student already has an in-progress attempt
        attemptRepository.findByStudentAndExamAndStatus(
                student, exam, ExamAttempt.AttemptStatus.IN_PROGRESS
        ).ifPresent(attempt -> {
            throw new BadRequestException("You already have an in-progress attempt for this exam");
        });

        ExamAttempt attempt = new ExamAttempt();
        attempt.setExam(exam);
        attempt.setStudent(student);
        attempt.setStartTime(LocalDateTime.now());
        attempt.setEndTime(LocalDateTime.now().plusMinutes(exam.getDurationMinutes()));
        attempt.setStatus(ExamAttempt.AttemptStatus.IN_PROGRESS);

        return attemptRepository.save(attempt);
    }

    public StudentAnswer submitAnswer(Long attemptId, Long examQuestionId, String answerText, String answerFileUrl) {
        ExamAttempt attempt = attemptRepository.findById(attemptId)
                .orElseThrow(() -> new ResourceNotFoundException("Attempt not found"));

        if (attempt.getStatus() != ExamAttempt.AttemptStatus.IN_PROGRESS) {
            throw new BadRequestException("This exam attempt is not in progress");
        }

        ExamQuestion examQuestion = examQuestionRepository.findById(examQuestionId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam question not found"));

        // Check or create answer
        StudentAnswer answer = answerRepository.findByAttemptIdAndExamQuestionId(attemptId, examQuestionId)
                .orElse(new StudentAnswer());

        answer.setAttempt(attempt);
        answer.setExamQuestion(examQuestion);
        answer.setAnswerText(answerText);
        answer.setAnswerFileUrl(answerFileUrl);
        answer.setAnsweredAt(LocalDateTime.now());

        return answerRepository.save(answer);
    }

    public ExamAttempt submitExam(Long attemptId) {
        ExamAttempt attempt = attemptRepository.findById(attemptId)
                .orElseThrow(() -> new ResourceNotFoundException("Attempt not found"));

        if (attempt.getStatus() != ExamAttempt.AttemptStatus.IN_PROGRESS) {
            throw new BadRequestException("This exam attempt is already submitted");
        }

        attempt.setSubmitTime(LocalDateTime.now());
        attempt.setStatus(ExamAttempt.AttemptStatus.SUBMITTED);

        // Auto-grade the attempt
        autoGradeAttempt(attempt);

        return attemptRepository.save(attempt);
    }

    private void autoGradeAttempt(ExamAttempt attempt) {
        List<StudentAnswer> answers = answerRepository.findByAttemptId(attempt.getId());
        BigDecimal autoScore = BigDecimal.ZERO;
        boolean needsManualGrading = false;

        for (StudentAnswer answer : answers) {
            Question question = answer.getExamQuestion().getQuestion();
            Question.QuestionType type = question.getQuestionType();

            if (type == Question.QuestionType.MULTIPLE_CHOICE) {
                // Auto-grade multiple choice
                List<QuestionOption> options = optionRepository.findByQuestionIdOrderByOptionOrder(question.getId());
                boolean isCorrect = false;

                for (QuestionOption option : options) {
                    if (option.getIsCorrect() && answer.getAnswerText() != null &&
                            answer.getAnswerText().equals(option.getId().toString())) {
                        isCorrect = true;
                        break;
                    }
                }

                if (isCorrect) {
                    BigDecimal points = BigDecimal.valueOf(answer.getExamQuestion().getPoints());
                    answer.setScore(points);
                    answer.setIsCorrect(true);
                    autoScore = autoScore.add(points);
                } else {
                    answer.setScore(BigDecimal.ZERO);
                    answer.setIsCorrect(false);
                }

                answerRepository.save(answer);

            } else if (type == Question.QuestionType.SHORT_ANSWER) {
                // Simple auto-grading for short answer (exact match)
                if (answer.getAnswerText() != null &&
                        answer.getAnswerText().trim().equalsIgnoreCase(question.getCorrectAnswer().trim())) {
                    BigDecimal points = BigDecimal.valueOf(answer.getExamQuestion().getPoints());
                    answer.setScore(points);
                    answer.setIsCorrect(true);
                    autoScore = autoScore.add(points);
                } else {
                    answer.setScore(BigDecimal.ZERO);
                    answer.setIsCorrect(false);
                }

                answerRepository.save(answer);

            } else {
                // Essay, Speaking, Listening need manual grading
                needsManualGrading = true;
            }
        }

        attempt.setAutoScore(autoScore);

        if (needsManualGrading) {
            attempt.setStatus(ExamAttempt.AttemptStatus.PENDING_MANUAL_GRADE);
        } else {
            attempt.setTotalScore(autoScore);
            attempt.setStatus(ExamAttempt.AttemptStatus.GRADED);
        }
    }

    public ExamAttempt getAttemptById(Long id) {
        return attemptRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attempt not found with id: " + id));
    }

    public List<ExamAttempt> getStudentAttempts(Long studentId) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        return attemptRepository.findByStudent(student);
    }

    public List<ExamAttempt> getAttemptsByExam(Long examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found"));
        return attemptRepository.findByExam(exam);
    }

    public List<StudentAnswer> getAttemptAnswers(Long attemptId) {
        return answerRepository.findByAttemptId(attemptId);
    }
}
