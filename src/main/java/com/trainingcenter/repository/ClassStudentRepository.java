package com.trainingcenter.repository;

import com.trainingcenter.entity.ClassStudent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassStudentRepository extends JpaRepository<ClassStudent, Long> {
    List<ClassStudent> findByClassEntityId(Long classId);
    List<ClassStudent> findByStudentId(Long studentId);
    Optional<ClassStudent> findByClassEntityIdAndStudentId(Long classId, Long studentId);
    long countByClassEntityId(Long classId);
}
