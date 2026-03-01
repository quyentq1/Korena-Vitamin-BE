package com.trainingcenter.service;

import com.trainingcenter.entity.Lesson;
import com.trainingcenter.exception.ResourceNotFoundException;
import com.trainingcenter.repository.LessonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class LessonService {

    @Autowired
    private LessonRepository lessonRepository;

    /**
     * Get all preview lessons for a course (for guest users)
     */
    @Transactional(readOnly = true)
    public List<Lesson> getPreviewLessonsByCourseId(Long courseId) {
        return lessonRepository.findByCourseIdAndIsPreviewTrueAndActiveTrueOrderByLessonOrder(courseId);
    }

    /**
     * Get all lessons for a course (for authenticated users)
     */
    @Transactional(readOnly = true)
    public List<Lesson> getLessonsByCourseId(Long courseId) {
        return lessonRepository.findByCourseIdAndActiveTrueOrderByLessonOrder(courseId);
    }

    /**
     * Get a specific lesson by ID
     */
    @Transactional(readOnly = true)
    public Lesson getLessonById(Long id) {
        return lessonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found with id: " + id));
    }

    /**
     * Create a new lesson
     */
    public Lesson createLesson(Lesson lesson) {
        return lessonRepository.save(lesson);
    }

    /**
     * Update an existing lesson
     */
    public Lesson updateLesson(Long id, Lesson lessonDetails) {
        Lesson lesson = getLessonById(id);
        lesson.setTitle(lessonDetails.getTitle());
        lesson.setDescription(lessonDetails.getDescription());
        lesson.setVideoUrl(lessonDetails.getVideoUrl());
        lesson.setContent(lessonDetails.getContent());
        lesson.setLessonOrder(lessonDetails.getLessonOrder());
        lesson.setIsPreview(lessonDetails.getIsPreview());
        lesson.setDurationMinutes(lessonDetails.getDurationMinutes());
        lesson.setThumbnailUrl(lessonDetails.getThumbnailUrl());
        lesson.setActive(lessonDetails.getActive());
        return lessonRepository.save(lesson);
    }

    /**
     * Delete a lesson
     */
    public void deleteLesson(Long id) {
        Lesson lesson = getLessonById(id);
        lessonRepository.delete(lesson);
    }

    /**
     * Count preview lessons for a course
     */
    @Transactional(readOnly = true)
    public Long countPreviewLessons(Long courseId) {
        return lessonRepository.countByCourseIdAndIsPreviewTrueAndActiveTrue(courseId);
    }
}
