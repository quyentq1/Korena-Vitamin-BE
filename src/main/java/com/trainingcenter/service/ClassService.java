package com.trainingcenter.service;

import com.trainingcenter.entity.*;
import com.trainingcenter.exception.BadRequestException;
import com.trainingcenter.exception.ResourceNotFoundException;
import com.trainingcenter.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Class Service
 * Manages class creation, enrollment, and scheduling
 */
@Service
@RequiredArgsConstructor
public class ClassService {

    private final ClassRepository classRepository;
    private final ClassStudentRepository classStudentRepository;
    private final ClassTeacherRepository classTeacherRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final ClassScheduleRepository classScheduleRepository;
    private final AttendanceRepository attendanceRepository;
    private final LessonQuizRepository lessonQuizRepository;

    /**
     * Create new class
     */
    @Transactional
    public ClassEntity createClass(Long courseId, String classCode, String className,
            Integer capacity, LocalDate startDate, LocalDate endDate) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new BadRequestException("Course not found"));

        // Check if class code already exists
        if (classRepository.findByClassCode(classCode).isPresent()) {
            throw new BadRequestException("Class code already exists");
        }

        ClassEntity classEntity = new ClassEntity();
        classEntity.setCourse(course);
        classEntity.setClassCode(classCode);
        classEntity.setClassName(className);
        classEntity.setCapacity(capacity);
        classEntity.setStartDate(startDate);
        classEntity.setEndDate(endDate);
        classEntity.setStatus(ClassEntity.ClassStatus.PLANNED);
        classEntity.setCurrentEnrollment(0);

        return classRepository.save(classEntity);
    }

    /**
     * Enroll student in class
     * Business Rule: Check capacity before enrollment
     */
    @Transactional
    public ClassStudent enrollStudent(Long classId, Long studentId) {
        ClassEntity classEntity = classRepository.findById(classId)
                .orElseThrow(() -> new BadRequestException("Class not found"));

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new BadRequestException("Student not found"));

        // Validate student role
        if (student.getRole() != User.UserRole.STUDENT &&
                student.getRole() != User.UserRole.LEARNER) {
            throw new BadRequestException("User must be STUDENT or LEARNER role");
        }

        // Check capacity
        if (classEntity.getCurrentEnrollment() >= classEntity.getCapacity()) {
            throw new BadRequestException("Class is full");
        }

        // Check if already enrolled
        if (classStudentRepository.findByClassEntityIdAndStudentId(classId, studentId).isPresent()) {
            throw new BadRequestException("Student already enrolled in this class");
        }

        ClassStudent enrollment = new ClassStudent();
        enrollment.setClassEntity(classEntity);
        enrollment.setStudent(student);
        enrollment.setEnrollmentDate(LocalDate.now());
        enrollment.setStatus(ClassStudent.EnrollmentStatus.ACTIVE);

        ClassStudent saved = classStudentRepository.save(enrollment);

        // Update class enrollment count
        classEntity.setCurrentEnrollment(classEntity.getCurrentEnrollment() + 1);
        classRepository.save(classEntity);

        return saved;
    }

    /**
     * Assign teacher to class
     */
    @Transactional
    public ClassTeacher assignTeacher(Long classId, Long teacherId, Boolean isPrimary) {
        ClassEntity classEntity = classRepository.findById(classId)
                .orElseThrow(() -> new BadRequestException("Class not found"));

        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new BadRequestException("Teacher not found"));

        if (teacher.getRole() != User.UserRole.TEACHER) {
            throw new BadRequestException("User must have TEACHER role");
        }

        ClassTeacher assignment = new ClassTeacher();
        assignment.setClassEntity(classEntity);
        assignment.setTeacher(teacher);
        assignment.setIsPrimary(isPrimary);
        assignment.setAssignedDate(LocalDate.now());

        return classTeacherRepository.save(assignment);
    }

    /**
     * Get all students in a class
     */
    public List<ClassStudent> getClassStudents(Long classId) {
        return classStudentRepository.findByClassEntityId(classId);
    }

    public List<ClassEntity> getAllClasses() {
        return classRepository.findAll();
    }

    public ClassEntity getClassById(Long id) {
        return classRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Class not found"));
    }

    @Transactional
    public ClassEntity updateClass(Long id, ClassEntity classDetails) {
        ClassEntity classEntity = getClassById(id);

        classEntity.setClassName(classDetails.getClassName());
        classEntity.setClassCode(classDetails.getClassCode()); // careful with unique constraint check
        classEntity.setStartDate(classDetails.getStartDate());
        classEntity.setEndDate(classDetails.getEndDate());
        classEntity.setCapacity(classDetails.getCapacity());
        classEntity.setStatus(classDetails.getStatus());
        // Handle course change if needed, usually fixed

        return classRepository.save(classEntity);
    }

    public void addStudentToClass(Long classId, Long studentId) {
        enrollStudent(classId, studentId);
    }

    public List<User> getStudentsByClass(Long classId) {
        return getClassStudents(classId).stream()
                .map(ClassStudent::getStudent)
                .collect(java.util.stream.Collectors.toList());
    }

    public List<ClassStudent> getStudentClasses(Long studentId) {
        return classStudentRepository.findByStudentId(studentId);
    }

    public List<ClassTeacher> getClassTeachers(Long classId) {
        return classTeacherRepository.findByClassEntityId(classId);
    }

    public List<User> getTeachersByClass(Long classId) {
        return getClassTeachers(classId).stream()
                .map(ClassTeacher::getTeacher)
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Update class status
     */
    @Transactional
    public ClassEntity updateClassStatus(Long classId, ClassEntity.ClassStatus status) {
        ClassEntity classEntity = classRepository.findById(classId)
                .orElseThrow(() -> new BadRequestException("Class not found"));

        classEntity.setStatus(status);
        return classRepository.save(classEntity);
    }

    // --- Schedule Management ---

    public List<ClassSchedule> getClassSchedules(Long classId) {
        return classScheduleRepository.findByClassEntityId(classId);
    }

    @Transactional
    public ClassSchedule createSchedule(Long classId, ClassSchedule schedule) {
        ClassEntity classEntity = getClassById(classId);
        schedule.setClassEntity(classEntity);
        return classScheduleRepository.save(schedule);
    }

    // --- Attendance Management ---

    public List<Attendance> getAttendance(Long scheduleId) {
        return attendanceRepository.findByScheduleId(scheduleId);
    }

    // ... existing attendance methods ...

    @Transactional
    public void markAttendance(Long scheduleId, List<Attendance> attendances) {
        ClassSchedule schedule = classScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new BadRequestException("Schedule not found"));

        for (Attendance att : attendances) {
            att.setSchedule(schedule);
            // Verify student is in class? (Optional but recommended)
            attendanceRepository.save(att);
        }
    }

    // --- Lesson Quiz Management ---

    public List<LessonQuiz> getLessonQuizzes(Long classId) {
        return lessonQuizRepository.findByClassEntityId(classId);
    }

    public List<LessonQuiz> getLessonQuizzesByTeacher(Long teacherId) {
        return lessonQuizRepository.findByCreatedById(teacherId);
    }

    @Transactional
    public LessonQuiz createLessonQuiz(Long classId, LessonQuiz quiz, Long teacherId) {
        ClassEntity classEntity = getClassById(classId);
        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found"));

        quiz.setClassEntity(classEntity);
        quiz.setCreatedBy(teacher);

        return lessonQuizRepository.save(quiz);
    }
}
