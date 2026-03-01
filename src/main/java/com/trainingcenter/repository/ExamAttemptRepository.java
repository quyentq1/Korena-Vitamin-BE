package com.trainingcenter.repository;

import com.trainingcenter.entity.Exam;
import com.trainingcenter.entity.ExamAttempt;
import com.trainingcenter.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExamAttemptRepository extends JpaRepository<ExamAttempt, Long> {
    List<ExamAttempt> findByExam(Exam exam);
    List<ExamAttempt> findByStudent(User student);
    List<ExamAttempt> findByStudentAndExam(User student, Exam exam);
    List<ExamAttempt> findByStatus(ExamAttempt.AttemptStatus status);

    // Use native query with LIMIT to avoid NonUniqueResultException if duplicates exist
    @Query(nativeQuery = true, value = "SELECT * FROM exam_attempts WHERE student_id = :studentId AND exam_id = :examId AND status = :status ORDER BY created_at DESC LIMIT 1")
    Optional<ExamAttempt> findByStudentAndExamAndStatus(@Param("studentId") Long studentId, @Param("examId") Long examId, @Param("status") String status);

    // Check if student has any completed attempt (SUBMITTED) for this exam
    // Use JPQL with CASE WHEN to return proper Boolean instead of native query Long
    @Query("SELECT CASE WHEN COUNT(ea) > 0 THEN true ELSE false END FROM ExamAttempt ea WHERE ea.student.id = :studentId AND ea.exam.id = :examId AND ea.status = 'SUBMITTED'")
    boolean hasCompletedAttempt(@Param("studentId") Long studentId, @Param("examId") Long examId);
}
