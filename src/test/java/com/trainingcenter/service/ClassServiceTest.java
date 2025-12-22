package com.trainingcenter.service;

import com.trainingcenter.entity.*;
import com.trainingcenter.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ClassService
 * Tests class enrollment capacity logic
 */
@ExtendWith(MockitoExtension.class)
class ClassServiceTest {

    @Mock
    private ClassRepository classRepository;

    @Mock
    private ClassStudentRepository classStudentRepository;

    @Mock
    private ClassTeacherRepository classTeacherRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ClassService classService;

    private Course testCourse;
    private ClassEntity testClass;
    private User testStudent;
    private User testTeacher;

    @BeforeEach
    void setUp() {
        testCourse = new Course();
        testCourse.setId(1L);

        testClass = new ClassEntity();
        testClass.setId(1L);
        testClass.setClassCode("TOPIK1-2025-01");
        testClass.setCapacity(30);
        testClass.setCurrentEnrollment(0);

        testStudent = new User();
        testStudent.setId(1L);
        testStudent.setRole(User.UserRole.STUDENT);

        testTeacher = new User();
        testTeacher.setId(2L);
        testTeacher.setRole(User.UserRole.TEACHER);
    }

    @Test
    void createClass_Success() {
        // Given
        when(courseRepository.findById(1L)).thenReturn(Optional.of(testCourse));
        when(classRepository.findByClassCode(anyString())).thenReturn(Optional.empty());
        when(classRepository.save(any(ClassEntity.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        ClassEntity created = classService.createClass(
            1L, "TOPIK1-2025-01", "TOPIK I - 2025 Batch 1",
            30, LocalDate.now(), LocalDate.now().plusMonths(3)
        );

        // Then
        assertNotNull(created);
        assertEquals("TOPIK1-2025-01", created.getClassCode());
        assertEquals(ClassEntity.ClassStatus.PLANNED, created.getStatus());
        assertEquals(0, created.getCurrentEnrollment());
    }

    @Test
    void enrollStudent_Success() {
        // Given
        when(classRepository.findById(1L)).thenReturn(Optional.of(testClass));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testStudent));
        when(classStudentRepository.findByClassEntityIdAndStudentId(1L, 1L))
            .thenReturn(Optional.empty());
        when(classStudentRepository.save(any(ClassStudent.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
        when(classRepository.save(any(ClassEntity.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        ClassStudent enrollment = classService.enrollStudent(1L, 1L);

        // Then
        assertNotNull(enrollment);
        assertEquals(ClassStudent.EnrollmentStatus.ACTIVE, enrollment.getStatus());
        
        // Verify enrollment count was incremented
        verify(classRepository).save(argThat(c -> c.getCurrentEnrollment() == 1));
    }

    @Test
    void enrollStudent_ClassFull_ThrowsException() {
        // Given: Class is at capacity
        testClass.setCurrentEnrollment(30); // Full
        when(classRepository.findById(1L)).thenReturn(Optional.of(testClass));

        // When & Then
        assertThrows(Exception.class, () -> {
            classService.enrollStudent(1L, 1L);
        }, "Should throw exception when class is full");
    }

    @Test
    void enrollStudent_AlreadyEnrolled_ThrowsException() {
        // Given
        when(classRepository.findById(1L)).thenReturn(Optional.of(testClass));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testStudent));
        when(classStudentRepository.findByClassEntityIdAndStudentId(1L, 1L))
            .thenReturn(Optional.of(new ClassStudent())); // Already enrolled

        // When & Then
        assertThrows(Exception.class, () -> {
            classService.enrollStudent(1L, 1L);
        }, "Should throw exception when student already enrolled");
    }

    @Test
    void assignTeacher_Success() {
        // Given
        when(classRepository.findById(1L)).thenReturn(Optional.of(testClass));
        when(userRepository.findById(2L)).thenReturn(Optional.of(testTeacher));
        when(classTeacherRepository.save(any(ClassTeacher.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        ClassTeacher assignment = classService.assignTeacher(1L, 2L, true);

        // Then
        assertNotNull(assignment);
        assertEquals(true, assignment.getIsPrimary());
    }

    @Test
    void assignTeacher_NonTeacherRole_ThrowsException() {
        // Given: User is not a teacher
        testStudent.setRole(User.UserRole.STUDENT);
        when(classRepository.findById(1L)).thenReturn(Optional.of(testClass));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testStudent));

        // When & Then
        assertThrows(Exception.class, () -> {
            classService.assignTeacher(1L, 1L, true);
        }, "Should throw exception when user is not a teacher");
    }
}
