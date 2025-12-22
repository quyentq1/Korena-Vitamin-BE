package com.trainingcenter.repository;

import com.trainingcenter.entity.QuestionPattern;
import com.trainingcenter.entity.ExamSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionPatternRepository extends JpaRepository<QuestionPattern, Long> {
    List<QuestionPattern> findBySkill(ExamSkill skill);
    Optional<QuestionPattern> findByPatternCode(String patternCode);
    List<QuestionPattern> findBySkillId(Long skillId);
}
