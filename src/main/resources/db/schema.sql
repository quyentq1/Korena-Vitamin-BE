# Database Schema: Training Center Management System

-- Create database
CREATE DATABASE IF NOT EXISTS k_vitamin_center;
USE k_vitamin_center;

-- Users table (Admin, Staff, Teacher, Student)
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE,
    phone VARCHAR(20),
    role ENUM('ADMIN', 'STAFF', 'TEACHER', 'STUDENT') NOT NULL,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Courses table
CREATE TABLE courses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(20) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    fee DECIMAL(10, 2) NOT NULL,
    capacity INT NOT NULL,
    start_date DATE,
    end_date DATE,
    schedule VARCHAR(200),
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_code (code),
    INDEX idx_active (active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Course registrations table
CREATE TABLE course_registrations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT COMMENT 'NULL for guest registrations before account creation',
    course_id BIGINT NOT NULL,
    registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status ENUM('PENDING', 'APPROVED', 'REJECTED', 'COMPLETED') DEFAULT 'PENDING',
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_course_id (course_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Question categories table
CREATE TABLE question_categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Questions table
CREATE TABLE questions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    category_id BIGINT NOT NULL,
    created_by BIGINT NOT NULL,
    question_type ENUM('MULTIPLE_CHOICE', 'SHORT_ANSWER', 'ESSAY', 'LISTENING', 'SPEAKING') NOT NULL,
    question_text TEXT NOT NULL,
    question_media_url VARCHAR(500),
    correct_answer TEXT,
    points INT DEFAULT 1,
    difficulty ENUM('EASY', 'MEDIUM', 'HARD') DEFAULT 'MEDIUM',
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES question_categories(id) ON DELETE CASCADE,
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_category_id (category_id),
    INDEX idx_question_type (question_type),
    INDEX idx_difficulty (difficulty),
    INDEX idx_active (active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Question options table (for multiple choice questions)
CREATE TABLE question_options (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    question_id BIGINT NOT NULL,
    option_text TEXT NOT NULL,
    is_correct BOOLEAN DEFAULT FALSE,
    option_order INT NOT NULL,
    FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE,
    INDEX idx_question_id (question_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Exams table
CREATE TABLE exams (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_id BIGINT NOT NULL,
    created_by BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    duration_minutes INT NOT NULL,
    total_points INT NOT NULL,
    available_from TIMESTAMP,
    available_to TIMESTAMP,
    published BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE,
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_course_id (course_id),
    INDEX idx_published (published)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Exam questions table (junction table with points override)
CREATE TABLE exam_questions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    exam_id BIGINT NOT NULL,
    question_id BIGINT NOT NULL,
    question_order INT NOT NULL,
    points INT NOT NULL,
    FOREIGN KEY (exam_id) REFERENCES exams(id) ON DELETE CASCADE,
    FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE,
    INDEX idx_exam_id (exam_id),
    INDEX idx_question_id (question_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Exam attempts table
CREATE TABLE exam_attempts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    exam_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    start_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    end_time TIMESTAMP,
    submit_time TIMESTAMP,
    auto_score DECIMAL(5, 2) DEFAULT 0,
    manual_score DECIMAL(5, 2) DEFAULT 0,
    total_score DECIMAL(5, 2) DEFAULT 0,
    status ENUM('IN_PROGRESS', 'SUBMITTED', 'GRADED', 'PENDING_MANUAL_GRADE') DEFAULT 'IN_PROGRESS',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (exam_id) REFERENCES exams(id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_exam_id (exam_id),
    INDEX idx_student_id (student_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Student answers table
CREATE TABLE student_answers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    attempt_id BIGINT NOT NULL,
    exam_question_id BIGINT NOT NULL,
    answer_text TEXT,
    answer_file_url VARCHAR(500),
    is_correct BOOLEAN,
    score DECIMAL(5, 2) DEFAULT 0,
    answered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (attempt_id) REFERENCES exam_attempts(id) ON DELETE CASCADE,
    FOREIGN KEY (exam_question_id) REFERENCES exam_questions(id) ON DELETE CASCADE,
    INDEX idx_attempt_id (attempt_id),
    INDEX idx_exam_question_id (exam_question_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Manual grades table (for essay, speaking, writing questions)
CREATE TABLE manual_grades (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    attempt_id BIGINT NOT NULL,
    exam_question_id BIGINT NOT NULL,
    graded_by BIGINT NOT NULL,
    score DECIMAL(5, 2) NOT NULL,
    feedback TEXT,
    graded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (attempt_id) REFERENCES exam_attempts(id) ON DELETE CASCADE,
    FOREIGN KEY (exam_question_id) REFERENCES exam_questions(id) ON DELETE CASCADE,
    FOREIGN KEY (graded_by) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_attempt_id (attempt_id),
    INDEX idx_graded_by (graded_by)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Registration forms table (for OCR processing)
CREATE TABLE registration_forms (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    form_number VARCHAR(50) UNIQUE,
    student_name VARCHAR(100),
    email VARCHAR(100),
    phone VARCHAR(20),
    address TEXT,
    course_code VARCHAR(20),
    form_image_url VARCHAR(500),
    ocr_text TEXT,
    status ENUM('PENDING', 'PROCESSED', 'APPROVED', 'REJECTED') DEFAULT 'PENDING',
    processed_by BIGINT,
    scanned_at TIMESTAMP,
    processed_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (processed_by) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_form_number (form_number),
    INDEX idx_status (status),
    INDEX idx_course_code (course_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- SAMPLE DATA FOR TESTING
-- =====================================================

-- Insert users: Admin, Staff, Teachers, Students
-- All passwords: admin123 (bcrypt hash)
INSERT INTO users (username, password, full_name, email, phone, role, active) VALUES
('admin', '$2a$10$7l8qyBXQcBE/u9V3hW8H9.bfGXGBKO5vLqZ7ZNPJ8x8zKqYxqHfKy', 'System Administrator', 'admin@trainingcenter.com', '0123456789', 'ADMIN', TRUE),
('staff01', '$2a$10$7l8qyBXQcBE/u9V3hW8H9.bfGXGBKO5vLqZ7ZNPJ8x8zKqYxqHfKy', 'Nguyen Van A', 'staff01@trainingcenter.com', '0912345678', 'STAFF', TRUE),
('teacher01', '$2a$10$7l8qyBXQcBE/u9V3hW8H9.bfGXGBKO5vLqZ7ZNPJ8x8zKqYxqHfKy', 'Tran Thi B', 'teacher01@trainingcenter.com', '0923456789', 'TEACHER', TRUE),
('teacher02', '$2a$10$7l8qyBXQcBE/u9V3hW8H9.bfGXGBKO5vLqZ7ZNPJ8x8zKqYxqHfKy', 'Le Van C', 'teacher02@trainingcenter.com', '0934567890', 'TEACHER', TRUE),
('student01', '$2a$10$7l8qyBXQcBE/u9V3hW8H9.bfGXGBKO5vLqZ7ZNPJ8x8zKqYxqHfKy', 'Pham Thi D', 'student01@trainingcenter.com', '0945678901', 'STUDENT', TRUE),
('student02', '$2a$10$7l8qyBXQcBE/u9V3hW8H9.bfGXGBKO5vLqZ7ZNPJ8x8zKqYxqHfKy', 'Hoang Van E', 'student02@trainingcenter.com', '0956789012', 'STUDENT', TRUE),
('student03', '$2a$10$7l8qyBXQcBE/u9V3hW8H9.bfGXGBKO5vLqZ7ZNPJ8x8zKqYxqHfKy', 'Vo Thi F', 'student03@trainingcenter.com', '0967890123', 'STUDENT', TRUE);

-- Insert question categories
INSERT INTO question_categories (name, description) VALUES
('Grammar', 'Grammar questions covering tenses, sentence structure, etc.'),
('Vocabulary', 'Vocabulary and word usage questions'),
('Reading', 'Reading comprehension questions'),
('Listening', 'Listening comprehension questions'),
('Speaking', 'Speaking and pronunciation questions'),
('Writing', 'Writing and composition questions');

-- Insert courses
INSERT INTO courses (code, name, description, fee, capacity, start_date, end_date, schedule, active) VALUES
('ENG101', 'English Foundation', 'Beginner English course covering basic grammar, vocabulary, and conversation', 2000000, 30, '2025-01-15', '2025-03-15', 'Monday, Wednesday, Friday 18:00-20:00', TRUE),
('ENG201', 'English Intermediate', 'Intermediate English course for students with basic knowledge', 2500000, 25, '2025-01-20', '2025-04-20', 'Tuesday, Thursday 18:00-20:00', TRUE),
('ENG301', 'English Advanced', 'Advanced English course focusing on business communication', 3000000, 20, '2025-02-01', '2025-05-01', 'Monday, Wednesday 19:00-21:00', TRUE);

-- Insert sample questions (teacher01 = id 3)
INSERT INTO questions (category_id, created_by, question_type, question_text, correct_answer, points, difficulty, active) VALUES
-- Grammar questions (category_id = 1)
(1, 3, 'MULTIPLE_CHOICE', 'Choose the correct form: She _____ to school every day.', NULL, 1, 'EASY', TRUE),
(1, 3, 'MULTIPLE_CHOICE', 'What is the past tense of "go"?', NULL, 1, 'EASY', TRUE),
(1, 3, 'SHORT_ANSWER', 'Fill in the blank: I _____ (to be) a student.', 'am', 1, 'EASY', TRUE),
(1, 3, 'MULTIPLE_CHOICE', 'Which sentence is correct?', NULL, 2, 'MEDIUM', TRUE),

-- Vocabulary questions (category_id = 2)
(2, 3, 'MULTIPLE_CHOICE', 'What does "beautiful" mean?', NULL, 1, 'EASY', TRUE),
(2, 3, 'MULTIPLE_CHOICE', 'Choose the synonym of "happy":', NULL, 1, 'EASY', TRUE),
(2, 3, 'SHORT_ANSWER', 'What is the opposite of "hot"?', 'cold', 1, 'EASY', TRUE),

-- Reading questions (category_id = 3)
(3, 3, 'ESSAY', 'Read the passage and explain the main idea in your own words.', NULL, 5, 'MEDIUM', TRUE),
(3, 3, 'MULTIPLE_CHOICE', 'According to the text, what time does the store open?', NULL, 2, 'MEDIUM', TRUE),

-- Listening questions (category_id = 4)
(4, 3, 'LISTENING', 'Listen to the audio and answer: What is the speaker talking about?', NULL, 3, 'MEDIUM', TRUE),

-- Speaking questions (category_id = 5)
(5, 3, 'SPEAKING', 'Introduce yourself in English (30 seconds).', NULL, 5, 'MEDIUM', TRUE),

-- Writing questions (category_id = 6)
(6, 3, 'ESSAY', 'Write a short paragraph about your favorite hobby (100 words).', NULL, 5, 'MEDIUM', TRUE);

-- Insert question options for multiple choice questions
-- Question 1: She _____ to school every day
INSERT INTO question_options (question_id, option_text, is_correct, option_order) VALUES
(1, 'go', FALSE, 1),
(1, 'goes', TRUE, 2),
(1, 'going', FALSE, 3),
(1, 'gone', FALSE, 4);

-- Question 2: Past tense of "go"
INSERT INTO question_options (question_id, option_text, is_correct, option_order) VALUES
(2, 'goed', FALSE, 1),
(2, 'went', TRUE, 2),
(2, 'gone', FALSE, 3),
(2, 'going', FALSE, 4);

-- Question 4: Which sentence is correct
INSERT INTO question_options (question_id, option_text, is_correct, option_order) VALUES
(4, 'He don''t like coffee', FALSE, 1),
(4, 'He doesn''t like coffee', TRUE, 2),
(4, 'He doesn''t likes coffee', FALSE, 3),
(4, 'He not like coffee', FALSE, 4);

-- Question 5: What does "beautiful" mean
INSERT INTO question_options (question_id, option_text, is_correct, option_order) VALUES
(5, 'Ugly', FALSE, 1),
(5, 'Pretty and attractive', TRUE, 2),
(5, 'Sad', FALSE, 3),
(5, 'Angry', FALSE, 4);

-- Question 6: Synonym of "happy"
INSERT INTO question_options (question_id, option_text, is_correct, option_order) VALUES
(6, 'Sad', FALSE, 1),
(6, 'Joyful', TRUE, 2),
(6, 'Angry', FALSE, 3),
(6, 'Tired', FALSE, 4);

-- Question 9: Store opening time
INSERT INTO question_options (question_id, option_text, is_correct, option_order) VALUES
(9, '8:00 AM', FALSE, 1),
(9, '9:00 AM', TRUE, 2),
(9, '10:00 AM', FALSE, 3),
(9, '11:00 AM', FALSE, 4);

-- Insert course registrations
INSERT INTO course_registrations (user_id, course_id, registration_date, status, notes) VALUES
(5, 1, NOW(), 'APPROVED', 'Regular registration'),
(6, 1, NOW(), 'APPROVED', 'Regular registration'),
(7, 2, NOW(), 'PENDING', 'Waiting for payment confirmation'),
(NULL, 1, NOW(), 'PENDING', 'Name: Nguyen Van G\nEmail: student04@example.com\nPhone: 0978901234\nAddress: 123 Main St, HCMC');

-- Insert sample exam (created by teacher01)
INSERT INTO exams (course_id, created_by, title, description, duration_minutes, total_points, available_from, available_to, published) VALUES
(1, 3, 'English Foundation - Mid-term Test', 'Mid-term examination covering grammar and vocabulary', 60, 20, '2025-01-20 08:00:00', '2025-01-25 23:59:59', TRUE),
(1, 3, 'English Foundation - Final Test', 'Final examination covering all topics', 90, 40, '2025-03-10 08:00:00', '2025-03-15 23:59:59', FALSE);

-- Insert exam questions (linking questions to exam 1)
INSERT INTO exam_questions (exam_id, question_id, question_order, points) VALUES
(1, 1, 1, 2),
(1, 2, 2, 2),
(1, 3, 3, 2),
(1, 4, 4, 3),
(1, 5, 5, 2),
(1, 6, 6, 2),
(1, 7, 7, 2),
(1, 8, 8, 5);

-- Insert sample exam attempt (student01 taking exam 1)
INSERT INTO exam_attempts (exam_id, student_id, start_time, end_time, submit_time, auto_score, manual_score, total_score, status) VALUES
(1, 5, '2025-01-21 10:00:00', '2025-01-21 11:00:00', '2025-01-21 10:45:00', 12.0, 4.0, 16.0, 'GRADED');

-- Insert student answers for the attempt
INSERT INTO student_answers (attempt_id, exam_question_id, answer_text, is_correct, score) VALUES
(1, 1, '2', TRUE, 2.0),  -- Correct: goes
(1, 2, '2', TRUE, 2.0),  -- Correct: went
(1, 3, 'am', TRUE, 2.0), -- Correct
(1, 4, '2', TRUE, 3.0),  -- Correct
(1, 5, '2', TRUE, 2.0),  -- Correct: Pretty
(1, 6, '1', FALSE, 0.0), -- Wrong answer
(1, 7, 'cold', TRUE, 1.0), -- Correct
(1, 8, 'My favorite hobby is reading books. I love reading because it helps me relax and learn new things...', NULL, 0.0); -- Essay - needs manual grading

-- Insert manual grade for essay question
INSERT INTO manual_grades (attempt_id, exam_question_id, graded_by, score, feedback) VALUES
(1, 8, 3, 4.0, 'Good essay structure and vocabulary. Minor grammar errors. Keep up the good work!');

-- Insert sample OCR registration form
INSERT INTO registration_forms (form_number, student_name, email, phone, address, course_code, form_image_url, ocr_text, status, scanned_at) VALUES
('FORM-2025-001', 'Tran Van H', 'student05@example.com', '0989012345', '456 Second St, Hanoi', 'ENG101', '/uploads/ocr/form001.jpg', 'Name: Tran Van H\nEmail: student05@example.com\nPhone: 0989012345\nAddress: 456 Second St, Hanoi\nCourse: ENG101', 'PENDING', NOW());
