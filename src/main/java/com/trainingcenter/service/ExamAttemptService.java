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

    @jakarta.persistence.PersistenceContext
    private jakarta.persistence.EntityManager entityManager;

    public ExamAttempt startExam(Long examId, Long studentId) {
        String clientIp = request.getRemoteAddr();

        // 1. Guest Logic (No Student ID)
        GuestAccessControl guestAccess = null; // track for post-save increment
        if (studentId == null) {
            // Find existing guest access or create new one and save immediately
            if (!guestAccessRepository.existsById(clientIp)) {
                // Create new guest access record
                guestAccess = new GuestAccessControl(clientIp, 0, LocalDateTime.now());
                guestAccess = guestAccessRepository.save(guestAccess);
            } else {
                guestAccess = guestAccessRepository.findById(clientIp).get();
            }

            if (guestAccess.getAttemptCount() >= 2) {
                throw new BadRequestException(
                        "LIMIT_EXCEEDED: You have used all your free guest tests (2/2). Please upgrade.");
            }

            // NOTE: We do NOT increment counter here.
            // Counter is incremented AFTER the attempt is saved successfully to avoid
            // consuming quota on failed requests.

            // Find or create a temporary GUEST user tied to this IP
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

        // For GUEST users: Check if already completed this exam (no retake allowed)
        if (student.getRole() == User.UserRole.GUEST) {
            boolean hasCompleted = attemptRepository.hasCompletedAttempt(student.getId(), exam.getId());
            if (hasCompleted) {
                throw new BadRequestException("Bạn đã làm bài này rồi. Mỗi bài test chỉ được làm 1 lần.");
            }
        }

        // Check if student already has an in-progress attempt (use IDs to avoid NonUniqueResultException)
        java.util.Optional<ExamAttempt> existingAttempt = attemptRepository.findByStudentAndExamAndStatus(
                student.getId(), exam.getId(), ExamAttempt.AttemptStatus.IN_PROGRESS.name());

        if (existingAttempt.isPresent()) {
            if (student.getRole() == User.UserRole.GUEST) {
                // For guest: resume the existing attempt instead of blocking
                return existingAttempt.get();
            } else {
                // For logged-in users: strict block to avoid duplicates
                throw new BadRequestException("You already have an in-progress attempt for this exam");
            }
        }

        // Type 1 & 2 Logic: Check Access (For non-guest / logged in users)
        // If it was a guest (converted to temp user above), they are GUEST role, so we
        // skip premium check
        // because we already checked IP limit.
        // Type 1 & 2 Logic: Check Access (For non-guest / logged in users)
        // If it was a guest (converted to temp user above), they are GUEST role, so we
        // skip premium check
        // because we already checked IP limit.
        // STUDENT role gets unlimited attempts. LEARNER role is subject to limits
        // unless premium.
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

        ExamAttempt saved = attemptRepository.save(attempt);

        // Increment guest attempt counter AFTER successful save
        if (guestAccess != null) {
            guestAccess.setAttemptCount(guestAccess.getAttemptCount() + 1);
            guestAccess.setLastAttemptAt(LocalDateTime.now());
            guestAccessRepository.save(guestAccess);
        }

        return saved;
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

        ExamAttempt saved = attemptRepository.save(attempt);

        // Eagerly fetch answers with all details (question, options) for result display
        // Without this, the LAZY fetch causes answers to be null in JSON response
        List<StudentAnswer> answers = answerRepository.findByAttempt_Id(attemptId);
        saved.setAnswers(answers);

        return saved;
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
