package com.trainingcenter.repository;

import com.trainingcenter.entity.ManualGrade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ManualGradeRepository extends JpaRepository<ManualGrade, Long> {
    List<ManualGrade> findByAttemptId(Long attemptId);
    Optional<ManualGrade> findByAttemptIdAndExamQuestionId(Long attemptId, Long examQuestionId);
    List<ManualGrade> findByGradedById(Long gradedById);
}
