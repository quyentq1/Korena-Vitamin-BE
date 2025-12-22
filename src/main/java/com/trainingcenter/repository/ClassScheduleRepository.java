package com.trainingcenter.repository;

import com.trainingcenter.entity.ClassSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ClassScheduleRepository extends JpaRepository<ClassSchedule, Long> {
    List<ClassSchedule> findByClassEntityId(Long classId);
    List<ClassSchedule> findByClassEntityIdOrderByLessonNumberAsc(Long classId);
    List<ClassSchedule> findByLessonDateBetween(LocalDate startDate, LocalDate endDate);
}
