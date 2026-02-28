package com.trainingcenter.repository;

import com.trainingcenter.entity.ConsultationRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConsultationRequestRepository extends JpaRepository<ConsultationRequest, Long> {
    List<ConsultationRequest> findByStatusOrderByCreatedAtDesc(ConsultationRequest.ConsultationStatus status);

    List<ConsultationRequest> findAllByOrderByCreatedAtDesc();
}
