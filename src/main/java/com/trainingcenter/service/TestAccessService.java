package com.trainingcenter.service;

import com.trainingcenter.entity.*;
import com.trainingcenter.exception.BadRequestException;
import com.trainingcenter.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Test Access Service
 * CRITICAL BUSINESS LOGIC: Manages 5 free tests limit and paid test access
 */
@Service
@RequiredArgsConstructor
public class TestAccessService {

    private final TestAccessHistoryRepository testAccessHistoryRepository;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;

    /**
     * BUG-03 FIX: Giới hạn free test = 2 (cả Guest lẫn User chưa đóng tiền)
     * - GUEST (chưa đăng nhập): 2 bài, tracking theo IP qua GuestAccessControl
     * - USER đã đăng ký nhưng chưa đóng học phí: 2 bài, tracking theo userId
     * - STUDENT (đã đóng học phí toàn khoá): UNLIMITED - bypass hoàn toàn
     */
    private static final int FREE_TEST_LIMIT = 2;

    /**
     * Check if user can take a free test
     * Business Rule: First 5 tests are FREE
     */
    public boolean canTakeFreeTest(Long userId) {
        long freeTestCount = testAccessHistoryRepository
                .countByUserIdAndAccessType(userId, TestAccessHistory.AccessType.FREE);
        return freeTestCount < FREE_TEST_LIMIT;
    }

    /**
     * Get remaining free tests for user
     */
    public int getRemainingFreeTests(Long userId) {
        long usedFreeTests = testAccessHistoryRepository
                .countByUserIdAndAccessType(userId, TestAccessHistory.AccessType.FREE);
        return Math.max(0, FREE_TEST_LIMIT - (int) usedFreeTests);
    }

    /**
     * Record free test access
     * Validates that user hasn't exceeded free test limit
     */
    @Transactional
    public TestAccessHistory recordFreeTestAccess(Long userId, Long examId) {
        if (!canTakeFreeTest(userId)) {
            throw new BadRequestException("Đã hết lượt thi miễn phí. Vui lòng thanh toán để tiếp tục.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("User not found"));

        Exam exam = new Exam();
        exam.setId(examId);

        TestAccessHistory access = new TestAccessHistory();
        access.setUser(user);
        access.setExam(exam);
        access.setAccessType(TestAccessHistory.AccessType.FREE);
        access.setPaymentAmount(BigDecimal.ZERO);

        // Update user's free test count
        user.setFreeTestCount(user.getFreeTestCount() + 1);
        userRepository.save(user);

        return testAccessHistoryRepository.save(access);
    }

    /**
     * Record paid test access after payment completed
     * Business Rule: Payment amounts are 100k, 200k, etc.
     */
    @Transactional
    public TestAccessHistory recordPaidTestAccess(Long userId, Long examId, Long paymentId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("User not found"));

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new BadRequestException("Payment not found"));

        if (payment.getStatus() != Payment.PaymentStatus.COMPLETED) {
            throw new BadRequestException("Payment not completed");
        }

        Exam exam = new Exam();
        exam.setId(examId);

        TestAccessHistory access = new TestAccessHistory();
        access.setUser(user);
        access.setExam(exam);
        access.setAccessType(TestAccessHistory.AccessType.PAID);
        access.setPaymentAmount(payment.getAmount());

        // Update user to premium/learner status
        user.setIsPremium(true);
        user.setPaymentTier(payment.getAmount().toString());
        userRepository.save(user);

        return testAccessHistoryRepository.save(access);
    }

    /**
     * Get all test access history for user
     */
    public List<TestAccessHistory> getUserTestHistory(Long userId) {
        return testAccessHistoryRepository.findByUserId(userId);
    }

    /**
     * Get test statistics for user
     */
    public TestStats getTestStats(Long userId) {
        long freeTests = testAccessHistoryRepository
                .countByUserIdAndAccessType(userId, TestAccessHistory.AccessType.FREE);
        long paidTests = testAccessHistoryRepository
                .countByUserIdAndAccessType(userId, TestAccessHistory.AccessType.PAID);

        return new TestStats(
                (int) freeTests,
                getRemainingFreeTests(userId),
                (int) paidTests,
                freeTests + paidTests);
    }

    public record TestStats(
            int usedFreeTests,
            int remainingFreeTests,
            int paidTests,
            long totalTests) {
    }
}
