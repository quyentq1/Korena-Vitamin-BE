package com.trainingcenter.dto;

import lombok.Data;
import java.util.List;

@Data
public class ExamGenerationRequest {
    private Long courseId;
    private String title;
    private String description;
    private Integer durationMinutes;
    private List<ExamStructureDto> structure;
}
