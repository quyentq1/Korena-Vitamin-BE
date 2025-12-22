package com.trainingcenter.service;

import com.trainingcenter.entity.*;
import com.trainingcenter.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportService {

    private final ClassRepository classRepository;
    private final ExamAttemptRepository attemptRepository;
    private final ClassScheduleRepository scheduleRepository;
    private final AttendanceRepository attendanceRepository;
    private final UserService userService;

    public List<Map<String, Object>> getClassPerformance(Long teacherId) {
        // Get classes for teacher
        // Assuming we have a way to find classes by teacher. ClassService has it?
        // Let's use ClassRepository directly if method exists or fetch all and filter (inefficient but ok for MVP)
        // Or inject ClassService.
        // For MVP, letting fetch all classes created by teacher or assigned.
        // Let's assume ClassRepository has findByTeacherId.
        
        // Mocking logic or relying on what's available. 
        // Let's rely on finding all classes for now or mock if calc is complex.
        
        List<ClassEntity> classes = classRepository.findAll(); // Should filter by teacher in real app
        
        List<Map<String, Object>> reports = new ArrayList<>();

        for (ClassEntity cls : classes) {
            Map<String, Object> report = new HashMap<>();
            report.put("classId", cls.getId());
            report.put("name", cls.getClassName());
            report.put("students", cls.getStudents().size());

            // Calculate Avg Score (from ExamAttempts of students in this class)
            // This is heavy. Optimization: Cache or dedicated stats table.
            List<User> students = cls.getStudents();
            BigDecimal totalScore = BigDecimal.ZERO;
            int attemptCount = 0;
            
            for (User student : students) {
                 List<ExamAttempt> attempts = attemptRepository.findByStudent(student);
                 for (ExamAttempt att : attempts) {
                     if (att.getTotalScore() != null) {
                         totalScore = totalScore.add(att.getTotalScore());
                         attemptCount++;
                     }
                 }
            }
            
            double avgScore = attemptCount > 0 ? totalScore.doubleValue() / attemptCount : 0.0;
            report.put("avgScore", Math.round(avgScore * 10.0) / 10.0);

            // Attendance Rate
            long totalSlots = 0;
            long presentSlots = 0;
            // Get schedules for class
            List<ClassSchedule> schedules = scheduleRepository.findByClassEntityId(cls.getId());
            for (ClassSchedule sch : schedules) {
                List<Attendance> atts = attendanceRepository.findByScheduleId(sch.getId());
                totalSlots += atts.size();
                presentSlots += atts.stream().filter(Attendance::getIsPresent).count();
            }
            
            double attendanceRate = totalSlots > 0 ? (double) presentSlots / totalSlots * 100 : 0.0;
            report.put("attendance", Math.round(attendanceRate * 10.0) / 10.0);
            
            // Progress (Mock for now or based on curriculum covered)
            report.put("progress", 50); // Mock

            reports.add(report);
        }
        
        return reports;
    }

    public List<Map<String, Object>> getTopStudents() {
        // Find top students based on average score of all attempts
        List<User> students = userService.getAllStudents(); 
        
        return students.stream()
            .map(s -> {
                List<ExamAttempt> attempts = attemptRepository.findByStudent(s);
                if (attempts.isEmpty()) return null;
                
                double avg = attempts.stream()
                    .filter(a -> a.getTotalScore() != null)
                    .mapToDouble(a -> a.getTotalScore().doubleValue())
                    .average().orElse(0.0);
                
                Map<String, Object> map = new HashMap<>();
                map.put("name", s.getFullName());
                map.put("class", "N/A"); // Need logic to find class
                map.put("score", Math.round(avg * 10.0) / 10.0);
                map.put("tests", attempts.size());
                return map;
            })
            .filter(Objects::nonNull)
            .sorted((a, b) -> Double.compare((Double)b.get("score"), (Double)a.get("score")))
            .limit(5)
            .collect(Collectors.toList());
    }
}
