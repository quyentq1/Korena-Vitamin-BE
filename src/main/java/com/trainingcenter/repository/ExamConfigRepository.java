package com.trainingcenter.repository;

import com.trainingcenter.entity.ExamConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExamConfigRepository extends JpaRepository<ExamConfig, Long> {
    Optional<ExamConfig> findByExamId(Long examId);
    void deleteByExamId(Long examId);
}
