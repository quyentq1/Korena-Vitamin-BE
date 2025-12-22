# =====================================================
# SCHEMA EXTENSION - Training Center Management System
# Trung Tâm Hàn Ngữ Vitamin
# =====================================================
# This file extends the base schema.sql with additional tables
# to support Korean language learning center requirements
# =====================================================

USE k_vitamin_center;

-- =====================================================
-- PART 1: USER ENHANCEMENTS
-- =====================================================

-- Add missing roles and fields to users table
ALTER TABLE users 
MODIFY COLUMN role ENUM('ADMIN', 'STAFF', 'TEACHER', 'STUDENT', 'GUEST', 'LEARNER', 'EDUCATION_MANAGER') NOT NULL,
ADD COLUMN free_test_count INT DEFAULT 0 COMMENT 'Number of free tests used (max 5)',
ADD COLUMN payment_tier VARCHAR(50) DEFAULT NULL COMMENT 'Premium tier: 100K, 200K, etc.',
ADD COLUMN is_premium BOOLEAN DEFAULT FALSE COMMENT 'Has paid for premium access';

-- =====================================================
-- PART 2: EXAM SYSTEM ENHANCEMENTS (11 tables)
-- =====================================================

-- 2.1. Exam Skills (Listening, Reading, Writing, Speaking)
CREATE TABLE exam_skills (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(10) NOT NULL UNIQUE COMMENT 'N=Listening, R=Reading, W=Writing, S=Speaking',
    name VARCHAR(100) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2.2. Question Patterns (N1-N8, R1-R8, W1-W8, S1-S8)
CREATE TABLE question_patterns (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    skill_id BIGINT NOT NULL,
    pattern_code VARCHAR(10) NOT NULL COMMENT 'N1, N2, ..., N8, R1, ..., R8, etc.',
    pattern_name VARCHAR(100) NOT NULL,
    description TEXT,
    typical_question_count INT DEFAULT 0 COMMENT 'Typical number of questions for this pattern',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (skill_id) REFERENCES exam_skills(id) ON DELETE CASCADE,
    UNIQUE KEY unique_pattern (skill_id, pattern_code),
    INDEX idx_pattern_code (pattern_code),
    INDEX idx_skill_id (skill_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2.3. Link questions to patterns
ALTER TABLE questions 
ADD COLUMN pattern_id BIGINT AFTER category_id,
ADD FOREIGN KEY (pattern_id) REFERENCES question_patterns(id) ON DELETE SET NULL,
ADD INDEX idx_pattern_id (pattern_id);

-- 2.4. Exam Pattern Distribution (how many questions per pattern in exam)
CREATE TABLE exam_pattern_distribution (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    exam_id BIGINT NOT NULL,
    pattern_id BIGINT NOT NULL,
    question_count INT NOT NULL COMMENT 'Number of questions for this pattern',
    points_per_question INT DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (exam_id) REFERENCES exams(id) ON DELETE CASCADE,
    FOREIGN KEY (pattern_id) REFERENCES question_patterns(id) ON DELETE CASCADE,
    UNIQUE KEY unique_exam_pattern (exam_id, pattern_id),
    INDEX idx_exam_id (exam_id),
    INDEX idx_pattern_id (pattern_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2.5. Exam Variants (20 different exam codes A-T)
CREATE TABLE exam_variants (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    exam_id BIGINT NOT NULL,
    variant_code VARCHAR(10) NOT NULL COMMENT 'A, B, C, ..., T (20 variants)',
    variant_name VARCHAR(100),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (exam_id) REFERENCES exams(id) ON DELETE CASCADE,
    UNIQUE KEY unique_variant (exam_id, variant_code),
    INDEX idx_exam_id (exam_id),
    INDEX idx_variant_code (variant_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2.6. Variant Questions (questions in each variant)
CREATE TABLE variant_questions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    variant_id BIGINT NOT NULL,
    question_id BIGINT NOT NULL,
    question_order INT NOT NULL,
    points INT DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (variant_id) REFERENCES exam_variants(id) ON DELETE CASCADE,
    FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE,
    UNIQUE KEY unique_variant_question (variant_id, question_id),
    INDEX idx_variant_id (variant_id),
    INDEX idx_question_id (question_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2.7. Question Usage History (for Test Type 1 - no duplicate questions)
CREATE TABLE question_usage_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    question_id BIGINT NOT NULL,
    used_in_exam_id BIGINT NOT NULL,
    used_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE,
    FOREIGN KEY (used_in_exam_id) REFERENCES exams(id) ON DELETE CASCADE,
    UNIQUE KEY unique_user_question (user_id, question_id),
    INDEX idx_user_id (user_id),
    INDEX idx_question_id (question_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2.8. Test Access History (track 5 free tests + paid tests)
CREATE TABLE test_access_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    exam_id BIGINT NOT NULL,
    access_type ENUM('FREE', 'PAID') DEFAULT 'FREE',
    payment_amount DECIMAL(10, 2) DEFAULT 0,
    accessed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (exam_id) REFERENCES exams(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_access_type (access_type),
    INDEX idx_accessed_at (accessed_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2.9. Exam Config (configuration for each exam type)
CREATE TABLE exam_configs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    exam_id BIGINT NOT NULL,
    total_questions INT NOT NULL,
    allow_duplicate_questions BOOLEAN DEFAULT TRUE COMMENT 'Type 2 allows duplicates, Type 1 does not',
    randomize_questions BOOLEAN DEFAULT TRUE,
    randomize_options BOOLEAN DEFAULT TRUE,
    show_result_immediately BOOLEAN DEFAULT FALSE,
    enable_keyboard_lock BOOLEAN DEFAULT FALSE COMMENT 'Anti-cheating: lock keyboard during exam',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (exam_id) REFERENCES exams(id) ON DELETE CASCADE,
    INDEX idx_exam_id (exam_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2.11. Update exams table
ALTER TABLE exams 
ADD COLUMN exam_type ENUM('FREE_PAID', 'UNLIMITED', 'LESSON_QUIZ', 'CERTIFICATE') NOT NULL DEFAULT 'UNLIMITED' AFTER course_id,
ADD COLUMN certificate_type ENUM('TOPIK', 'OPIC', 'EPS_TOPIK', 'OTHER') AFTER exam_type,
ADD INDEX idx_exam_type (exam_type),
ADD INDEX idx_certificate_type (certificate_type);

-- =====================================================
-- PART 3: CLASS MANAGEMENT (5 tables)
-- =====================================================

-- 3.1. Classes
CREATE TABLE classes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_id BIGINT NOT NULL,
    class_code VARCHAR(50) NOT NULL UNIQUE,
    class_name VARCHAR(200) NOT NULL,
    start_date DATE,
    end_date DATE,
    capacity INT DEFAULT 30,
    current_enrollment INT DEFAULT 0,
    status ENUM('PLANNED', 'ONGOING', 'COMPLETED', 'CANCELLED') DEFAULT 'PLANNED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE,
    INDEX idx_course_id (course_id),
    INDEX idx_class_code (class_code),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3.2. Class Schedules
CREATE TABLE class_schedules (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    class_id BIGINT NOT NULL,
    lesson_number INT NOT NULL,
    lesson_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    topic VARCHAR(200),
    room VARCHAR(50),
    status ENUM('SCHEDULED', 'COMPLETED', 'CANCELLED', 'RESCHEDULED') DEFAULT 'SCHEDULED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (class_id) REFERENCES classes(id) ON DELETE CASCADE,
    INDEX idx_class_id (class_id),
    INDEX idx_lesson_date (lesson_date),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3.3. Class Teachers
CREATE TABLE class_teachers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    class_id BIGINT NOT NULL,
    teacher_id BIGINT NOT NULL,
    assigned_date DATE DEFAULT (CURRENT_DATE),
    is_primary BOOLEAN DEFAULT FALSE COMMENT 'Primary teacher vs assistant teacher',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (class_id) REFERENCES classes(id) ON DELETE CASCADE,
    FOREIGN KEY (teacher_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_class_id (class_id),
    INDEX idx_teacher_id (teacher_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3.4. Class Students
CREATE TABLE class_students (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    class_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    enrollment_date DATE DEFAULT (CURRENT_DATE),
    status ENUM('ACTIVE', 'DROPPED', 'COMPLETED') DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (class_id) REFERENCES classes(id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY unique_class_student (class_id, student_id),
    INDEX idx_class_id (class_id),
    INDEX idx_student_id (student_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3.5. Attendance
CREATE TABLE attendance (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    schedule_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    status ENUM('PRESENT', 'ABSENT', 'LATE', 'EXCUSED') DEFAULT 'PRESENT',
    notes TEXT,
    marked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (schedule_id) REFERENCES class_schedules(id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY unique_attendance (schedule_id, student_id),
    INDEX idx_schedule_id (schedule_id),
    INDEX idx_student_id (student_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3.6. Lesson Quizzes (Test Type 3 - quiz after each lesson)
CREATE TABLE lesson_quizzes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    class_id BIGINT NOT NULL,
    lesson_number INT NOT NULL,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    created_by BIGINT NOT NULL,
    duration_minutes INT DEFAULT 15,
    total_points INT DEFAULT 10,
    quiz_date DATE,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (class_id) REFERENCES classes(id) ON DELETE CASCADE,
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_class_id (class_id),
    INDEX idx_lesson_number (lesson_number),
    INDEX idx_created_by (created_by)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- PART 4: FORUM (4 tables)
-- =====================================================

-- 4.1. Forum Categories
CREATE TABLE forum_categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    display_order INT DEFAULT 0,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_display_order (display_order),
    INDEX idx_active (active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4.2. Forum Posts
CREATE TABLE forum_posts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    category_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    view_count INT DEFAULT 0,
    is_pinned BOOLEAN DEFAULT FALSE,
    is_locked BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES forum_categories(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_category_id (category_id),
    INDEX idx_user_id (user_id),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4.3. Forum Comments
CREATE TABLE forum_comments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    post_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (post_id) REFERENCES forum_posts(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_post_id (post_id),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4.4. Forum Likes
CREATE TABLE forum_likes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    post_id BIGINT,
    comment_id BIGINT,
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (post_id) REFERENCES forum_posts(id) ON DELETE CASCADE,
    FOREIGN KEY (comment_id) REFERENCES forum_comments(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CHECK (post_id IS NOT NULL OR comment_id IS NOT NULL),
    UNIQUE KEY unique_user_like (user_id, post_id, comment_id),
    INDEX idx_post_id (post_id),
    INDEX idx_comment_id (comment_id),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- PART 5: PAYMENT SYSTEM (3 tables)
-- =====================================================

-- 5.1. Payment Methods
CREATE TABLE payment_methods (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    code VARCHAR(50) NOT NULL UNIQUE,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_code (code),
    INDEX idx_is_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 5.2. Payments
CREATE TABLE payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    payment_type ENUM('COURSE_FEE', 'TEST_UNLOCK', 'OTHER') NOT NULL,
    related_id BIGINT COMMENT 'course_id or exam_id depending on payment_type',
    amount DECIMAL(10, 2) NOT NULL,
    payment_method_id BIGINT,
    status ENUM('PENDING', 'COMPLETED', 'FAILED', 'REFUNDED') DEFAULT 'PENDING',
    transaction_id VARCHAR(100),
    paid_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (payment_method_id) REFERENCES payment_methods(id) ON DELETE SET NULL,
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_payment_type (payment_type),
    INDEX idx_transaction_id (transaction_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 5.3. Invoices
CREATE TABLE invoices (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    payment_id BIGINT NOT NULL,
    invoice_number VARCHAR(50) NOT NULL UNIQUE,
    issue_date DATE NOT NULL,
    due_date DATE,
    total_amount DECIMAL(10, 2) NOT NULL,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (payment_id) REFERENCES payments(id) ON DELETE CASCADE,
    INDEX idx_payment_id (payment_id),
    INDEX idx_invoice_number (invoice_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- PART 6: NOTIFICATIONS (2 tables)
-- =====================================================

-- 6.1. Notifications
CREATE TABLE notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    message TEXT NOT NULL,
    notification_type ENUM('SYSTEM', 'COURSE', 'EXAM', 'PAYMENT', 'SCHEDULE', 'OTHER') DEFAULT 'SYSTEM',
    sender_id BIGINT COMMENT 'NULL = system notification',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_notification_type (notification_type),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 6.2. Notification Recipients
CREATE TABLE notification_recipients (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    notification_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    read_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (notification_id) REFERENCES notifications(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY unique_notification_user (notification_id, user_id),
    INDEX idx_notification_id (notification_id),
    INDEX idx_user_id (user_id),
    INDEX idx_is_read (is_read)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- PART 7: REPORTS & WORKFLOW (3 tables)
-- =====================================================

-- 7.1. Learning Reports (from teachers)
CREATE TABLE learning_reports (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    class_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    teacher_id BIGINT NOT NULL,
    report_date DATE NOT NULL,
    attendance_rate DECIMAL(5, 2),
    progress TEXT,
    strengths TEXT,
    weaknesses TEXT,
    recommendations TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (class_id) REFERENCES classes(id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (teacher_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_class_id (class_id),
    INDEX idx_student_id (student_id),
    INDEX idx_teacher_id (teacher_id),
    INDEX idx_report_date (report_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 7.2. Schedule Change Requests
CREATE TABLE schedule_change_requests (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    schedule_id BIGINT NOT NULL,
    requested_by BIGINT NOT NULL COMMENT 'Teacher requesting change',
    request_type ENUM('RESCHEDULE', 'CANCEL', 'ROOM_CHANGE') NOT NULL,
    reason TEXT NOT NULL,
    proposed_date DATE,
    proposed_time TIME,
    proposed_room VARCHAR(50),
    status ENUM('PENDING', 'APPROVED', 'REJECTED') DEFAULT 'PENDING',
    reviewed_by BIGINT COMMENT 'Education Manager',
    reviewed_at TIMESTAMP,
    review_notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (schedule_id) REFERENCES class_schedules(id) ON DELETE CASCADE,
    FOREIGN KEY (requested_by) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (reviewed_by) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_schedule_id (schedule_id),
    INDEX idx_status (status),
    INDEX idx_requested_by (requested_by),
    INDEX idx_reviewed_by (reviewed_by)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 7.3. Question Approvals (Education Manager approval workflow)
CREATE TABLE question_approvals (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    question_id BIGINT NOT NULL,
    submitted_by BIGINT NOT NULL COMMENT 'Teacher who created question',
    reviewed_by BIGINT COMMENT 'Education Manager',
    status ENUM('PENDING', 'APPROVED', 'REJECTED', 'REVISION_NEEDED') DEFAULT 'PENDING',
    feedback TEXT,
    duplicate_check_passed BOOLEAN DEFAULT NULL COMMENT 'AI/Tool check for duplicate content',
    submitted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    reviewed_at TIMESTAMP,
    FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE,
    FOREIGN KEY (submitted_by) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (reviewed_by) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_question_id (question_id),
    INDEX idx_status (status),
    INDEX idx_submitted_by (submitted_by),
    INDEX idx_reviewed_by (reviewed_by)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- PART 8: AI FEATURES (4 tables - Optional)
-- =====================================================

-- 8.1. Chatbot Conversations
CREATE TABLE chatbot_conversations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    session_id VARCHAR(100) NOT NULL,
    started_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ended_at TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_session_id (session_id),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 8.2. Chatbot Messages
CREATE TABLE chatbot_messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    conversation_id BIGINT NOT NULL,
    sender_type ENUM('USER', 'BOT') NOT NULL,
    message TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (conversation_id) REFERENCES chatbot_conversations(id) ON DELETE CASCADE,
    INDEX idx_conversation_id (conversation_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 8.3. AI Grading Results
CREATE TABLE ai_grading_results (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_answer_id BIGINT NOT NULL,
    ai_score DECIMAL(5, 2),
    ai_model VARCHAR(50) COMMENT 'e.g., GPT-4, Claude, Gemini',
    confidence_score DECIMAL(5, 2),
    graded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_answer_id) REFERENCES student_answers(id) ON DELETE CASCADE,
    INDEX idx_student_answer_id (student_answer_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 8.4. AI Feedback
CREATE TABLE ai_feedback (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    ai_grading_id BIGINT NOT NULL,
    feedback_type ENUM('GRAMMAR', 'VOCABULARY', 'STRUCTURE', 'CONTENT', 'PRONUNCIATION', 'OTHER') NOT NULL,
    feedback_text TEXT NOT NULL,
    severity ENUM('ERROR', 'WARNING', 'SUGGESTION') DEFAULT 'SUGGESTION',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (ai_grading_id) REFERENCES ai_grading_results(id) ON DELETE CASCADE,
    INDEX idx_ai_grading_id (ai_grading_id),
    INDEX idx_feedback_type (feedback_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- PART 9: AUDIT & LOGS (2 tables)
-- =====================================================

-- 9.1. Audit Logs
CREATE TABLE audit_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    action VARCHAR(100) NOT NULL COMMENT 'CREATE, UPDATE, DELETE, LOGIN, LOGOUT, etc.',
    entity_type VARCHAR(50) COMMENT 'users, exams, questions, etc.',
    entity_id BIGINT,
    old_value TEXT,
    new_value TEXT,
    ip_address VARCHAR(50),
    user_agent TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_user_id (user_id),
    INDEX idx_action (action),
    INDEX idx_entity_type (entity_type),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 9.2. Login History
CREATE TABLE login_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    login_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    logout_time TIMESTAMP,
    ip_address VARCHAR(50),
    device_info TEXT,
    status ENUM('SUCCESS', 'FAILED') DEFAULT 'SUCCESS',
    failure_reason VARCHAR(200),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_login_time (login_time),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- SAMPLE DATA FOR NEW TABLES
-- =====================================================

-- Insert Exam Skills
INSERT INTO exam_skills (code, name, description) VALUES
('N', 'Listening (Nghe)', 'Kỹ năng nghe hiểu tiếng Hàn'),
('R', 'Reading (Đọc)', 'Kỹ năng đọc hiểu tiếng Hàn'),
('W', 'Writing (Viết)', 'Kỹ năng viết tiếng Hàn'),
('S', 'Speaking (Nói)', 'Kỹ năng nói tiếng Hàn');

-- Insert Question Patterns (examples for Listening)
INSERT INTO question_patterns (skill_id, pattern_code, pattern_name, typical_question_count) VALUES
-- Listening patterns
(1, 'N1', 'Nghe hội thoại ngắn - chọn tranh phù hợp', 3),
(1, 'N2', 'Nghe câu hỏi - chọn đáp án phù hợp', 3),
(1, 'N3', 'Nghe hội thoại - hiểu nội dung chính', 2),
(1, 'N4', 'Nghe và chọn hành động tiếp theo', 2),
-- Reading patterns
(2, 'R1', 'Chọn từ phù hợp điền vào chỗ trống', 4),
(2, 'R2', 'Đọc đoạn văn ngắn - chọn nội dung đúng', 3),
(2, 'R3', 'Hiểu thông tin cụ thể trong bài đọc', 3),
-- Writing patterns
(3, 'W1', 'Viết câu theo mẫu cho sẵn', 2),
(3, 'W2', 'Viết đoạn văn ngắn 100-150 từ', 1),
-- Speaking patterns
(4, 'S1', 'Giới thiệu bản thân (30 giây)', 1),
(4, 'S2', 'Mô tả tình huống (1 phút)', 1);

-- Insert Payment Methods
INSERT INTO payment_methods (name, code) VALUES
('Chuyển khoản ngân hàng', 'BANK_TRANSFER'),
('Ví MoMo', 'MOMO'),
('VNPay', 'VNPAY'),
('Tiền mặt', 'CASH');

-- Insert Forum Categories
INSERT INTO forum_categories (name, description, display_order) VALUES
('Thông báo chung', 'Thông báo từ trung tâm', 1),
('Hỏi đáp học tập', 'Trao đổi về bài học và kiến thức', 2),
('Chia sẻ tài liệu', 'Chia sẻ tài liệu học tập hữu ích', 3),
('Góc văn hóa Hàn Quốc', 'Tìm hiểu về văn hóa, phong tục Hàn Quốc', 4);

-- Insert Sample Class
INSERT INTO classes (course_id, class_code, class_name, start_date, end_date, capacity, current_enrollment, status) VALUES
(1, 'TOPIK1-2025-01', 'Lớp TOPIK I - Kỳ 1/2025', '2025-01-15', '2025-03-15', 25, 0, 'PLANNED');

-- Insert Sample Class Schedules
INSERT INTO class_schedules (class_id, lesson_number, lesson_date, start_time, end_time, topic, room, status) VALUES
(1, 1, '2025-01-15', '18:00:00', '20:00:00', 'Bảng chữ cái Hangul - Nguyên âm cơ bản', 'A101', 'SCHEDULED'),
(1, 2, '2025-01-17', '18:00:00', '20:00:00', 'Bảng chữ cái Hangul - Phụ âm cơ bản', 'A101', 'SCHEDULED'),
(1, 3, '2025-01-20', '18:00:00', '20:00:00', 'Ngữ pháp cơ bản - Câu giới thiệu', 'A101', 'SCHEDULED');

-- =====================================================
-- END OF SCHEMA EXTENSION
-- =====================================================
-- Total Tables Added: 28
-- Total Tables in System: 10 (original) + 28 (new) = 38 tables
-- =====================================================
