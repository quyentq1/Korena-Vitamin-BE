package com.trainingcenter.repository;

import com.trainingcenter.entity.Question;
import com.trainingcenter.entity.QuestionCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByCategory(QuestionCategory category);
    List<Question> findByCategoryAndActive(QuestionCategory category, Boolean active);
    List<Question> findByQuestionType(Question.QuestionType questionType);
    List<Question> findByDifficulty(Question.Difficulty difficulty);
    List<Question> findByActive(Boolean active);
    List<Question> findByPatternIdAndActive(Long patternId, Boolean active);
    
    @Query("SELECT q FROM Question q WHERE q.category.id = :categoryId AND q.active = true")
    List<Question> findActiveByCategoryId(@Param("categoryId") Long categoryId);
    
    @Query("SELECT q FROM Question q WHERE q.category.id = :categoryId AND q.difficulty = :difficulty AND q.active = true")
    List<Question> findByCategoryIdAndDifficulty(@Param("categoryId") Long categoryId, @Param("difficulty") Question.Difficulty difficulty);
}
