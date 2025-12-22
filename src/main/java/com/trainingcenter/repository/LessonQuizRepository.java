package com.trainingcenter.repository;

import com.trainingcenter.entity.LessonQuiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LessonQuizRepository extends JpaRepository<LessonQuiz, Long> {
    List<LessonQuiz> findByClassEntityId(Long classId);
    Optional<LessonQuiz> findByClassEntityIdAndLessonNumber(Long classId, Integer lessonNumber);
    List<LessonQuiz> findByCreatedById(Long teacherId);
}
