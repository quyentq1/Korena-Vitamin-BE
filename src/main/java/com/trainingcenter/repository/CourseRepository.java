package com.trainingcenter.repository;

import com.trainingcenter.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    Optional<Course> findByCode(String code);
    List<Course> findByActive(Boolean active);
    Boolean existsByCode(String code);

    // Search courses by keyword in name, description, or code
    @Query("SELECT c.id, c.name, c.description, c.code, c.schedule FROM Course c " +
           "WHERE c.active = true AND (" +
           "LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(c.code) LIKE LOWER(CONCAT('%', :keyword, '%')))" +
           ") ORDER BY c.name")
    List<Object[]> searchCourses(@Param("keyword") String keyword);
}
