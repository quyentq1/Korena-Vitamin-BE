package com.trainingcenter.repository;

import com.trainingcenter.entity.ClassTeacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClassTeacherRepository extends JpaRepository<ClassTeacher, Long> {
    List<ClassTeacher> findByClassEntityId(Long classId);
    List<ClassTeacher> findByTeacherId(Long teacherId);
    List<ClassTeacher> findByClassEntityIdAndIsPrimary(Long classId, Boolean isPrimary);
}
