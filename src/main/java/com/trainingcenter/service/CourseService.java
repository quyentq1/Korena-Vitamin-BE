package com.trainingcenter.service;

import com.trainingcenter.entity.Course;
import com.trainingcenter.entity.CourseRegistration;
import com.trainingcenter.entity.User;
import com.trainingcenter.exception.BadRequestException;
import com.trainingcenter.exception.ResourceNotFoundException;
import com.trainingcenter.repository.CourseRegistrationRepository;
import com.trainingcenter.repository.CourseRepository;
import com.trainingcenter.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CourseRegistrationRepository registrationRepository;

    @Autowired
    private UserRepository userRepository;

    public Course createCourse(Course course) {
        if (courseRepository.existsByCode(course.getCode())) {
            throw new BadRequestException("Course code already exists");
        }
        return courseRepository.save(course);
    }

    public Course updateCourse(Long id, Course courseDetails) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));

        // Check code uniqueness if changed
        if (!course.getCode().equals(courseDetails.getCode()) &&
                courseRepository.existsByCode(courseDetails.getCode())) {
            throw new BadRequestException("Course code already exists");
        }

        course.setCode(courseDetails.getCode());
        course.setName(courseDetails.getName());
        course.setDescription(courseDetails.getDescription());
        course.setFee(courseDetails.getFee());
        course.setCapacity(courseDetails.getCapacity());
        course.setStartDate(courseDetails.getStartDate());
        course.setEndDate(courseDetails.getEndDate());
        course.setSchedule(courseDetails.getSchedule());
        course.setActive(courseDetails.getActive());

        return courseRepository.save(course);
    }

    public void deleteCourse(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
        courseRepository.delete(course);
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public Course getCourseById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
    }

    public Course getCourseByCode(String code) {
        return courseRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with code: " + code));
    }

    public List<Course> getActiveCourses() {
        return courseRepository.findByActive(true);
    }

    public List<User> getStudentsByCourse(Long courseId) {
        Course course = getCourseById(courseId);
        List<CourseRegistration> registrations = registrationRepository.findByCourseAndStatus(
                course,
                CourseRegistration.RegistrationStatus.APPROVED
        );
        return registrations.stream()
                .map(CourseRegistration::getUser)
                .filter(user -> user.getRole() == User.UserRole.STUDENT)
                .toList();
    }
}
