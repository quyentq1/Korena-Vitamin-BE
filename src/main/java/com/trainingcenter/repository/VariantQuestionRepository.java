package com.trainingcenter.repository;

import com.trainingcenter.entity.VariantQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VariantQuestionRepository extends JpaRepository<VariantQuestion, Long> {
    List<VariantQuestion> findByVariantId(Long variantId);
    List<VariantQuestion> findByVariantIdOrderByQuestionOrderAsc(Long variantId);
    void deleteByVariantId(Long variantId);
}
