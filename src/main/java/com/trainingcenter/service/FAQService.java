package com.trainingcenter.service;

import com.trainingcenter.entity.FAQ;
import com.trainingcenter.exception.ResourceNotFoundException;
import com.trainingcenter.repository.FAQRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class FAQService {

    @Autowired
    private FAQRepository faqRepository;

    /**
     * Get all active FAQs by language
     */
    @Transactional(readOnly = true)
    public List<FAQ> getFAQsByLanguage(String language) {
        return faqRepository.findByActiveTrueAndLanguageOrderByOrderIndexAscCategoryAsc(language);
    }

    /**
     * Get all active FAQs by category and language
     */
    @Transactional(readOnly = true)
    public List<FAQ> getFAQsByCategory(String category, String language) {
        return faqRepository.findByActiveTrueAndCategoryAndLanguageOrderByOrderIndexAsc(category, language);
    }

    /**
     * Get all distinct categories
     */
    @Transactional(readOnly = true)
    public List<String> getCategories(String language) {
        return faqRepository.findDistinctCategoriesByLanguage(language);
    }

    /**
     * Search FAQs by keyword
     */
    @Transactional(readOnly = true)
    public List<FAQ> searchFAQs(String language, String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getFAQsByLanguage(language);
        }
        return faqRepository.searchFAQs(language, keyword);
    }

    /**
     * Get a specific FAQ by ID
     */
    @Transactional(readOnly = true)
    public FAQ getFAQById(Long id) {
        FAQ faq = faqRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FAQ not found with id: " + id));

        // Increment view count
        faq.setViewCount((faq.getViewCount() != null ? faq.getViewCount() : 0) + 1);
        faqRepository.save(faq);

        return faq;
    }

    /**
     * Create a new FAQ
     */
    public FAQ createFAQ(FAQ faq) {
        return faqRepository.save(faq);
    }

    /**
     * Update an existing FAQ
     */
    public FAQ updateFAQ(Long id, FAQ faqDetails) {
        FAQ faq = getFAQById(id);
        faq.setQuestion(faqDetails.getQuestion());
        faq.setAnswer(faqDetails.getAnswer());
        faq.setCategory(faqDetails.getCategory());
        faq.setOrderIndex(faqDetails.getOrderIndex());
        faq.setActive(faqDetails.getActive());
        faq.setLanguage(faqDetails.getLanguage());
        faq.setKeywords(faqDetails.getKeywords());
        return faqRepository.save(faq);
    }

    /**
     * Delete a FAQ
     */
    public void deleteFAQ(Long id) {
        FAQ faq = getFAQById(id);
        faqRepository.delete(faq);
    }
}
