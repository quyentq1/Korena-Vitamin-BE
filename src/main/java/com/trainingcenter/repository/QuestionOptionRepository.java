package com.trainingcenter.repository;

import com.trainingcenter.entity.QuestionOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionOptionRepository extends JpaRepository<QuestionOption, Long> {
    List<QuestionOption> findByQuestionIdOrderByOptionOrder(Long questionId);
    void deleteByQuestionId(Long questionId);
}
