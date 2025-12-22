package com.trainingcenter.repository;

import com.trainingcenter.entity.TestAccessHistory;
import com.trainingcenter.entity.TestAccessHistory.AccessType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Critical for Test Type 1: Tracks 5 free tests limit + paid tests
 */
@Repository
public interface TestAccessHistoryRepository extends JpaRepository<TestAccessHistory, Long> {
    
    List<TestAccessHistory> findByUserId(Long userId);
    
    /**
     * Count free tests used by user (max 5)
     */
    long countByUserIdAndAccessType(Long userId, AccessType accessType);
    
    /**
     * Check if user has reached free test limit
     */
    default boolean hasReachedFreeTestLimit(Long userId) {
        return countByUserIdAndAccessType(userId, AccessType.FREE) >= 5;
    }
}
