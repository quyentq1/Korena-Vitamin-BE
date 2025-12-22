package com.trainingcenter.service;

import com.trainingcenter.entity.Question;
import com.trainingcenter.entity.QuestionCategory;
import com.trainingcenter.entity.QuestionOption;
import com.trainingcenter.exception.BadRequestException;
import com.trainingcenter.exception.ResourceNotFoundException;
import com.trainingcenter.repository.QuestionCategoryRepository;
import com.trainingcenter.repository.QuestionOptionRepository;
import com.trainingcenter.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class QuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuestionCategoryRepository categoryRepository;

    @Autowired
    private QuestionOptionRepository optionRepository;

    public Question createQuestion(Question question, List<QuestionOption> options) {
        // Validate category exists
        QuestionCategory category = categoryRepository.findById(question.getCategory().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        question.setCategory(category);
        question.setActive(true);

        Question savedQuestion = questionRepository.save(question);

        // Save options for multiple choice questions
        if (question.getQuestionType() == Question.QuestionType.MULTIPLE_CHOICE && options != null) {
            for (QuestionOption option : options) {
                option.setQuestion(savedQuestion);
                optionRepository.save(option);
            }
            savedQuestion.setOptions(options);
        }

        return savedQuestion;
    }

    public Question updateQuestion(Long id, Question questionDetails, List<QuestionOption> options) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + id));

        // Update fields
        question.setQuestionText(questionDetails.getQuestionText());
        question.setQuestionMediaUrl(questionDetails.getQuestionMediaUrl());
        question.setCorrectAnswer(questionDetails.getCorrectAnswer());
        question.setPoints(questionDetails.getPoints());
        question.setDifficulty(questionDetails.getDifficulty());
        question.setActive(questionDetails.getActive());

        // Update category if changed
        if (!question.getCategory().getId().equals(questionDetails.getCategory().getId())) {
            QuestionCategory category = categoryRepository.findById(questionDetails.getCategory().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
            question.setCategory(category);
        }

        Question updated = questionRepository.save(question);

        // Update options if multiple choice
        if (question.getQuestionType() == Question.QuestionType.MULTIPLE_CHOICE && options != null) {
            // Delete old options
            optionRepository.deleteByQuestionId(id);

            // Add new options
            for (QuestionOption option : options) {
                option.setQuestion(updated);
                optionRepository.save(option);
            }
        }

        return updated;
    }

    public void deleteQuestion(Long id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + id));
        questionRepository.delete(question);
    }

    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }

    public Question getQuestionById(Long id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + id));
    }

    public List<Question> getQuestionsByCategory(Long categoryId) {
        return questionRepository.findActiveByCategoryId(categoryId);
    }

    public List<Question> getQuestionsByCategoryAndDifficulty(Long categoryId, Question.Difficulty difficulty) {
        return questionRepository.findByCategoryIdAndDifficulty(categoryId, difficulty);
    }

    public QuestionCategory createCategory(QuestionCategory category) {
        if (categoryRepository.existsByName(category.getName())) {
            throw new BadRequestException("Category name already exists");
        }
        return categoryRepository.save(category);
    }

    public List<QuestionCategory> getAllCategories() {
        return categoryRepository.findAll();
    }

    public QuestionCategory getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
    }

    public List<QuestionOption> getOptionsByQuestionId(Long questionId) {
        return optionRepository.findByQuestionIdOrderByOptionOrder(questionId);
    }
}
