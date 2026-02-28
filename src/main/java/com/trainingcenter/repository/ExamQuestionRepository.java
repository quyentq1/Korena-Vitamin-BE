package com.trainingcenter.repository;

import com.trainingcenter.entity.ExamQuestion;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamQuestionRepository extends JpaRepository<ExamQuestion, Long> {
    @EntityGraph(attributePaths = { "question", "question.options" })
    List<ExamQuestion> findByExamIdOrderByQuestionOrder(Long examId);

    void deleteByExamId(Long examId);
}
