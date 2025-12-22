package com.trainingcenter.repository;

import com.trainingcenter.entity.ScheduleChangeRequest;
import com.trainingcenter.entity.ScheduleChangeRequest.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleChangeRequestRepository extends JpaRepository<ScheduleChangeRequest, Long> {
    List<ScheduleChangeRequest> findByRequestedById(Long teacherId);
    List<ScheduleChangeRequest> findByStatus(RequestStatus status);
    List<ScheduleChangeRequest> findByReviewedById(Long educationManagerId);
    long countByStatus(RequestStatus status);
}
