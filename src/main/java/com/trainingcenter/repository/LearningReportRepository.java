package com.trainingcenter.repository;

import com.trainingcenter.entity.LearningReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LearningReportRepository extends JpaRepository<LearningReport, Long> {
    List<LearningReport> findByStudentId(Long studentId);
    List<LearningReport> findByClassEntityId(Long classId);
    List<LearningReport> findByTeacherId(Long teacherId);
    List<LearningReport> findByStudentIdAndClassEntityId(Long studentId, Long classId);
}
