package com.trainingcenter.repository;

import com.trainingcenter.entity.Payment;
import com.trainingcenter.entity.Payment.PaymentType;
import com.trainingcenter.entity.Payment.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByUserId(Long userId);
    List<Payment> findByStatus(PaymentStatus status);
    List<Payment> findByPaymentType(PaymentType paymentType);
    List<Payment> findByUserIdAndPaymentType(Long userId, PaymentType paymentType);
}
