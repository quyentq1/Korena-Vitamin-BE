package com.trainingcenter.repository;

import com.trainingcenter.entity.FAQ;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FAQRepository extends JpaRepository<FAQ, Long> {

    // Find all active FAQs by language
    List<FAQ> findByActiveTrueAndLanguageOrderByOrderIndexAscCategoryAsc(String language);

    // Find active FAQs by category and language
    List<FAQ> findByActiveTrueAndCategoryAndLanguageOrderByOrderIndexAsc(String category, String language);

    // Get all distinct categories
    @Query("SELECT DISTINCT f.category FROM FAQ f WHERE f.active = true AND f.language = :language ORDER BY f.category")
    List<String> findDistinctCategoriesByLanguage(@Param("language") String language);

    // Search FAQs by question or answer
    @Query("SELECT f FROM FAQ f WHERE f.active = true AND f.language = :language " +
           "AND (LOWER(f.question) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(f.answer) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(f.keywords) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "ORDER BY f.orderIndex ASC")
    List<FAQ> searchFAQs(@Param("language") String language, @Param("keyword") String keyword);

    // Increment view count
    @Query("UPDATE FAQ f SET f.viewCount = f.viewCount + 1 WHERE f.id = :id")
    void incrementViewCount(@Param("id") Long id);
}
