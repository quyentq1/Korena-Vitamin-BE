package com.trainingcenter.repository;

import com.trainingcenter.entity.Course;
import com.trainingcenter.entity.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {
    List<Exam> findByCourse(Course course);
    List<Exam> findByPublished(Boolean published);
    List<Exam> findByCourseAndPublished(Course course, Boolean published);
    List<Exam> findByCreatedById(Long createdById);

    // Search exams by keyword in title or description
    @Query("SELECT e.id, e.title, e.description, e.durationMinutes, " +
           "(SELECT COUNT(q) FROM ExamQuestion eq WHERE eq.exam = e), " +
           "c.name FROM Exam e " +
           "LEFT JOIN e.course c " +
           "WHERE e.published = true AND (" +
           "LOWER(e.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(e.description) LIKE LOWER(CONCAT('%', :keyword, '%')))" +
           ") ORDER BY e.title")
    List<Object[]> searchExams(@Param("keyword") String keyword);
}
