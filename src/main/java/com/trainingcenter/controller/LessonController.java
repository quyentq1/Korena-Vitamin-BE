package com.trainingcenter.controller;

import com.trainingcenter.entity.Lesson;
import com.trainingcenter.service.LessonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/lessons")
public class LessonController {

    @Autowired
    private LessonService lessonService;

    /**
     * Public endpoint for guest users to view preview lessons of a course
     * GET /api/lessons/course/{courseId}/preview
     * Note: This is public because SecurityConfig permits /lessons/** with GET
     */
    @GetMapping("/course/{courseId}/preview")
    public ResponseEntity<List<Lesson>> getPreviewLessons(@PathVariable Long courseId) {
        List<Lesson> lessons = lessonService.getPreviewLessonsByCourseId(courseId);
        return ResponseEntity.ok(lessons);
    }

    /**
     * Get all lessons for a course (requires authentication)
     * GET /api/lessons/course/{courseId}
     */
    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<Lesson>> getCourseLessons(@PathVariable Long courseId) {
        List<Lesson> lessons = lessonService.getLessonsByCourseId(courseId);
        return ResponseEntity.ok(lessons);
    }

    /**
     * Get a specific lesson by ID (requires authentication)
     * GET /api/lessons/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Lesson> getLesson(@PathVariable Long id) {
        Lesson lesson = lessonService.getLessonById(id);
        return ResponseEntity.ok(lesson);
    }

    /**
     * Create a new lesson (admin only)
     * POST /api/lessons
     */
    @PostMapping
    public ResponseEntity<Lesson> createLesson(@RequestBody Lesson lesson) {
        Lesson createdLesson = lessonService.createLesson(lesson);
        return ResponseEntity.ok(createdLesson);
    }

    /**
     * Update a lesson (admin only)
     * PUT /api/lessons/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<Lesson> updateLesson(@PathVariable Long id, @RequestBody Lesson lesson) {
        Lesson updatedLesson = lessonService.updateLesson(id, lesson);
        return ResponseEntity.ok(updatedLesson);
    }

    /**
     * Delete a lesson (admin only)
     * DELETE /api/lessons/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLesson(@PathVariable Long id) {
        lessonService.deleteLesson(id);
        return ResponseEntity.noContent().build();
    }
}
