package com.trainingcenter.repository;

import com.trainingcenter.entity.ExamPatternDistribution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamPatternDistributionRepository extends JpaRepository<ExamPatternDistribution, Long> {
    List<ExamPatternDistribution> findByExamId(Long examId);
    void deleteByExamId(Long examId);
}
