package com.trainingcenter.dto;

import com.trainingcenter.entity.Question;
import lombok.Data;

@Data
public class ExamStructureDto {
    private Long categoryId;
    private Question.QuestionType questionType;
    private Question.Difficulty difficulty;
    private Integer count;
    private Integer pointsPerQuestion;
}
