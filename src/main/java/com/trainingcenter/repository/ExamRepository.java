package com.trainingcenter.repository;

import com.trainingcenter.entity.Course;
import com.trainingcenter.entity.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {
    List<Exam> findByCourse(Course course);
    List<Exam> findByPublished(Boolean published);
    List<Exam> findByCourseAndPublished(Course course, Boolean published);
    List<Exam> findByCreatedById(Long createdById);
}
