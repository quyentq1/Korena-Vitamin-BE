package com.trainingcenter.controller;

import com.trainingcenter.entity.Course;
import com.trainingcenter.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/public/courses")
public class PublicCourseController {

    @Autowired
    private CourseService courseService;

    @GetMapping
    public ResponseEntity<List<Course>> getActiveCourses() {
        return ResponseEntity.ok(courseService.getActiveCourses());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Course> getCourseDetail(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.getCourseById(id));
    }
}
