package com.trainingcenter.service;

import com.trainingcenter.entity.*;
import com.trainingcenter.exception.BadRequestException;
import com.trainingcenter.exception.ResourceNotFoundException;
import com.trainingcenter.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class ExamService {

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private ExamQuestionRepository examQuestionRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Generate exam based on criteria (category-based question selection)
     * criteriaMap example: {categoryId: numberOfQuestions}
     */
    public Exam generateExam(Long courseId, Long createdBy, String title, Integer durationMinutes,
                              Map<Long, Integer> criteriaMap) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        User teacher = userRepository.findById(createdBy)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found"));

        List<ExamQuestion> examQuestions = new ArrayList<>();
        int order = 1;
        int totalPoints = 0;

        // For each category, select random questions
        for (Map.Entry<Long, Integer> entry : criteriaMap.entrySet()) {
            Long categoryId = entry.getKey();
            Integer count = entry.getValue();

            List<Question> questions = questionRepository.findActiveByCategoryId(categoryId);

            if (questions.size() < count) {
                throw new BadRequestException("Not enough questions in category. Available: " +
                        questions.size() + ", Requested: " + count);
            }

            // Randomly select questions
            Collections.shuffle(questions);
            List<Question> selectedQuestions = questions.subList(0, count);

            for (Question question : selectedQuestions) {
                ExamQuestion examQuestion = new ExamQuestion();
                examQuestion.setQuestion(question);
                examQuestion.setQuestionOrder(order++);
                examQuestion.setPoints(question.getPoints());
                totalPoints += question.getPoints();
                examQuestions.add(examQuestion);
            }
        }

        // Create exam
        Exam exam = new Exam();
        exam.setCourse(course);
        exam.setCreatedBy(teacher);
        exam.setTitle(title);
        exam.setDurationMinutes(durationMinutes);
        exam.setTotalPoints(totalPoints);
        exam.setPublished(false);

        Exam savedExam = examRepository.save(exam);

        // Save exam questions
        for (ExamQuestion eq : examQuestions) {
            eq.setExam(savedExam);
            examQuestionRepository.save(eq);
        }

        savedExam.setExamQuestions(examQuestions);
        return savedExam;
    }

    public Exam createExam(Exam exam, List<ExamQuestion> examQuestions) {
        // Validate course
        Course course = courseRepository.findById(exam.getCourse().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        exam.setCourse(course);

        // Validate teacher
        User teacher = userRepository.findById(exam.getCreatedBy().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found"));
        exam.setCreatedBy(teacher);

        exam.setPublished(false);
        Exam savedExam = examRepository.save(exam);

        // Save exam questions
        int order = 1;
        for (ExamQuestion eq : examQuestions) {
            eq.setExam(savedExam);
            eq.setQuestionOrder(order++);
            examQuestionRepository.save(eq);
        }

        return savedExam;
    }

    public Exam updateExam(Long id, Exam examDetails) {
        Exam exam = examRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found with id: " + id));

        exam.setTitle(examDetails.getTitle());
        exam.setDescription(examDetails.getDescription());
        exam.setDurationMinutes(examDetails.getDurationMinutes());
        exam.setAvailableFrom(examDetails.getAvailableFrom());
        exam.setAvailableTo(examDetails.getAvailableTo());

        return examRepository.save(exam);
    }

    public void deleteExam(Long id) {
        Exam exam = examRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found with id: " + id));
        examRepository.delete(exam);
    }

    public Exam publishExam(Long id, boolean published) {
        Exam exam = examRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found with id: " + id));
        exam.setPublished(published);
        return examRepository.save(exam);
    }

    public Exam getExamById(Long id) {
        return examRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found with id: " + id));
    }

    public List<Exam> getExamsByCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        return examRepository.findByCourse(course);
    }

    public List<Exam> getPublishedExams() {
        return examRepository.findByPublished(true);
    }

    public List<Exam> getGuestExams() {
        return examRepository.findByPublished(true).stream()
                .limit(2)
                .collect(java.util.stream.Collectors.toList());
    }

    public List<ExamQuestion> getExamQuestions(Long examId) {
        return examQuestionRepository.findByExamIdOrderByQuestionOrder(examId);
    }
}
