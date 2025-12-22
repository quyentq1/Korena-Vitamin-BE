package com.trainingcenter.repository;

import com.trainingcenter.entity.Course;
import com.trainingcenter.entity.CourseRegistration;
import com.trainingcenter.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRegistrationRepository extends JpaRepository<CourseRegistration, Long> {
    List<CourseRegistration> findByUser(User user);

    List<CourseRegistration> findByCourse(Course course);

    List<CourseRegistration> findByStatus(CourseRegistration.RegistrationStatus status);

    List<CourseRegistration> findByUserAndCourse(User user, Course course);

    List<CourseRegistration> findByCourseAndStatus(Course course, CourseRegistration.RegistrationStatus status);
}
