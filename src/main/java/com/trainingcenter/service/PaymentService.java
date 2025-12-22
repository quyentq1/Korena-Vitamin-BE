package com.trainingcenter.service;

import com.trainingcenter.entity.*;
import com.trainingcenter.exception.BadRequestException;
import com.trainingcenter.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * Payment Service
 * Handles course fee payments and test unlock payments (100k, 200k)
 */
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final InvoiceRepository invoiceRepository;

    public static final BigDecimal TEST_UNLOCK_TIER_1 = new BigDecimal("100000"); // 100k VND
    public static final BigDecimal TEST_UNLOCK_TIER_2 = new BigDecimal("200000"); // 200k VND

    /**
     * Create test unlock payment
     * Business Rule: 100k or 200k to unlock additional tests
     */
    @Transactional
    public Payment createTestUnlockPayment(Long userId, Long examId, BigDecimal amount) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BadRequestException("User not found"));

        // Validate amount
        if (!amount.equals(TEST_UNLOCK_TIER_1) && !amount.equals(TEST_UNLOCK_TIER_2)) {
            throw new BadRequestException("Invalid payment amount. Must be 100,000 or 200,000 VND");
        }

        Payment payment = new Payment();
        payment.setUser(user);
        payment.setPaymentType(Payment.PaymentType.TEST_UNLOCK);
        payment.setRelatedId(examId);
        payment.setAmount(amount);
        payment.setStatus(Payment.PaymentStatus.PENDING);

        return paymentRepository.save(payment);
    }

    /**
     * Create course fee payment
     */
    @Transactional
    public Payment createCourseFeePayment(Long userId, Long courseId, BigDecimal amount) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BadRequestException("User not found"));

        Payment payment = new Payment();
        payment.setUser(user);
        payment.setPaymentType(Payment.PaymentType.COURSE_FEE);
        payment.setRelatedId(courseId);
        payment.setAmount(amount);
        payment.setStatus(Payment.PaymentStatus.PENDING);

        return paymentRepository.save(payment);
    }

    /**
     * Complete payment
     * Updates payment status and creates invoice
     */
    @Transactional
    public Payment completePayment(Long paymentId, String transactionId, Long paymentMethodId) {
        Payment payment = paymentRepository.findById(paymentId)
            .orElseThrow(() -> new BadRequestException("Payment not found"));

        if (payment.getStatus() == Payment.PaymentStatus.COMPLETED) {
            throw new BadRequestException("Payment already completed");
        }

        PaymentMethod paymentMethod = paymentMethodRepository.findById(paymentMethodId)
            .orElseThrow(() -> new BadRequestException("Payment method not found"));

        payment.setStatus(Payment.PaymentStatus.COMPLETED);
        payment.setTransactionId(transactionId);
        payment.setPaymentMethod(paymentMethod);
        payment.setPaidAt(java.time.LocalDateTime.now());

        Payment savedPayment = paymentRepository.save(payment);

        // Create invoice
        createInvoice(savedPayment);

        // Update user tier if test unlock payment
        if (payment.getPaymentType() == Payment.PaymentType.TEST_UNLOCK) {
            updateUserTier(payment.getUser(), payment.getAmount());
        }

        return savedPayment;
    }

    /**
     * Create invoice for completed payment
     */
    private void createInvoice(Payment payment) {
        String invoiceNumber = generateInvoiceNumber(payment.getId());

        Invoice invoice = new Invoice();
        invoice.setPayment(payment);
        invoice.setInvoiceNumber(invoiceNumber);
        invoice.setIssueDate(java.time.LocalDate.now());
        invoice.setTotalAmount(payment.getAmount());

        invoiceRepository.save(invoice);
    }

    /**
     * Update user's payment tier after test unlock payment
     */
    private void updateUserTier(User user, BigDecimal amount) {
        if (amount.equals(TEST_UNLOCK_TIER_1)) {
            user.setPaymentTier("TIER_1_100K");
        } else if (amount.equals(TEST_UNLOCK_TIER_2)) {
            user.setPaymentTier("TIER_2_200K");
        }
        user.setIsPremium(true);
        userRepository.save(user);
    }

    /**
     * Generate unique invoice number
     */
    private String generateInvoiceNumber(Long paymentId) {
        return String.format("INV-%d-%08d", 
            java.time.LocalDate.now().getYear(), 
            paymentId);
    }

    /**
     * Get user's payment history
     */
    public java.util.List<Payment> getUserPayments(Long userId) {
        return paymentRepository.findByUserId(userId);
    }

    /**
     * Refund payment
     */
    @Transactional
    public Payment refundPayment(Long paymentId, String reason) {
        Payment payment = paymentRepository.findById(paymentId)
            .orElseThrow(() -> new BadRequestException("Payment not found"));

        if (payment.getStatus() != Payment.PaymentStatus.COMPLETED) {
            throw new BadRequestException("Can only refund completed payments");
        }

        payment.setStatus(Payment.PaymentStatus.REFUNDED);
        return paymentRepository.save(payment);
    }
}
