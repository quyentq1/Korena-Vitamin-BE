package com.trainingcenter.repository;

import com.trainingcenter.entity.ExamVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExamVariantRepository extends JpaRepository<ExamVariant, Long> {
    List<ExamVariant> findByExamId(Long examId);
    List<ExamVariant> findByExamIdAndIsActive(Long examId, Boolean isActive);
    Optional<ExamVariant> findByExamIdAndVariantCode(Long examId, String variantCode);
    long countByExamId(Long examId);
}
