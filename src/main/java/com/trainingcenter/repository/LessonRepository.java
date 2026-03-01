package com.trainingcenter.repository;

import com.trainingcenter.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {

    // Find all preview lessons for a specific course
    List<Lesson> findByCourseIdAndIsPreviewTrueAndActiveTrueOrderByLessonOrder(Long courseId);

    // Find all lessons for a course
    List<Lesson> findByCourseIdAndActiveTrueOrderByLessonOrder(Long courseId);

    // Count preview lessons for a course
    Long countByCourseIdAndIsPreviewTrueAndActiveTrue(Long courseId);
}
