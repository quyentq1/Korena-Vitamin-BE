package com.trainingcenter.service;

import com.trainingcenter.exception.BadRequestException;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class OcrService {

    @Value("${ocr.tessdata.path}")
    private String tessdataPath;

    @Value("${ocr.language}")
    private String language;

    @Value("${file.ocr-dir}")
    private String ocrDir;

    public Map<String, String> processFormImage(MultipartFile file) {
        // Validate tessdata existence
        validateTessdata();

        try {
            // Save uploaded file temporarily
            Path uploadPath = Paths.get(ocrDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(filename);
            file.transferTo(filePath.toAbsolutePath().toFile());

            // Perform OCR
            Tesseract tesseract = new Tesseract();
            tesseract.setDatapath(tessdataPath);
            tesseract.setLanguage(language);

            String ocrResult = tesseract.doOCR(filePath.toFile());

            // Parse OCR result
            Map<String, String> parsedData = parseRegistrationForm(ocrResult);
            parsedData.put("imageUrl", filePath.toString());
            parsedData.put("rawOcrText", ocrResult);

            return parsedData;

        } catch (TesseractException e) {
            throw new BadRequestException("OCR processing failed: " + e.getMessage());
        } catch (IOException e) {
            throw new BadRequestException("File processing failed: " + e.getMessage());
        }
    }

    private Map<String, String> parseRegistrationForm(String ocrText) {
        Map<String, String> data = new HashMap<>();

        // Parse name
        Pattern namePattern = Pattern.compile("(?:name|họ tên|ten)\\s*:?\\s*([\\p{L}\\s]+)",
                Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS);
        Matcher nameMatcher = namePattern.matcher(ocrText);
        if (nameMatcher.find()) {
            data.put("studentName", nameMatcher.group(1).trim());
        }

        // Parse email
        Pattern emailPattern = Pattern.compile("([a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,})");
        Matcher emailMatcher = emailPattern.matcher(ocrText);
        if (emailMatcher.find()) {
            data.put("email", emailMatcher.group(1).trim());
        }

        // Parse phone
        Pattern phonePattern = Pattern.compile("(?:phone|điện thoại|sđt|dt)\\s*:?\\s*([0-9\\s\\-\\+]+)",
                Pattern.CASE_INSENSITIVE);
        Matcher phoneMatcher = phonePattern.matcher(ocrText);
        if (phoneMatcher.find()) {
            data.put("phone", phoneMatcher.group(1).replaceAll("[\\s\\-]", "").trim());
        }

        // Parse address
        Pattern addressPattern = Pattern.compile("(?:address|địa chỉ|dia chi)\\s*:?\\s*([\\p{L}0-9\\s,.-]+)",
                Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS);
        Matcher addressMatcher = addressPattern.matcher(ocrText);
        if (addressMatcher.find()) {
            data.put("address", addressMatcher.group(1).trim());
        }

        // Parse course code
        Pattern coursePattern = Pattern.compile("(?:course|khóa học|ma khoa hoc|course code)\\s*:?\\s*([A-Z0-9]+)",
                Pattern.CASE_INSENSITIVE);
        Matcher courseMatcher = coursePattern.matcher(ocrText);
        if (courseMatcher.find()) {
            data.put("courseCode", courseMatcher.group(1).toUpperCase().trim());
        }

        return data;
    }

    public String exportToWord(String ocrText, String studentName) {
        try {
            Path uploadPath = Paths.get(ocrDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String filename = System.currentTimeMillis() + "_" + studentName.replaceAll("\\s+", "_") + ".docx";
            Path outputPath = uploadPath.resolve(filename);

            XWPFDocument document = new XWPFDocument();

            // Add title
            XWPFParagraph titleParagraph = document.createParagraph();
            XWPFRun titleRun = titleParagraph.createRun();
            titleRun.setText("REGISTRATION FORM - PHIẾU ĐĂNG KÝ");
            titleRun.setBold(true);
            titleRun.setFontSize(16);

            // Add content
            XWPFParagraph contentParagraph = document.createParagraph();
            XWPFRun contentRun = contentParagraph.createRun();
            contentRun.setText(ocrText);
            contentRun.setFontSize(12);

            // Write to file
            try (FileOutputStream out = new FileOutputStream(outputPath.toFile())) {
                document.write(out);
            }
            document.close();

            return outputPath.toString();

        } catch (IOException e) {
            throw new BadRequestException("Failed to export to Word: " + e.getMessage());
        }
    }

    private void validateTessdata() {
        Path tessPath = Paths.get(tessdataPath);
        if (!Files.exists(tessPath) || !Files.isDirectory(tessPath)) {
            throw new BadRequestException("Tesseract data directory missing at: " + tessPath.toAbsolutePath() +
                    ". Please create 'tessdata' folder in project root.");
        }

        String[] langs = language.split("\\+");
        for (String lang : langs) {
            Path langFile = tessPath.resolve(lang + ".traineddata");
            if (!Files.exists(langFile)) {
                throw new BadRequestException(
                        "Missing Tesseract language file: " + lang + ".traineddata in " + tessPath.toAbsolutePath() +
                                ". Please download it from https://github.com/tesseract-ocr/tessdata");
            }
        }
    }
}
