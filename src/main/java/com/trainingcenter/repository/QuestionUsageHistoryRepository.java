package com.trainingcenter.repository;

import com.trainingcenter.entity.QuestionUsageHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Critical for Test Type 1 (FREE_PAID): Tracks used questions to prevent duplicates
 */
@Repository
public interface QuestionUsageHistoryRepository extends JpaRepository<QuestionUsageHistory, Long> {
    
    List<QuestionUsageHistory> findByUserId(Long userId);
    
    boolean existsByUserIdAndQuestionId(Long userId, Long questionId);
    
    /**
     * Get all question IDs already used by this user
     * Critical for filtering out used questions when generating new tests
     */
    @Query("SELECT quh.question.id FROM QuestionUsageHistory quh WHERE quh.user.id = :userId")
    List<Long> findUsedQuestionIdsByUserId(@Param("userId") Long userId);
}
