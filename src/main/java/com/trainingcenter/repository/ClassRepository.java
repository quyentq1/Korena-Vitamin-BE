package com.trainingcenter.repository;

import com.trainingcenter.entity.ClassEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassRepository extends JpaRepository<ClassEntity, Long> {
    Optional<ClassEntity> findByClassCode(String classCode);
    List<ClassEntity> findByCourseId(Long courseId);
    List<ClassEntity> findByStatus(ClassEntity.ClassStatus status);
}
