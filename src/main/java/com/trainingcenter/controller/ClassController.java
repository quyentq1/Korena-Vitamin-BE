package com.trainingcenter.controller;

import com.trainingcenter.entity.ClassEntity;
import com.trainingcenter.entity.User;
import com.trainingcenter.service.ClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/classes")
@PreAuthorize("hasAnyRole('STAFF', 'ADMIN', 'TEACHER')")
public class ClassController {

    @Autowired
    private ClassService classService;
    
    @Autowired
    private com.trainingcenter.service.UserService userService;

    @PostMapping
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    public ResponseEntity<ClassEntity> createClass(@RequestBody ClassEntity classEntity) {
        return ResponseEntity.ok(classService.createClass(classEntity));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    public ResponseEntity<ClassEntity> updateClass(@PathVariable Long id, @RequestBody ClassEntity classDetails) {
        return ResponseEntity.ok(classService.updateClass(id, classDetails));
    }

    @GetMapping
    public ResponseEntity<List<ClassEntity>> getAllClasses() {
        return ResponseEntity.ok(classService.getAllClasses());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClassEntity> getClassById(@PathVariable Long id) {
        return ResponseEntity.ok(classService.getClassById(id));
    }

    @PostMapping("/{id}/students")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    public ResponseEntity<?> addStudentToClass(@PathVariable Long id, @RequestBody Long studentId) {
        classService.addStudentToClass(id, studentId);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/{id}/students")
    public ResponseEntity<List<User>> getClassStudents(@PathVariable Long id) {
        return ResponseEntity.ok(classService.getStudentsByClass(id));
    }

    @PostMapping("/{id}/teachers")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    public ResponseEntity<?> assignTeacherToClass(
            @PathVariable Long id, 
            @RequestBody Long teacherId,
            @RequestParam(defaultValue = "false") Boolean isPrimary) {
        classService.assignTeacher(id, teacherId, isPrimary);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/teachers")
    public ResponseEntity<List<User>> getClassTeachers(@PathVariable Long id) {
        return ResponseEntity.ok(classService.getTeachersByClass(id));
    }

    // --- Schedules ---

    @GetMapping("/{id}/schedules")
    public ResponseEntity<List<ClassSchedule>> getClassSchedules(@PathVariable Long id) {
        return ResponseEntity.ok(classService.getClassSchedules(id));
    }

    @PostMapping("/{id}/schedules")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN', 'TEACHER')")
    public ResponseEntity<ClassSchedule> createSchedule(@PathVariable Long id, @RequestBody ClassSchedule schedule) {
        return ResponseEntity.ok(classService.createSchedule(id, schedule));
    }

    // --- Attendance ---

    @GetMapping("/schedules/{scheduleId}/attendance")
    public ResponseEntity<List<Attendance>> getAttendance(@PathVariable Long scheduleId) {
        return ResponseEntity.ok(classService.getAttendance(scheduleId));
    }

    @PostMapping("/schedules/{scheduleId}/attendance")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN', 'TEACHER')")
    public ResponseEntity<?> markAttendance(@PathVariable Long scheduleId, @RequestBody List<Attendance> attendances) {
        classService.markAttendance(scheduleId, attendances);
        return ResponseEntity.ok().build();
    }

    // --- Lesson Quizzes ---

    @GetMapping("/{id}/quizzes")
    public ResponseEntity<List<LessonQuiz>> getLessonQuizzes(@PathVariable Long id) {
        return ResponseEntity.ok(classService.getLessonQuizzes(id));
    }

    @PostMapping("/{id}/quizzes")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<LessonQuiz> createLessonQuiz(
            @PathVariable Long id, 
            @RequestBody LessonQuiz quiz,
            org.springframework.security.core.Authentication authentication) {
        
        User teacher = userService.getUserByUsername(authentication.getName());
        return ResponseEntity.ok(classService.createLessonQuiz(id, quiz, teacher.getId()));
    }
}
