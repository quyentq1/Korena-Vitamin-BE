package com.trainingcenter.repository;

import com.trainingcenter.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findByScheduleId(Long scheduleId);
    List<Attendance> findByStudentId(Long studentId);
    Optional<Attendance> findByScheduleIdAndStudentId(Long scheduleId, Long studentId);
    
    /**
     * Calculate attendance rate for a student in a class
     */
    @Query("SELECT COUNT(a) * 100.0 / (SELECT COUNT(cs) FROM ClassSchedule cs WHERE cs.classEntity.id = :classId) " +
           "FROM Attendance a WHERE a.student.id = :studentId AND a.status = 'PRESENT' " +
           "AND a.schedule.classEntity.id = :classId")
    Double calculateAttendanceRate(@Param("studentId") Long studentId, @Param("classId") Long classId);
}
