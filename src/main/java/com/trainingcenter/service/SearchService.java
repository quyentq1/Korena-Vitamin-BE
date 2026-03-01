package com.trainingcenter.service;

import com.trainingcenter.dto.SearchResultDTO;
import com.trainingcenter.repository.CourseRepository;
import com.trainingcenter.repository.ExamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class SearchService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ExamRepository examRepository;

    /**
     * Search across courses and exams
     */
    @Transactional(readOnly = true)
    public List<SearchResultDTO> searchAll(String keyword) {
        List<SearchResultDTO> results = new ArrayList<>();

        // Search courses
        List<Object[]> courses = courseRepository.searchCourses(keyword);
        for (Object[] course : courses) {
            results.add(SearchResultDTO.fromCourse(course));
        }

        // Search exams
        List<Object[]> exams = examRepository.searchExams(keyword);
        for (Object[] exam : exams) {
            results.add(SearchResultDTO.fromExam(exam));
        }

        return results;
    }

    /**
     * Search only courses
     */
    @Transactional(readOnly = true)
    public List<SearchResultDTO> searchCourses(String keyword) {
        List<SearchResultDTO> results = new ArrayList<>();
        List<Object[]> courses = courseRepository.searchCourses(keyword);

        for (Object[] course : courses) {
            results.add(SearchResultDTO.fromCourse(course));
        }

        return results;
    }

    /**
     * Search only exams
     */
    @Transactional(readOnly = true)
    public List<SearchResultDTO> searchExams(String keyword) {
        List<SearchResultDTO> results = new ArrayList<>();
        List<Object[]> exams = examRepository.searchExams(keyword);

        for (Object[] exam : exams) {
            results.add(SearchResultDTO.fromExam(exam));
        }

        return results;
    }
}
