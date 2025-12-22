package com.trainingcenter.service;

import com.trainingcenter.entity.*;
import com.trainingcenter.repository.*;
import com.trainingcenter.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionImportService {

    private final QuestionRepository questionRepository;
    private final QuestionCategoryRepository categoryRepository;
    private final UserRepository userRepository;

    @Transactional
    public List<Question> importFromExcel(MultipartFile file, Long teacherId) {
        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new BadRequestException("Teacher not found"));

        List<Question> importedQuestions = new ArrayList<>();

        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            
            // Skip header row
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                importedQuestions.add(parseQuestionRow(row, teacher));
            }
            
            return questionRepository.saveAll(importedQuestions);

        } catch (IOException e) {
            throw new BadRequestException("Failed to parse Excel file: " + e.getMessage());
        }
    }

    private Question parseQuestionRow(Row row, User teacher) {
        // Expected Columns: 
        // 0: CategoryName, 1: Type, 2: Text, 3: Difficulty, 4: Points, 5: CorrectAnswer, 6: Options (comma split)
        
        String categoryName = getCellValue(row.getCell(0));
        String typeStr = getCellValue(row.getCell(1));
        String text = getCellValue(row.getCell(2));
        String diffStr = getCellValue(row.getCell(3));
        String pointsStr = getCellValue(row.getCell(4));
        String correctAnswer = getCellValue(row.getCell(5));
        String optionsStr = getCellValue(row.getCell(6));

        if (text == null || text.trim().isEmpty()) {
            return null; // Skip empty rows
        }

        Question question = new Question();
        question.setQuestionText(text);
        question.setCreatedBy(teacher);
        question.setActive(true);
        question.setCorrectAnswer(correctAnswer);

        // Category (Find or Create)
        QuestionCategory category = categoryRepository.findByName(categoryName)
                .orElseGet(() -> {
                     QuestionCategory newCat = new QuestionCategory();
                     newCat.setName(categoryName);
                     return categoryRepository.save(newCat);
                });
        question.setCategory(category);

        // Type
        try {
            question.setQuestionType(Question.QuestionType.valueOf(typeStr.toUpperCase()));
        } catch (IllegalArgumentException e) {
            question.setQuestionType(Question.QuestionType.MULTIPLE_CHOICE); // Default
        }

        // Difficulty
        try {
            question.setDifficulty(Question.Difficulty.valueOf(diffStr.toUpperCase()));
        } catch (IllegalArgumentException e) {
            question.setDifficulty(Question.Difficulty.MEDIUM);
        }

        // Points
        try {
            question.setPoints((int) Double.parseDouble(pointsStr));
        } catch (Exception e) {
            question.setPoints(1);
        }

        // Options
        if (question.getQuestionType() == Question.QuestionType.MULTIPLE_CHOICE && optionsStr != null) {
            List<QuestionOption> options = new ArrayList<>();
            String[] parts = optionsStr.split("\\|"); // Use delimiter
            int order = 1;
            for (String part : parts) {
                QuestionOption opt = new QuestionOption();
                opt.setOptionText(part.trim());
                opt.setOptionOrder(order++);
                
                // Simple logic for correctness: if option text == correct answer
                // Or user provides 1|2|3|4 and says '1' is correct.
                // Or structure: OptionA|OptionB, Correct is OptionA?
                // Let's assume correct answer field matches one of the options exactly.
                opt.setIsCorrect(part.trim().equalsIgnoreCase(correctAnswer.trim()));
                
                opt.setQuestion(question);
                options.add(opt);
            }
            question.setOptions(options);
        }
        
        return question;
    }

    private String getCellValue(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING: return cell.getStringCellValue();
            case NUMERIC: return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN: return String.valueOf(cell.getBooleanCellValue());
            default: return "";
        }
    }
}
