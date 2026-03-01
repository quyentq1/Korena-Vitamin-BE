package com.trainingcenter.controller;

import com.trainingcenter.entity.FAQ;
import com.trainingcenter.service.FAQService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/faq")
public class FAQController {

    @Autowired
    private FAQService faqService;

    /**
     * Public endpoint for guests to get all FAQs
     * GET /api/faq?lang=vi
     */
    @GetMapping
    public ResponseEntity<List<FAQ>> getFAQs(
            @RequestParam(defaultValue = "vi") String lang) {
        List<FAQ> faqs = faqService.getFAQsByLanguage(lang);
        return ResponseEntity.ok(faqs);
    }

    /**
     * Public endpoint to get FAQ categories
     * GET /api/faq/categories?lang=vi
     */
    @GetMapping("/categories")
    public ResponseEntity<List<String>> getCategories(
            @RequestParam(defaultValue = "vi") String lang) {
        List<String> categories = faqService.getCategories(lang);
        return ResponseEntity.ok(categories);
    }

    /**
     * Public endpoint to get FAQs by category
     * GET /api/faq/category/{category}?lang=vi
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<List<FAQ>> getFAQsByCategory(
            @PathVariable String category,
            @RequestParam(defaultValue = "vi") String lang) {
        List<FAQ> faqs = faqService.getFAQsByCategory(category, lang);
        return ResponseEntity.ok(faqs);
    }

    /**
     * Public endpoint to search FAQs
     * GET /api/faq/search?keyword=...&lang=vi
     */
    @GetMapping("/search")
    public ResponseEntity<List<FAQ>> searchFAQs(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "vi") String lang) {
        List<FAQ> faqs = faqService.searchFAQs(lang, keyword);
        return ResponseEntity.ok(faqs);
    }

    /**
     * Public endpoint to get a specific FAQ
     * GET /api/faq/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<FAQ> getFAQ(@PathVariable Long id) {
        FAQ faq = faqService.getFAQById(id);
        return ResponseEntity.ok(faq);
    }

    /**
     * Create a new FAQ (admin only)
     * POST /api/faq
     */
    @PostMapping
    public ResponseEntity<FAQ> createFAQ(@RequestBody FAQ faq) {
        FAQ createdFAQ = faqService.createFAQ(faq);
        return ResponseEntity.ok(createdFAQ);
    }

    /**
     * Update an FAQ (admin only)
     * PUT /api/faq/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<FAQ> updateFAQ(@PathVariable Long id, @RequestBody FAQ faq) {
        FAQ updatedFAQ = faqService.updateFAQ(id, faq);
        return ResponseEntity.ok(updatedFAQ);
    }

    /**
     * Delete an FAQ (admin only)
     * DELETE /api/faq/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFAQ(@PathVariable Long id) {
        faqService.deleteFAQ(id);
        return ResponseEntity.noContent().build();
    }
}
