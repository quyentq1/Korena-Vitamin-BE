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

    @Autowired
    private GuestAccessControlRepository guestAccessRepository;

    @Autowired
    private jakarta.servlet.http.HttpServletRequest request;

    public ExamAttempt startExam(Long examId, Long studentId) {
        String clientIp = request.getRemoteAddr();

        // 1. Guest Logic (No Student ID)
        if (studentId == null) {
            GuestAccessControl access = guestAccessRepository.findById(clientIp)
                    .orElse(new GuestAccessControl(clientIp, 0, LocalDateTime.now()));

            if (access.getAttemptCount() >= 2) {
                throw new BadRequestException("LIMIT_EXCEEDED: You have used all your free guest tests (2/2). Please upgrade.");
            }

            access.setAttemptCount(access.getAttemptCount() + 1);
            guestAccessRepository.save(access);
            
            // For guest, we might create a temporary "Guest" user or handle null student in ExamAttempt
            // Ideally, ExamAttempt.student should be nullable or mapped to a generic Guest
            // For now, assuming we proceed without a User entity for Guest, or fetch a dummy "GUEST" user
            // To be safe and minimal: We will REQUIRE a valid User for ExamAttempt foreign key in current DB design.
            // If DB requires User, we can't insert null. 
            // Workaround: We return a "Mock" attempt object that isn't saved to DB? 
            // OR: We have a pre-seeded "GUEST_USER" in DB. Let's assume there is one or creating one.
            // For this task, strict requirement is "Guest" mode. 
            
            // Let's modify to find/create a temporary GUEST user for the record
            User guestUser = userRepository.findByUsername("guest_" + clientIp)
                    .orElseGet(() -> {
                         User u = new User();
                         u.setUsername("guest_" + clientIp);
                         u.setPassword("guest");
                         u.setFullName("Guest " + clientIp);
                         u.setRole(User.UserRole.GUEST);
                         u.setEmail("guest_" + clientIp + "@temp.com");
                         return userRepository.save(u);
                    });
             studentId = guestUser.getId();
        }

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

        // Type 1 & 2 Logic: Check Access (For non-guest / logged in users)
        // If it was a guest (converted to temp user above), they are GUEST role, so we skip premium check 
        // because we already checked IP limit.
        // Type 1 & 2 Logic: Check Access (For non-guest / logged in users)
        // If it was a guest (converted to temp user above), they are GUEST role, so we skip premium check 
        // because we already checked IP limit.
        // STUDENT role gets unlimited attempts. LEARNER role is subject to limits unless premium.
        if (student.getRole() != User.UserRole.GUEST && 
            student.getRole() != User.UserRole.STUDENT && 
            !student.getIsPremium()) {
             if (student.getFreeTestCount() > 0) {
                 student.setFreeTestCount(student.getFreeTestCount() - 1);
                 userRepository.save(student);
             } else {
                 throw new BadRequestException("You have used all your free tests. Please upgrade.");
             }
        }

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

    // --- Manual Grading ---

    public List<ExamAttempt> getPendingGradingAttempts() {
        return attemptRepository.findByStatus(ExamAttempt.AttemptStatus.PENDING_MANUAL_GRADE);
    }

    @Transactional
    public void submitManualGrade(Long attemptId, List<StudentAnswer> gradedAnswers) {
        ExamAttempt attempt = getAttemptById(attemptId);
        
        BigDecimal totalAscScore = attempt.getAutoScore() != null ? attempt.getAutoScore() : BigDecimal.ZERO;
        BigDecimal manualScore = BigDecimal.ZERO;

        for (StudentAnswer graded : gradedAnswers) {
            StudentAnswer answer = answerRepository.findById(graded.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Answer not found"));
            
            // Update score and feedback
            // Ensure we don't overwrite auto-graded ones if mixed? 
            // Usually manual grading is for Essay/Speaking which have 0 auto score.
            
            answer.setScore(graded.getScore());
            answer.setFeedback(graded.getFeedback());
            answer.setIsCorrect(graded.getScore().compareTo(BigDecimal.ZERO) > 0);
            
            answerRepository.save(answer);
            
            if (graded.getScore() != null) {
                manualScore = manualScore.add(graded.getScore());
            }
        }

        attempt.setTotalScore(totalAscScore.add(manualScore));
        attempt.setStatus(ExamAttempt.AttemptStatus.GRADED);
        attemptRepository.save(attempt);
    }
}
