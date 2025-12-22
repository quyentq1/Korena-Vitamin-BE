package com.trainingcenter.service;

import com.trainingcenter.entity.*;
import com.trainingcenter.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for TestAccessService
 * Tests critical business logic: 5 free tests limit
 */
@ExtendWith(MockitoExtension.class)
class TestAccessServiceTest {

    @Mock
    private TestAccessHistoryRepository testAccessHistoryRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private TestAccessService testAccessService;

    private User testUser;
    private Exam testExam;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setFreeTestCount(0);
        testUser.setIsPremium(false);

        testExam = new Exam();
        testExam.setId(1L);
    }

    @Test
    void canTakeFreeTest_WithinLimit_ReturnsTrue() {
        // Given: User has taken 3 free tests (< 5)
        when(testAccessHistoryRepository.countByUserIdAndAccessType(1L, TestAccessHistory.AccessType.FREE))
            .thenReturn(3L);

        // When
        boolean canTake = testAccessService.canTakeFreeTest(1L);

        // Then
        assertTrue(canTake, "User should be able to take free test when under limit");
    }

    @Test
    void canTakeFreeTest_AtLimit_ReturnsFalse() {
        // Given: User has taken 5 free tests (at limit)
        when(testAccessHistoryRepository.countByUserIdAndAccessType(1L, TestAccessHistory.AccessType.FREE))
            .thenReturn(5L);

        // When
        boolean canTake = testAccessService.canTakeFreeTest(1L);

        // Then
        assertFalse(canTake, "User should NOT be able to take free test when at limit");
    }

    @Test
    void getRemainingFreeTests_Returns_CorrectCount() {
        // Given: User has used 3 free tests
        when(testAccessHistoryRepository.countByUserIdAndAccessType(1L, TestAccessHistory.AccessType.FREE))
            .thenReturn(3L);

        // When
        int remaining = testAccessService.getRemainingFreeTests(1L);

        // Then
        assertEquals(2, remaining, "Should have 2 remaining free tests (5 - 3)");
    }

    @Test
    void recordFreeTestAccess_Success() {
        // Given
        when(testAccessHistoryRepository.countByUserIdAndAccessType(1L, TestAccessHistory.AccessType.FREE))
            .thenReturn(2L); // Under limit
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(testAccessHistoryRepository.save(any(TestAccessHistory.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        TestAccessHistory result = testAccessService.recordFreeTestAccess(1L, 1L);

        // Then
        assertNotNull(result);
        assertEquals(TestAccessHistory.AccessType.FREE, result.getAccessType());
        assertEquals(BigDecimal.ZERO, result.getPaymentAmount());
        
        // Verify user's free test count was incremented
        verify(userRepository).save(argThat(user -> user.getFreeTestCount() == 1));
    }

    @Test
    void recordFreeTestAccess_ExceedsLimit_ThrowsException() {
        // Given: User already used 5 free tests
        when(testAccessHistoryRepository.countByUserIdAndAccessType(1L, TestAccessHistory.AccessType.FREE))
            .thenReturn(5L);

        // When & Then
        assertThrows(Exception.class, () -> {
            testAccessService.recordFreeTestAccess(1L, 1L);
        }, "Should throw exception when free test limit exceeded");
    }

    @Test
    void recordPaidTestAccess_UpdatesUserToPremium() {
        // Given
        Payment payment = new Payment();
        payment.setId(1L);
        payment.setAmount(new BigDecimal("100000"));
        payment.setStatus(Payment.PaymentStatus.COMPLETED);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));
        when(testAccessHistoryRepository.save(any(TestAccessHistory.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        TestAccessHistory result = testAccessService.recordPaidTestAccess(1L, 1L, 1L);

        // Then
        assertNotNull(result);
        assertEquals(TestAccessHistory.AccessType.PAID, result.getAccessType());
        
        // Verify user was upgraded to premium
        verify(userRepository).save(argThat(user -> 
            user.getIsPremium() && user.getPaymentTier() != null));
    }

    @Test
    void getTestStats_ReturnsCorrectStatistics() {
        // Given
        when(testAccessHistoryRepository.countByUserIdAndAccessType(1L, TestAccessHistory.AccessType.FREE))
            .thenReturn(3L);
        when(testAccessHistoryRepository.countByUserIdAndAccessType(1L, TestAccessHistory.AccessType.PAID))
            .thenReturn(2L);

        // When
        TestAccessService.TestStats stats = testAccessService.getTestStats(1L);

        // Then
        assertEquals(3, stats.usedFreeTests());
        assertEquals(2, stats.remainingFreeTests());
        assertEquals(2, stats.paidTests());
        assertEquals(5, stats.totalTests());
    }
}
