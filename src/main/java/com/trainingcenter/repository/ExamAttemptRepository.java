package com.trainingcenter.repository;

import com.trainingcenter.entity.Exam;
import com.trainingcenter.entity.ExamAttempt;
import com.trainingcenter.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExamAttemptRepository extends JpaRepository<ExamAttempt, Long> {
    List<ExamAttempt> findByExam(Exam exam);
    List<ExamAttempt> findByStudent(User student);
    List<ExamAttempt> findByStudentAndExam(User student, Exam exam);
    List<ExamAttempt> findByStatus(ExamAttempt.AttemptStatus status);
    Optional<ExamAttempt> findByStudentAndExamAndStatus(User student, Exam exam, ExamAttempt.AttemptStatus status);
}
