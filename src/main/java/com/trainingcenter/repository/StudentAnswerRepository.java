package com.trainingcenter.repository;

import com.trainingcenter.entity.StudentAnswer;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentAnswerRepository extends JpaRepository<StudentAnswer, Long> {
    List<StudentAnswer> findByAttemptId(Long attemptId);
    Optional<StudentAnswer> findByAttemptIdAndExamQuestionId(Long attemptId, Long examQuestionId);

    // Eagerly fetch attempt, examQuestion, and nested question/relationships for result display
    @EntityGraph(attributePaths = {"examQuestion", "examQuestion.question", "examQuestion.question.options"})
    List<StudentAnswer> findByAttempt_Id(Long attemptId);
}
