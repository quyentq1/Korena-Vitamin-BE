package com.trainingcenter.service;

import com.trainingcenter.dto.ExamGenerationRequest;
import com.trainingcenter.dto.ExamStructureDto;
import com.trainingcenter.entity.*;
import com.trainingcenter.exception.BadRequestException;
import com.trainingcenter.exception.ResourceNotFoundException;
import com.trainingcenter.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExamGenerationService {

    private final ExamRepository examRepository;
    private final ExamQuestionRepository examQuestionRepository;
    private final QuestionRepository questionRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    @Transactional
    public Exam generateExam(ExamGenerationRequest request, Long teacherId) {
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found"));

        List<ExamQuestion> examQuestions = new ArrayList<>();
        int order = 1;
        int totalPoints = 0;

        for (ExamStructureDto criteria : request.getStructure()) {
            List<Question> availableQuestions = fetchQuestions(criteria);

            if (availableQuestions.size() < criteria.getCount()) {
                throw new BadRequestException("Not enough questions matching criteria: " +
                        "Category=" + criteria.getCategoryId() +
                        ", Type=" + criteria.getQuestionType() +
                        ", Difficulty=" + criteria.getDifficulty() +
                        ". Available: " + availableQuestions.size() +
                        ", Requested: " + criteria.getCount());
            }

            // Shuffle and select
            Collections.shuffle(availableQuestions);
            List<Question> selectedQuestions = availableQuestions.subList(0, criteria.getCount());

            for (Question question : selectedQuestions) {
                ExamQuestion examQuestion = new ExamQuestion();
                examQuestion.setQuestion(question);
                examQuestion.setQuestionOrder(order++);
                
                // Use override points if specified, else default from question
                int points = (criteria.getPointsPerQuestion() != null) 
                        ? criteria.getPointsPerQuestion() 
                        : question.getPoints();
                
                examQuestion.setPoints(points);
                totalPoints += points;
                examQuestions.add(examQuestion);
            }
        }

        // Create Exam Entity
        Exam exam = new Exam();
        exam.setCourse(course);
        exam.setCreatedBy(teacher);
        exam.setTitle(request.getTitle());
        exam.setDescription(request.getDescription());
        exam.setDurationMinutes(request.getDurationMinutes());
        exam.setTotalPoints(totalPoints);
        exam.setPublished(false);
        exam.setExamType(Exam.ExamType.UNLIMITED); // Default for generated exams

        Exam savedExam = examRepository.save(exam);

        // Save ExamQuestions
        for (ExamQuestion eq : examQuestions) {
            eq.setExam(savedExam);
            examQuestionRepository.save(eq);
        }
        
        savedExam.setExamQuestions(examQuestions);
        return savedExam;
    }

    private List<Question> fetchQuestions(ExamStructureDto criteria) {
        // Build query logic
        // Ideally use Specification or Criteria API for flexible filtering.
        // For simplicity, we can fetch by lowest common denominator and filter in memory if volume is low,
        // OR use specific Repository methods. 
        
        // Let's rely on Repository methods. We need to handle nulls in criteria.
        
        // Strategy: 
        // 1. Fetch ALL active questions (or by Category if present, as it's a strong filter).
        // 2. Filter stream by other non-null criteria.
        
        List<Question> candidates;
        if (criteria.getCategoryId() != null) {
            candidates = questionRepository.findActiveByCategoryId(criteria.getCategoryId());
        } else {
            candidates = questionRepository.findByActive(true);
        }
        
        return candidates.stream()
                .filter(q -> criteria.getQuestionType() == null || q.getQuestionType() == criteria.getQuestionType())
                .filter(q -> criteria.getDifficulty() == null || q.getDifficulty() == criteria.getDifficulty())
                .collect(Collectors.toList());
    }
}
