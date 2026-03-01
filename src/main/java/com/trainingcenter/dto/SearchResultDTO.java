package com.trainingcenter.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchResultDTO {
    private String type; // "course" or "exam"
    private Long id;
    private String title;
    private String description;
    private String code; // For courses
    private Integer duration; // For exams in minutes
    private Integer questionCount; // For exams
    private String category; // For courses/exams
    private String imageUrl;
    private String url; // Navigation URL

    public static SearchResultDTO fromCourse(Object[] courseData) {
        SearchResultDTO dto = new SearchResultDTO();
        dto.setType("course");
        dto.setId(((Number) courseData[0]).longValue());
        dto.setTitle((String) courseData[1]);
        dto.setDescription((String) courseData[2]);
        dto.setCode((String) courseData[3]);
        dto.setCategory((String) courseData[4]);
        dto.setUrl("/courses/" + dto.getId());
        return dto;
    }

    public static SearchResultDTO fromExam(Object[] examData) {
        SearchResultDTO dto = new SearchResultDTO();
        dto.setType("exam");
        dto.setId(((Number) examData[0]).longValue());
        dto.setTitle((String) examData[1]);
        dto.setDescription((String) examData[2]);
        dto.setDuration(examData[3] != null ? ((Number) examData[3]).intValue() : null);
        dto.setQuestionCount(examData[4] != null ? ((Number) examData[4]).intValue() : null);
        dto.setCategory((String) examData[5]);
        dto.setUrl("/free-tests");
        return dto;
    }
}
