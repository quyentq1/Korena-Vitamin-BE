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
 * Unit tests for PaymentService
 * Tests payment tier logic (100k, 200k)
 */
@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PaymentMethodRepository paymentMethodRepository;

    @Mock
    private InvoiceRepository invoiceRepository;

    @InjectMocks
    private PaymentService paymentService;

    private User testUser;
    private PaymentMethod testPaymentMethod;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        testPaymentMethod = new PaymentMethod();
        testPaymentMethod.setId(1L);
        testPaymentMethod.setCode("BANK_TRANSFER");
    }

    @Test
    void createTestUnlockPayment_Tier1_Success() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(paymentRepository.save(any(Payment.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Payment payment = paymentService.createTestUnlockPayment(1L, 1L, PaymentService.TEST_UNLOCK_TIER_1);

        // Then
        assertNotNull(payment);
        assertEquals(Payment.PaymentType.TEST_UNLOCK, payment.getPaymentType());
        assertEquals(new BigDecimal("100000"), payment.getAmount());
        assertEquals(Payment.PaymentStatus.PENDING, payment.getStatus());
    }

    @Test
    void createTestUnlockPayment_InvalidAmount_ThrowsException() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        BigDecimal invalidAmount = new BigDecimal("50000"); // Not 100k or 200k

        // When & Then
        assertThrows(Exception.class, () -> {
            paymentService.createTestUnlockPayment(1L, 1L, invalidAmount);
        }, "Should throw exception for invalid payment amount");
    }

    @Test
    void completePayment_UpdatesUserToTier1() {
        // Given
        Payment payment = new Payment();
        payment.setId(1L);
        payment.setUser(testUser);
        payment.setPaymentType(Payment.PaymentType.TEST_UNLOCK);
        payment.setAmount(PaymentService.TEST_UNLOCK_TIER_1);
        payment.setStatus(Payment.PaymentStatus.PENDING);

        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));
        when(paymentMethodRepository.findById(1L)).thenReturn(Optional.of(testPaymentMethod));
        when(paymentRepository.save(any(Payment.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
        when(invoiceRepository.save(any(Invoice.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Payment completed = paymentService.completePayment(1L, "TXN123", 1L);

        // Then
        assertEquals(Payment.PaymentStatus.COMPLETED, completed.getStatus());
        assertNotNull(completed.getPaidAt());
        
        // Verify user tier updated
        verify(userRepository).save(argThat(user -> 
            "TIER_1_100K".equals(user.getPaymentTier()) && user.getIsPremium()));
    }

    @Test
    void completePayment_CreatesInvoice() {
        // Given
        Payment payment = new Payment();
        payment.setId(1L);
        payment.setUser(testUser);
        payment.setPaymentType(Payment.PaymentType.COURSE_FEE);
        payment.setAmount(new BigDecimal("1500000"));
        payment.setStatus(Payment.PaymentStatus.PENDING);

        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));
        when(paymentMethodRepository.findById(1L)).thenReturn(Optional.of(testPaymentMethod));
        when(paymentRepository.save(any(Payment.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
        when(invoiceRepository.save(any(Invoice.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        paymentService.completePayment(1L, "TXN123", 1L);

        // Then
        verify(invoiceRepository).save(argThat(invoice -> 
            invoice.getPayment().equals(payment) && 
            invoice.getInvoiceNumber() != null));
    }
}
