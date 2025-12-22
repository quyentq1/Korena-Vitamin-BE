package com.trainingcenter.repository;

import com.trainingcenter.entity.QuestionApproval;
import com.trainingcenter.entity.QuestionApproval.ApprovalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionApprovalRepository extends JpaRepository<QuestionApproval, Long> {
    Optional<QuestionApproval> findByQuestionId(Long questionId);
    List<QuestionApproval> findBySubmittedById(Long teacherId);
    List<QuestionApproval> findByReviewedById(Long educationManagerId);
    List<QuestionApproval> findByStatus(ApprovalStatus status);
    
    /**
     * Get pending approvals for Education Manager dashboard
     */
    @Query("SELECT COUNT(qa) FROM QuestionApproval qa WHERE qa.status = 'PENDING'")
    long countPendingApprovals();
}
