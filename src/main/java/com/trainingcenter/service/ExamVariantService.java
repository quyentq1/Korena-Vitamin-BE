package com.trainingcenter.service;

import com.trainingcenter.entity.*;
import com.trainingcenter.exception.BadRequestException;
import com.trainingcenter.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Exam Variant Service
 * CRITICAL: Generates 20 unique exam variants with pattern-based question distribution
 */
@Service
@RequiredArgsConstructor
public class ExamVariantService {

    private final ExamRepository examRepository;
    private final ExamVariantRepository examVariantRepository;
    private final ExamPatternDistributionRepository patternDistributionRepository;
    private final QuestionRepository questionRepository;
    private final VariantQuestionRepository variantQuestionRepository;
    private final QuestionUsageHistoryRepository usageHistoryRepository;

    private static final String[] VARIANT_CODES = {
        "A", "B", "C", "D", "E", "F", "G", "H", "I", "J",
        "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T"
    };

    /**
     * Generate 20 exam variants
     * Business Rule: Each variant has different questions but same pattern distribution
     */
    @Transactional
    public List<ExamVariant> generateVariants(Long examId) {
        Exam exam = examRepository.findById(examId)
            .orElseThrow(() -> new BadRequestException("Exam not found"));

        // Get pattern distribution for this exam
        List<ExamPatternDistribution> distributions = patternDistributionRepository.findByExamId(examId);
        if (distributions.isEmpty()) {
            throw new BadRequestException("No pattern distribution configured for this exam");
        }

        // Delete existing variants
        List<ExamVariant> existingVariants = examVariantRepository.findByExamId(examId);
        existingVariants.forEach(variant -> variantQuestionRepository.deleteByVariantId(variant.getId()));
        examVariantRepository.deleteAll(existingVariants);

        List<ExamVariant> variants = new ArrayList<>();

        // Create 20 variants
        for (String code : VARIANT_CODES) {
            ExamVariant variant = new ExamVariant();
            variant.setExam(exam);
            variant.setVariantCode(code);
            variant.setVariantName("Đề thi mã " + code);
            variant.setIsActive(true);
            
            ExamVariant savedVariant = examVariantRepository.save(variant);
            
            // Assign questions to this variant
            assignQuestionsToVariant(savedVariant, distributions);
            
            variants.add(savedVariant);
        }

        return variants;
    }

    /**
     * Assign questions to a variant based on pattern distribution
     * CRITICAL: Ensures no duplicate questions across variants
     */
    private void assignQuestionsToVariant(ExamVariant variant, List<ExamPatternDistribution> distributions) {
        Set<Long> usedQuestionIds = new HashSet<>();
        int order = 1;

        for (ExamPatternDistribution dist : distributions) {
            Long patternId = dist.getPattern().getId();
            int requiredCount = dist.getQuestionCount();

            // Get available questions for this pattern
            List<Question> availableQuestions = questionRepository.findByPatternIdAndActive(patternId, true);
            
            // Filter out already used questions
            List<Question> unusedQuestions = availableQuestions.stream()
                .filter(q -> !usedQuestionIds.contains(q.getId()))
                .collect(Collectors.toList());

            if (unusedQuestions.size() < requiredCount) {
                throw new BadRequestException(
                    "Không đủ câu hỏi cho pattern " + dist.getPattern().getPatternCode() + 
                    ". Cần " + requiredCount + ", chỉ có " + unusedQuestions.size()
                );
            }

            // Randomly select questions
            Collections.shuffle(unusedQuestions);
            List<Question> selectedQuestions = unusedQuestions.subList(0, requiredCount);

            // Create VariantQuestion entries
            for (Question question : selectedQuestions) {
                VariantQuestion vq = new VariantQuestion();
                vq.setVariant(variant);
                vq.setQuestion(question);
                vq.setQuestionOrder(order++);
                vq.setPoints(dist.getPointsPerQuestion());
                
                variantQuestionRepository.save(vq);
                usedQuestionIds.add(question.getId());
            }
        }
    }

    /**
     * Get random variant for student
     * Business Rule: Type 1 (FREE_PAID) must exclude questions student has seen before
     */
    public ExamVariant getVariantForStudent(Long examId, Long userId) {
        Exam exam = examRepository.findById(examId)
            .orElseThrow(() -> new BadRequestException("Exam not found"));

        List<ExamVariant> activeVariants = examVariantRepository.findByExamIdAndIsActive(examId, true);
        if (activeVariants.isEmpty()) {
            throw new BadRequestException("No active variants available");
        }

        // For Type 1 exams, check question usage history
        if (exam.getExamType() == Exam.ExamType.FREE_PAID) {
            List<Long> usedQuestionIds = usageHistoryRepository.findUsedQuestionIdsByUserId(userId);
            
            // Find variant with minimum overlap
            ExamVariant bestVariant = findVariantWithMinimumUsedQuestions(activeVariants, usedQuestionIds);
            return bestVariant;
        }

        // For other types, return random variant
        Collections.shuffle(activeVariants);
        return activeVariants.get(0);
    }

    private ExamVariant findVariantWithMinimumUsedQuestions(List<ExamVariant> variants, List<Long> usedQuestionIds) {
        ExamVariant bestVariant = null;
        int minUsedCount = Integer.MAX_VALUE;

        for (ExamVariant variant : variants) {
            List<VariantQuestion> vqs = variantQuestionRepository.findByVariantId(variant.getId());
            long usedCount = vqs.stream()
                .filter(vq -> usedQuestionIds.contains(vq.getQuestion().getId()))
                .count();

            if (usedCount < minUsedCount) {
                minUsedCount = (int) usedCount;
                bestVariant = variant;
            }
        }

        return bestVariant;
    }

    /**
     * Get all variants for an exam
     */
    public List<ExamVariant> getExamVariants(Long examId) {
        return examVariantRepository.findByExamId(examId);
    }
}
