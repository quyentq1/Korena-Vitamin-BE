-- phpMyAdmin SQL Dump
-- version 5.2.0
-- https://www.phpmyadmin.net/
--
-- Host: localhost:3306
-- Generation Time: Dec 06, 2025 at 04:42 PM
-- Server version: 8.0.30
-- PHP Version: 7.4.33

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `k_vitamin_center`
--

-- --------------------------------------------------------

--
-- Table structure for table `ai_feedback`
--

CREATE TABLE `ai_feedback` (
  `id` bigint NOT NULL,
  `ai_grading_id` bigint NOT NULL,
  `feedback_type` enum('GRAMMAR','VOCABULARY','STRUCTURE','CONTENT','PRONUNCIATION','OTHER') COLLATE utf8mb4_unicode_ci NOT NULL,
  `feedback_text` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `severity` enum('ERROR','WARNING','SUGGESTION') COLLATE utf8mb4_unicode_ci DEFAULT 'SUGGESTION',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `ai_grading_results`
--

CREATE TABLE `ai_grading_results` (
  `id` bigint NOT NULL,
  `student_answer_id` bigint NOT NULL,
  `ai_score` decimal(5,2) DEFAULT NULL,
  `ai_model` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'e.g., GPT-4, Claude, Gemini',
  `confidence_score` decimal(5,2) DEFAULT NULL,
  `graded_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `attendance`
--

CREATE TABLE `attendance` (
  `id` bigint NOT NULL,
  `schedule_id` bigint NOT NULL,
  `student_id` bigint NOT NULL,
  `status` enum('PRESENT','ABSENT','LATE','EXCUSED') COLLATE utf8mb4_unicode_ci DEFAULT 'PRESENT',
  `notes` text COLLATE utf8mb4_unicode_ci,
  `marked_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `audit_logs`
--

CREATE TABLE `audit_logs` (
  `id` bigint NOT NULL,
  `user_id` bigint DEFAULT NULL,
  `action` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'CREATE, UPDATE, DELETE, LOGIN, LOGOUT, etc.',
  `entity_type` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'users, exams, questions, etc.',
  `entity_id` bigint DEFAULT NULL,
  `old_value` text COLLATE utf8mb4_unicode_ci,
  `new_value` text COLLATE utf8mb4_unicode_ci,
  `ip_address` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `user_agent` text COLLATE utf8mb4_unicode_ci,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `chatbot_conversations`
--

CREATE TABLE `chatbot_conversations` (
  `id` bigint NOT NULL,
  `user_id` bigint DEFAULT NULL,
  `session_id` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `started_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `ended_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `chatbot_messages`
--

CREATE TABLE `chatbot_messages` (
  `id` bigint NOT NULL,
  `conversation_id` bigint NOT NULL,
  `sender_type` enum('USER','BOT') COLLATE utf8mb4_unicode_ci NOT NULL,
  `message` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `classes`
--

CREATE TABLE `classes` (
  `id` bigint NOT NULL,
  `course_id` bigint NOT NULL,
  `class_code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `class_name` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `start_date` date DEFAULT NULL,
  `end_date` date DEFAULT NULL,
  `capacity` int DEFAULT '30',
  `current_enrollment` int DEFAULT '0',
  `status` enum('PLANNED','ONGOING','COMPLETED','CANCELLED') COLLATE utf8mb4_unicode_ci DEFAULT 'PLANNED',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `classes`
--

INSERT INTO `classes` (`id`, `course_id`, `class_code`, `class_name`, `start_date`, `end_date`, `capacity`, `current_enrollment`, `status`, `created_at`, `updated_at`) VALUES
(1, 1, 'TOPIK1-2025-01', 'Lớp TOPIK I - Kỳ 1/2025', '2025-01-15', '2025-03-15', 25, 0, 'PLANNED', '2025-12-06 16:37:46', '2025-12-06 16:37:46');

-- --------------------------------------------------------

--
-- Table structure for table `class_schedules`
--

CREATE TABLE `class_schedules` (
  `id` bigint NOT NULL,
  `class_id` bigint NOT NULL,
  `lesson_number` int NOT NULL,
  `lesson_date` date NOT NULL,
  `start_time` time NOT NULL,
  `end_time` time NOT NULL,
  `topic` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `room` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` enum('SCHEDULED','COMPLETED','CANCELLED','RESCHEDULED') COLLATE utf8mb4_unicode_ci DEFAULT 'SCHEDULED',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `class_schedules`
--

INSERT INTO `class_schedules` (`id`, `class_id`, `lesson_number`, `lesson_date`, `start_time`, `end_time`, `topic`, `room`, `status`, `created_at`, `updated_at`) VALUES
(1, 1, 1, '2025-01-15', '18:00:00', '20:00:00', 'Bảng chữ cái Hangul - Nguyên âm cơ bản', 'A101', 'SCHEDULED', '2025-12-06 16:37:46', '2025-12-06 16:37:46'),
(2, 1, 2, '2025-01-17', '18:00:00', '20:00:00', 'Bảng chữ cái Hangul - Phụ âm cơ bản', 'A101', 'SCHEDULED', '2025-12-06 16:37:46', '2025-12-06 16:37:46'),
(3, 1, 3, '2025-01-20', '18:00:00', '20:00:00', 'Ngữ pháp cơ bản - Câu giới thiệu', 'A101', 'SCHEDULED', '2025-12-06 16:37:46', '2025-12-06 16:37:46');

-- --------------------------------------------------------

--
-- Table structure for table `class_students`
--

CREATE TABLE `class_students` (
  `id` bigint NOT NULL,
  `class_id` bigint NOT NULL,
  `student_id` bigint NOT NULL,
  `enrollment_date` date DEFAULT (curdate()),
  `status` enum('ACTIVE','DROPPED','COMPLETED') COLLATE utf8mb4_unicode_ci DEFAULT 'ACTIVE',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `class_teachers`
--

CREATE TABLE `class_teachers` (
  `id` bigint NOT NULL,
  `class_id` bigint NOT NULL,
  `teacher_id` bigint NOT NULL,
  `assigned_date` date DEFAULT (curdate()),
  `is_primary` tinyint(1) DEFAULT '0' COMMENT 'Primary teacher vs assistant teacher',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `courses`
--

CREATE TABLE `courses` (
  `id` bigint NOT NULL,
  `code` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `fee` decimal(10,2) NOT NULL,
  `capacity` int NOT NULL,
  `start_date` date DEFAULT NULL,
  `end_date` date DEFAULT NULL,
  `schedule` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `active` tinyint(1) DEFAULT '1',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `courses`
--

INSERT INTO `courses` (`id`, `code`, `name`, `description`, `fee`, `capacity`, `start_date`, `end_date`, `schedule`, `active`, `created_at`, `updated_at`) VALUES
(1, 'ENG101', 'English Foundation', 'Beginner English course covering basic grammar, vocabulary, and conversation', '2000000.00', 30, '2025-01-15', '2025-03-15', 'Monday, Wednesday, Friday 18:00-20:00', 1, '2025-12-06 16:37:18', '2025-12-06 16:37:18'),
(2, 'ENG201', 'English Intermediate', 'Intermediate English course for students with basic knowledge', '2500000.00', 25, '2025-01-20', '2025-04-20', 'Tuesday, Thursday 18:00-20:00', 1, '2025-12-06 16:37:18', '2025-12-06 16:37:18'),
(3, 'ENG301', 'English Advanced', 'Advanced English course focusing on business communication', '3000000.00', 20, '2025-02-01', '2025-05-01', 'Monday, Wednesday 19:00-21:00', 1, '2025-12-06 16:37:18', '2025-12-06 16:37:18');

-- --------------------------------------------------------

--
-- Table structure for table `course_registrations`
--

CREATE TABLE `course_registrations` (
  `id` bigint NOT NULL,
  `user_id` bigint DEFAULT NULL COMMENT 'NULL for guest registrations before account creation',
  `course_id` bigint NOT NULL,
  `registration_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `status` enum('PENDING','APPROVED','REJECTED','COMPLETED') COLLATE utf8mb4_unicode_ci DEFAULT 'PENDING',
  `notes` text COLLATE utf8mb4_unicode_ci,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `course_registrations`
--

INSERT INTO `course_registrations` (`id`, `user_id`, `course_id`, `registration_date`, `status`, `notes`, `created_at`, `updated_at`) VALUES
(1, 5, 1, '2025-12-06 16:37:18', 'APPROVED', 'Regular registration', '2025-12-06 16:37:18', '2025-12-06 16:37:18'),
(2, 6, 1, '2025-12-06 16:37:18', 'APPROVED', 'Regular registration', '2025-12-06 16:37:18', '2025-12-06 16:37:18'),
(3, 7, 2, '2025-12-06 16:37:18', 'PENDING', 'Waiting for payment confirmation', '2025-12-06 16:37:18', '2025-12-06 16:37:18'),
(4, NULL, 1, '2025-12-06 16:37:18', 'PENDING', 'Name: Nguyen Van G\nEmail: student04@example.com\nPhone: 0978901234\nAddress: 123 Main St, HCMC', '2025-12-06 16:37:18', '2025-12-06 16:37:18');

-- --------------------------------------------------------

--
-- Table structure for table `exams`
--

CREATE TABLE `exams` (
  `id` bigint NOT NULL,
  `course_id` bigint NOT NULL,
  `exam_type` enum('FREE_PAID','UNLIMITED','LESSON_QUIZ','CERTIFICATE') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'UNLIMITED',
  `certificate_type` enum('TOPIK','OPIC','EPS_TOPIK','OTHER') COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_by` bigint NOT NULL,
  `title` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `duration_minutes` int NOT NULL,
  `total_points` int NOT NULL,
  `available_from` timestamp NULL DEFAULT NULL,
  `available_to` timestamp NULL DEFAULT NULL,
  `published` tinyint(1) DEFAULT '0',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `exams`
--

INSERT INTO `exams` (`id`, `course_id`, `exam_type`, `certificate_type`, `created_by`, `title`, `description`, `duration_minutes`, `total_points`, `available_from`, `available_to`, `published`, `created_at`, `updated_at`) VALUES
(1, 1, 'UNLIMITED', NULL, 3, 'English Foundation - Mid-term Test', 'Mid-term examination covering grammar and vocabulary', 60, 20, '2025-01-20 01:00:00', '2025-01-25 16:59:59', 1, '2025-12-06 16:37:18', '2025-12-06 16:37:18'),
(2, 1, 'UNLIMITED', NULL, 3, 'English Foundation - Final Test', 'Final examination covering all topics', 90, 40, '2025-03-10 01:00:00', '2025-03-15 16:59:59', 0, '2025-12-06 16:37:18', '2025-12-06 16:37:18');

-- --------------------------------------------------------

--
-- Table structure for table `exam_attempts`
--

CREATE TABLE `exam_attempts` (
  `id` bigint NOT NULL,
  `exam_id` bigint NOT NULL,
  `student_id` bigint NOT NULL,
  `start_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `end_time` timestamp NULL DEFAULT NULL,
  `submit_time` timestamp NULL DEFAULT NULL,
  `auto_score` decimal(5,2) DEFAULT '0.00',
  `manual_score` decimal(5,2) DEFAULT '0.00',
  `total_score` decimal(5,2) DEFAULT '0.00',
  `status` enum('IN_PROGRESS','SUBMITTED','GRADED','PENDING_MANUAL_GRADE') COLLATE utf8mb4_unicode_ci DEFAULT 'IN_PROGRESS',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `exam_attempts`
--

INSERT INTO `exam_attempts` (`id`, `exam_id`, `student_id`, `start_time`, `end_time`, `submit_time`, `auto_score`, `manual_score`, `total_score`, `status`, `created_at`, `updated_at`) VALUES
(1, 1, 5, '2025-01-21 03:00:00', '2025-01-21 04:00:00', '2025-01-21 03:45:00', '12.00', '4.00', '16.00', 'GRADED', '2025-12-06 16:37:18', '2025-12-06 16:37:18');

-- --------------------------------------------------------

--
-- Table structure for table `exam_configs`
--

CREATE TABLE `exam_configs` (
  `id` bigint NOT NULL,
  `exam_id` bigint NOT NULL,
  `total_questions` int NOT NULL,
  `allow_duplicate_questions` tinyint(1) DEFAULT '1' COMMENT 'Type 2 allows duplicates, Type 1 does not',
  `randomize_questions` tinyint(1) DEFAULT '1',
  `randomize_options` tinyint(1) DEFAULT '1',
  `show_result_immediately` tinyint(1) DEFAULT '0',
  `enable_keyboard_lock` tinyint(1) DEFAULT '0' COMMENT 'Anti-cheating: lock keyboard during exam',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `exam_pattern_distribution`
--

CREATE TABLE `exam_pattern_distribution` (
  `id` bigint NOT NULL,
  `exam_id` bigint NOT NULL,
  `pattern_id` bigint NOT NULL,
  `question_count` int NOT NULL COMMENT 'Number of questions for this pattern',
  `points_per_question` int DEFAULT '1',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `exam_questions`
--

CREATE TABLE `exam_questions` (
  `id` bigint NOT NULL,
  `exam_id` bigint NOT NULL,
  `question_id` bigint NOT NULL,
  `question_order` int NOT NULL,
  `points` int NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `exam_questions`
--

INSERT INTO `exam_questions` (`id`, `exam_id`, `question_id`, `question_order`, `points`) VALUES
(1, 1, 1, 1, 2),
(2, 1, 2, 2, 2),
(3, 1, 3, 3, 2),
(4, 1, 4, 4, 3),
(5, 1, 5, 5, 2),
(6, 1, 6, 6, 2),
(7, 1, 7, 7, 2),
(8, 1, 8, 8, 5);

-- --------------------------------------------------------

--
-- Table structure for table `exam_skills`
--

CREATE TABLE `exam_skills` (
  `id` bigint NOT NULL,
  `code` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'N=Listening, R=Reading, W=Writing, S=Speaking',
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `exam_skills`
--

INSERT INTO `exam_skills` (`id`, `code`, `name`, `description`, `created_at`) VALUES
(1, 'N', 'Listening (Nghe)', 'Kỹ năng nghe hiểu tiếng Hàn', '2025-12-06 16:37:46'),
(2, 'R', 'Reading (Đọc)', 'Kỹ năng đọc hiểu tiếng Hàn', '2025-12-06 16:37:46'),
(3, 'W', 'Writing (Viết)', 'Kỹ năng viết tiếng Hàn', '2025-12-06 16:37:46'),
(4, 'S', 'Speaking (Nói)', 'Kỹ năng nói tiếng Hàn', '2025-12-06 16:37:46');

-- --------------------------------------------------------

--
-- Table structure for table `exam_variants`
--

CREATE TABLE `exam_variants` (
  `id` bigint NOT NULL,
  `exam_id` bigint NOT NULL,
  `variant_code` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'A, B, C, ..., T (20 variants)',
  `variant_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT '1',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `forum_categories`
--

CREATE TABLE `forum_categories` (
  `id` bigint NOT NULL,
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `display_order` int DEFAULT '0',
  `active` tinyint(1) DEFAULT '1',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `forum_categories`
--

INSERT INTO `forum_categories` (`id`, `name`, `description`, `display_order`, `active`, `created_at`, `updated_at`) VALUES
(1, 'Thông báo chung', 'Thông báo từ trung tâm', 1, 1, '2025-12-06 16:37:46', '2025-12-06 16:37:46'),
(2, 'Hỏi đáp học tập', 'Trao đổi về bài học và kiến thức', 2, 1, '2025-12-06 16:37:46', '2025-12-06 16:37:46'),
(3, 'Chia sẻ tài liệu', 'Chia sẻ tài liệu học tập hữu ích', 3, 1, '2025-12-06 16:37:46', '2025-12-06 16:37:46'),
(4, 'Góc văn hóa Hàn Quốc', 'Tìm hiểu về văn hóa, phong tục Hàn Quốc', 4, 1, '2025-12-06 16:37:46', '2025-12-06 16:37:46');

-- --------------------------------------------------------

--
-- Table structure for table `forum_comments`
--

CREATE TABLE `forum_comments` (
  `id` bigint NOT NULL,
  `post_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `content` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `forum_likes`
--

CREATE TABLE `forum_likes` (
  `id` bigint NOT NULL,
  `post_id` bigint DEFAULT NULL,
  `comment_id` bigint DEFAULT NULL,
  `user_id` bigint NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP
) ;

-- --------------------------------------------------------

--
-- Table structure for table `forum_posts`
--

CREATE TABLE `forum_posts` (
  `id` bigint NOT NULL,
  `category_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `title` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `content` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `view_count` int DEFAULT '0',
  `is_pinned` tinyint(1) DEFAULT '0',
  `is_locked` tinyint(1) DEFAULT '0',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `invoices`
--

CREATE TABLE `invoices` (
  `id` bigint NOT NULL,
  `payment_id` bigint NOT NULL,
  `invoice_number` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `issue_date` date NOT NULL,
  `due_date` date DEFAULT NULL,
  `total_amount` decimal(10,2) NOT NULL,
  `notes` text COLLATE utf8mb4_unicode_ci,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `learning_reports`
--

CREATE TABLE `learning_reports` (
  `id` bigint NOT NULL,
  `class_id` bigint NOT NULL,
  `student_id` bigint NOT NULL,
  `teacher_id` bigint NOT NULL,
  `report_date` date NOT NULL,
  `attendance_rate` decimal(5,2) DEFAULT NULL,
  `progress` text COLLATE utf8mb4_unicode_ci,
  `strengths` text COLLATE utf8mb4_unicode_ci,
  `weaknesses` text COLLATE utf8mb4_unicode_ci,
  `recommendations` text COLLATE utf8mb4_unicode_ci,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `lesson_quizzes`
--

CREATE TABLE `lesson_quizzes` (
  `id` bigint NOT NULL,
  `class_id` bigint NOT NULL,
  `lesson_number` int NOT NULL,
  `title` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `created_by` bigint NOT NULL,
  `duration_minutes` int DEFAULT '15',
  `total_points` int DEFAULT '10',
  `quiz_date` date DEFAULT NULL,
  `active` tinyint(1) DEFAULT '1',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `login_history`
--

CREATE TABLE `login_history` (
  `id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `login_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `logout_time` timestamp NULL DEFAULT NULL,
  `ip_address` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `device_info` text COLLATE utf8mb4_unicode_ci,
  `status` enum('SUCCESS','FAILED') COLLATE utf8mb4_unicode_ci DEFAULT 'SUCCESS',
  `failure_reason` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `manual_grades`
--

CREATE TABLE `manual_grades` (
  `id` bigint NOT NULL,
  `attempt_id` bigint NOT NULL,
  `exam_question_id` bigint NOT NULL,
  `graded_by` bigint NOT NULL,
  `score` decimal(5,2) NOT NULL,
  `feedback` text COLLATE utf8mb4_unicode_ci,
  `graded_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `manual_grades`
--

INSERT INTO `manual_grades` (`id`, `attempt_id`, `exam_question_id`, `graded_by`, `score`, `feedback`, `graded_at`) VALUES
(1, 1, 8, 3, '4.00', 'Good essay structure and vocabulary. Minor grammar errors. Keep up the good work!', '2025-12-06 16:37:18');

-- --------------------------------------------------------

--
-- Table structure for table `notifications`
--

CREATE TABLE `notifications` (
  `id` bigint NOT NULL,
  `title` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `message` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `notification_type` enum('SYSTEM','COURSE','EXAM','PAYMENT','SCHEDULE','OTHER') COLLATE utf8mb4_unicode_ci DEFAULT 'SYSTEM',
  `sender_id` bigint DEFAULT NULL COMMENT 'NULL = system notification',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `notification_recipients`
--

CREATE TABLE `notification_recipients` (
  `id` bigint NOT NULL,
  `notification_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `is_read` tinyint(1) DEFAULT '0',
  `read_at` timestamp NULL DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `payments`
--

CREATE TABLE `payments` (
  `id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `payment_type` enum('COURSE_FEE','TEST_UNLOCK','OTHER') COLLATE utf8mb4_unicode_ci NOT NULL,
  `related_id` bigint DEFAULT NULL COMMENT 'course_id or exam_id depending on payment_type',
  `amount` decimal(10,2) NOT NULL,
  `payment_method_id` bigint DEFAULT NULL,
  `status` enum('PENDING','COMPLETED','FAILED','REFUNDED') COLLATE utf8mb4_unicode_ci DEFAULT 'PENDING',
  `transaction_id` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `paid_at` timestamp NULL DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `payment_methods`
--

CREATE TABLE `payment_methods` (
  `id` bigint NOT NULL,
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `is_active` tinyint(1) DEFAULT '1',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `payment_methods`
--

INSERT INTO `payment_methods` (`id`, `name`, `code`, `is_active`, `created_at`, `updated_at`) VALUES
(1, 'Chuyển khoản ngân hàng', 'BANK_TRANSFER', 1, '2025-12-06 16:37:46', '2025-12-06 16:37:46'),
(2, 'Ví MoMo', 'MOMO', 1, '2025-12-06 16:37:46', '2025-12-06 16:37:46'),
(3, 'VNPay', 'VNPAY', 1, '2025-12-06 16:37:46', '2025-12-06 16:37:46'),
(4, 'Tiền mặt', 'CASH', 1, '2025-12-06 16:37:46', '2025-12-06 16:37:46');

-- --------------------------------------------------------

--
-- Table structure for table `questions`
--

CREATE TABLE `questions` (
  `id` bigint NOT NULL,
  `category_id` bigint NOT NULL,
  `pattern_id` bigint DEFAULT NULL,
  `created_by` bigint NOT NULL,
  `question_type` enum('MULTIPLE_CHOICE','SHORT_ANSWER','ESSAY','LISTENING','SPEAKING') COLLATE utf8mb4_unicode_ci NOT NULL,
  `question_text` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `question_media_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `correct_answer` text COLLATE utf8mb4_unicode_ci,
  `points` int DEFAULT '1',
  `difficulty` enum('EASY','MEDIUM','HARD') COLLATE utf8mb4_unicode_ci DEFAULT 'MEDIUM',
  `active` tinyint(1) DEFAULT '1',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `questions`
--

INSERT INTO `questions` (`id`, `category_id`, `pattern_id`, `created_by`, `question_type`, `question_text`, `question_media_url`, `correct_answer`, `points`, `difficulty`, `active`, `created_at`, `updated_at`) VALUES
(1, 1, NULL, 3, 'MULTIPLE_CHOICE', 'Choose the correct form: She _____ to school every day.', NULL, NULL, 1, 'EASY', 1, '2025-12-06 16:37:18', '2025-12-06 16:37:18'),
(2, 1, NULL, 3, 'MULTIPLE_CHOICE', 'What is the past tense of \"go\"?', NULL, NULL, 1, 'EASY', 1, '2025-12-06 16:37:18', '2025-12-06 16:37:18'),
(3, 1, NULL, 3, 'SHORT_ANSWER', 'Fill in the blank: I _____ (to be) a student.', NULL, 'am', 1, 'EASY', 1, '2025-12-06 16:37:18', '2025-12-06 16:37:18'),
(4, 1, NULL, 3, 'MULTIPLE_CHOICE', 'Which sentence is correct?', NULL, NULL, 2, 'MEDIUM', 1, '2025-12-06 16:37:18', '2025-12-06 16:37:18'),
(5, 2, NULL, 3, 'MULTIPLE_CHOICE', 'What does \"beautiful\" mean?', NULL, NULL, 1, 'EASY', 1, '2025-12-06 16:37:18', '2025-12-06 16:37:18'),
(6, 2, NULL, 3, 'MULTIPLE_CHOICE', 'Choose the synonym of \"happy\":', NULL, NULL, 1, 'EASY', 1, '2025-12-06 16:37:18', '2025-12-06 16:37:18'),
(7, 2, NULL, 3, 'SHORT_ANSWER', 'What is the opposite of \"hot\"?', NULL, 'cold', 1, 'EASY', 1, '2025-12-06 16:37:18', '2025-12-06 16:37:18'),
(8, 3, NULL, 3, 'ESSAY', 'Read the passage and explain the main idea in your own words.', NULL, NULL, 5, 'MEDIUM', 1, '2025-12-06 16:37:18', '2025-12-06 16:37:18'),
(9, 3, NULL, 3, 'MULTIPLE_CHOICE', 'According to the text, what time does the store open?', NULL, NULL, 2, 'MEDIUM', 1, '2025-12-06 16:37:18', '2025-12-06 16:37:18'),
(10, 4, NULL, 3, 'LISTENING', 'Listen to the audio and answer: What is the speaker talking about?', NULL, NULL, 3, 'MEDIUM', 1, '2025-12-06 16:37:18', '2025-12-06 16:37:18'),
(11, 5, NULL, 3, 'SPEAKING', 'Introduce yourself in English (30 seconds).', NULL, NULL, 5, 'MEDIUM', 1, '2025-12-06 16:37:18', '2025-12-06 16:37:18'),
(12, 6, NULL, 3, 'ESSAY', 'Write a short paragraph about your favorite hobby (100 words).', NULL, NULL, 5, 'MEDIUM', 1, '2025-12-06 16:37:18', '2025-12-06 16:37:18');

-- --------------------------------------------------------

--
-- Table structure for table `question_approvals`
--

CREATE TABLE `question_approvals` (
  `id` bigint NOT NULL,
  `question_id` bigint NOT NULL,
  `submitted_by` bigint NOT NULL COMMENT 'Teacher who created question',
  `reviewed_by` bigint DEFAULT NULL COMMENT 'Education Manager',
  `status` enum('PENDING','APPROVED','REJECTED','REVISION_NEEDED') COLLATE utf8mb4_unicode_ci DEFAULT 'PENDING',
  `feedback` text COLLATE utf8mb4_unicode_ci,
  `duplicate_check_passed` tinyint(1) DEFAULT NULL COMMENT 'AI/Tool check for duplicate content',
  `submitted_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `reviewed_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `question_categories`
--

CREATE TABLE `question_categories` (
  `id` bigint NOT NULL,
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `question_categories`
--

INSERT INTO `question_categories` (`id`, `name`, `description`, `created_at`, `updated_at`) VALUES
(1, 'Grammar', 'Grammar questions covering tenses, sentence structure, etc.', '2025-12-06 16:37:18', '2025-12-06 16:37:18'),
(2, 'Vocabulary', 'Vocabulary and word usage questions', '2025-12-06 16:37:18', '2025-12-06 16:37:18'),
(3, 'Reading', 'Reading comprehension questions', '2025-12-06 16:37:18', '2025-12-06 16:37:18'),
(4, 'Listening', 'Listening comprehension questions', '2025-12-06 16:37:18', '2025-12-06 16:37:18'),
(5, 'Speaking', 'Speaking and pronunciation questions', '2025-12-06 16:37:18', '2025-12-06 16:37:18'),
(6, 'Writing', 'Writing and composition questions', '2025-12-06 16:37:18', '2025-12-06 16:37:18');

-- --------------------------------------------------------

--
-- Table structure for table `question_options`
--

CREATE TABLE `question_options` (
  `id` bigint NOT NULL,
  `question_id` bigint NOT NULL,
  `option_text` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `is_correct` tinyint(1) DEFAULT '0',
  `option_order` int NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `question_options`
--

INSERT INTO `question_options` (`id`, `question_id`, `option_text`, `is_correct`, `option_order`) VALUES
(1, 1, 'go', 0, 1),
(2, 1, 'goes', 1, 2),
(3, 1, 'going', 0, 3),
(4, 1, 'gone', 0, 4),
(5, 2, 'goed', 0, 1),
(6, 2, 'went', 1, 2),
(7, 2, 'gone', 0, 3),
(8, 2, 'going', 0, 4),
(9, 4, 'He don\'t like coffee', 0, 1),
(10, 4, 'He doesn\'t like coffee', 1, 2),
(11, 4, 'He doesn\'t likes coffee', 0, 3),
(12, 4, 'He not like coffee', 0, 4),
(13, 5, 'Ugly', 0, 1),
(14, 5, 'Pretty and attractive', 1, 2),
(15, 5, 'Sad', 0, 3),
(16, 5, 'Angry', 0, 4),
(17, 6, 'Sad', 0, 1),
(18, 6, 'Joyful', 1, 2),
(19, 6, 'Angry', 0, 3),
(20, 6, 'Tired', 0, 4),
(21, 9, '8:00 AM', 0, 1),
(22, 9, '9:00 AM', 1, 2),
(23, 9, '10:00 AM', 0, 3),
(24, 9, '11:00 AM', 0, 4);

-- --------------------------------------------------------

--
-- Table structure for table `question_patterns`
--

CREATE TABLE `question_patterns` (
  `id` bigint NOT NULL,
  `skill_id` bigint NOT NULL,
  `pattern_code` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'N1, N2, ..., N8, R1, ..., R8, etc.',
  `pattern_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `typical_question_count` int DEFAULT '0' COMMENT 'Typical number of questions for this pattern',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `question_patterns`
--

INSERT INTO `question_patterns` (`id`, `skill_id`, `pattern_code`, `pattern_name`, `description`, `typical_question_count`, `created_at`) VALUES
(1, 1, 'N1', 'Nghe hội thoại ngắn - chọn tranh phù hợp', NULL, 3, '2025-12-06 16:37:46'),
(2, 1, 'N2', 'Nghe câu hỏi - chọn đáp án phù hợp', NULL, 3, '2025-12-06 16:37:46'),
(3, 1, 'N3', 'Nghe hội thoại - hiểu nội dung chính', NULL, 2, '2025-12-06 16:37:46'),
(4, 1, 'N4', 'Nghe và chọn hành động tiếp theo', NULL, 2, '2025-12-06 16:37:46'),
(5, 2, 'R1', 'Chọn từ phù hợp điền vào chỗ trống', NULL, 4, '2025-12-06 16:37:46'),
(6, 2, 'R2', 'Đọc đoạn văn ngắn - chọn nội dung đúng', NULL, 3, '2025-12-06 16:37:46'),
(7, 2, 'R3', 'Hiểu thông tin cụ thể trong bài đọc', NULL, 3, '2025-12-06 16:37:46'),
(8, 3, 'W1', 'Viết câu theo mẫu cho sẵn', NULL, 2, '2025-12-06 16:37:46'),
(9, 3, 'W2', 'Viết đoạn văn ngắn 100-150 từ', NULL, 1, '2025-12-06 16:37:46'),
(10, 4, 'S1', 'Giới thiệu bản thân (30 giây)', NULL, 1, '2025-12-06 16:37:46'),
(11, 4, 'S2', 'Mô tả tình huống (1 phút)', NULL, 1, '2025-12-06 16:37:46');

-- --------------------------------------------------------

--
-- Table structure for table `question_usage_history`
--

CREATE TABLE `question_usage_history` (
  `id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `question_id` bigint NOT NULL,
  `used_in_exam_id` bigint NOT NULL,
  `used_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `registration_forms`
--

CREATE TABLE `registration_forms` (
  `id` bigint NOT NULL,
  `form_number` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `student_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `email` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `phone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `address` text COLLATE utf8mb4_unicode_ci,
  `course_code` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `form_image_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ocr_text` text COLLATE utf8mb4_unicode_ci,
  `status` enum('PENDING','PROCESSED','APPROVED','REJECTED') COLLATE utf8mb4_unicode_ci DEFAULT 'PENDING',
  `processed_by` bigint DEFAULT NULL,
  `scanned_at` timestamp NULL DEFAULT NULL,
  `processed_at` timestamp NULL DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `registration_forms`
--

INSERT INTO `registration_forms` (`id`, `form_number`, `student_name`, `email`, `phone`, `address`, `course_code`, `form_image_url`, `ocr_text`, `status`, `processed_by`, `scanned_at`, `processed_at`, `created_at`, `updated_at`) VALUES
(1, 'FORM-2025-001', 'Tran Van H', 'student05@example.com', '0989012345', '456 Second St, Hanoi', 'ENG101', '/uploads/ocr/form001.jpg', 'Name: Tran Van H\nEmail: student05@example.com\nPhone: 0989012345\nAddress: 456 Second St, Hanoi\nCourse: ENG101', 'PENDING', NULL, '2025-12-06 16:37:18', NULL, '2025-12-06 16:37:18', '2025-12-06 16:37:18');

-- --------------------------------------------------------

--
-- Table structure for table `schedule_change_requests`
--

CREATE TABLE `schedule_change_requests` (
  `id` bigint NOT NULL,
  `schedule_id` bigint NOT NULL,
  `requested_by` bigint NOT NULL COMMENT 'Teacher requesting change',
  `request_type` enum('RESCHEDULE','CANCEL','ROOM_CHANGE') COLLATE utf8mb4_unicode_ci NOT NULL,
  `reason` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `proposed_date` date DEFAULT NULL,
  `proposed_time` time DEFAULT NULL,
  `proposed_room` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` enum('PENDING','APPROVED','REJECTED') COLLATE utf8mb4_unicode_ci DEFAULT 'PENDING',
  `reviewed_by` bigint DEFAULT NULL COMMENT 'Education Manager',
  `reviewed_at` timestamp NULL DEFAULT NULL,
  `review_notes` text COLLATE utf8mb4_unicode_ci,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `student_answers`
--

CREATE TABLE `student_answers` (
  `id` bigint NOT NULL,
  `attempt_id` bigint NOT NULL,
  `exam_question_id` bigint NOT NULL,
  `answer_text` text COLLATE utf8mb4_unicode_ci,
  `answer_file_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_correct` tinyint(1) DEFAULT NULL,
  `score` decimal(5,2) DEFAULT '0.00',
  `answered_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `student_answers`
--

INSERT INTO `student_answers` (`id`, `attempt_id`, `exam_question_id`, `answer_text`, `answer_file_url`, `is_correct`, `score`, `answered_at`) VALUES
(1, 1, 1, '2', NULL, 1, '2.00', '2025-12-06 16:37:18'),
(2, 1, 2, '2', NULL, 1, '2.00', '2025-12-06 16:37:18'),
(3, 1, 3, 'am', NULL, 1, '2.00', '2025-12-06 16:37:18'),
(4, 1, 4, '2', NULL, 1, '3.00', '2025-12-06 16:37:18'),
(5, 1, 5, '2', NULL, 1, '2.00', '2025-12-06 16:37:18'),
(6, 1, 6, '1', NULL, 0, '0.00', '2025-12-06 16:37:18'),
(7, 1, 7, 'cold', NULL, 1, '1.00', '2025-12-06 16:37:18'),
(8, 1, 8, 'My favorite hobby is reading books. I love reading because it helps me relax and learn new things...', NULL, NULL, '0.00', '2025-12-06 16:37:18');

-- --------------------------------------------------------

--
-- Table structure for table `test_access_history`
--

CREATE TABLE `test_access_history` (
  `id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `exam_id` bigint NOT NULL,
  `access_type` enum('FREE','PAID') COLLATE utf8mb4_unicode_ci DEFAULT 'FREE',
  `payment_amount` decimal(10,2) DEFAULT '0.00',
  `accessed_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `id` bigint NOT NULL,
  `username` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `password` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `full_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `email` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `phone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `role` enum('ADMIN','STAFF','TEACHER','STUDENT','GUEST','LEARNER','EDUCATION_MANAGER') COLLATE utf8mb4_unicode_ci NOT NULL,
  `active` tinyint(1) DEFAULT '1',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `free_test_count` int DEFAULT '0' COMMENT 'Number of free tests used (max 5)',
  `payment_tier` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'Premium tier: 100K, 200K, etc.',
  `is_premium` tinyint(1) DEFAULT '0' COMMENT 'Has paid for premium access'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `username`, `password`, `full_name`, `email`, `phone`, `role`, `active`, `created_at`, `updated_at`, `free_test_count`, `payment_tier`, `is_premium`) VALUES
(1, 'admin', '$2a$10$7l8qyBXQcBE/u9V3hW8H9.bfGXGBKO5vLqZ7ZNPJ8x8zKqYxqHfKy', 'System Administrator', 'admin@trainingcenter.com', '0123456789', 'ADMIN', 1, '2025-12-06 16:37:18', '2025-12-06 16:37:18', 0, NULL, 0),
(2, 'staff01', '$2a$10$7l8qyBXQcBE/u9V3hW8H9.bfGXGBKO5vLqZ7ZNPJ8x8zKqYxqHfKy', 'Nguyen Van A', 'staff01@trainingcenter.com', '0912345678', 'STAFF', 1, '2025-12-06 16:37:18', '2025-12-06 16:37:18', 0, NULL, 0),
(3, 'teacher01', '$2a$10$7l8qyBXQcBE/u9V3hW8H9.bfGXGBKO5vLqZ7ZNPJ8x8zKqYxqHfKy', 'Tran Thi B', 'teacher01@trainingcenter.com', '0923456789', 'TEACHER', 1, '2025-12-06 16:37:18', '2025-12-06 16:37:18', 0, NULL, 0),
(4, 'teacher02', '$2a$10$7l8qyBXQcBE/u9V3hW8H9.bfGXGBKO5vLqZ7ZNPJ8x8zKqYxqHfKy', 'Le Van C', 'teacher02@trainingcenter.com', '0934567890', 'TEACHER', 1, '2025-12-06 16:37:18', '2025-12-06 16:37:18', 0, NULL, 0),
(5, 'student01', '$2a$10$7l8qyBXQcBE/u9V3hW8H9.bfGXGBKO5vLqZ7ZNPJ8x8zKqYxqHfKy', 'Pham Thi D', 'student01@trainingcenter.com', '0945678901', 'STUDENT', 1, '2025-12-06 16:37:18', '2025-12-06 16:37:18', 0, NULL, 0),
(6, 'student02', '$2a$10$7l8qyBXQcBE/u9V3hW8H9.bfGXGBKO5vLqZ7ZNPJ8x8zKqYxqHfKy', 'Hoang Van E', 'student02@trainingcenter.com', '0956789012', 'STUDENT', 1, '2025-12-06 16:37:18', '2025-12-06 16:37:18', 0, NULL, 0),
(7, 'student03', '$2a$10$7l8qyBXQcBE/u9V3hW8H9.bfGXGBKO5vLqZ7ZNPJ8x8zKqYxqHfKy', 'Vo Thi F', 'student03@trainingcenter.com', '0967890123', 'STUDENT', 1, '2025-12-06 16:37:18', '2025-12-06 16:37:18', 0, NULL, 0);

-- --------------------------------------------------------

--
-- Table structure for table `variant_questions`
--

CREATE TABLE `variant_questions` (
  `id` bigint NOT NULL,
  `variant_id` bigint NOT NULL,
  `question_id` bigint NOT NULL,
  `question_order` int NOT NULL,
  `points` int DEFAULT '1',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `ai_feedback`
--
ALTER TABLE `ai_feedback`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_ai_grading_id` (`ai_grading_id`),
  ADD KEY `idx_feedback_type` (`feedback_type`);

--
-- Indexes for table `ai_grading_results`
--
ALTER TABLE `ai_grading_results`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_student_answer_id` (`student_answer_id`);

--
-- Indexes for table `attendance`
--
ALTER TABLE `attendance`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `unique_attendance` (`schedule_id`,`student_id`),
  ADD KEY `idx_schedule_id` (`schedule_id`),
  ADD KEY `idx_student_id` (`student_id`),
  ADD KEY `idx_status` (`status`);

--
-- Indexes for table `audit_logs`
--
ALTER TABLE `audit_logs`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_user_id` (`user_id`),
  ADD KEY `idx_action` (`action`),
  ADD KEY `idx_entity_type` (`entity_type`),
  ADD KEY `idx_created_at` (`created_at`);

--
-- Indexes for table `chatbot_conversations`
--
ALTER TABLE `chatbot_conversations`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_session_id` (`session_id`),
  ADD KEY `idx_user_id` (`user_id`);

--
-- Indexes for table `chatbot_messages`
--
ALTER TABLE `chatbot_messages`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_conversation_id` (`conversation_id`);

--
-- Indexes for table `classes`
--
ALTER TABLE `classes`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `class_code` (`class_code`),
  ADD KEY `idx_course_id` (`course_id`),
  ADD KEY `idx_class_code` (`class_code`),
  ADD KEY `idx_status` (`status`);

--
-- Indexes for table `class_schedules`
--
ALTER TABLE `class_schedules`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_class_id` (`class_id`),
  ADD KEY `idx_lesson_date` (`lesson_date`),
  ADD KEY `idx_status` (`status`);

--
-- Indexes for table `class_students`
--
ALTER TABLE `class_students`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `unique_class_student` (`class_id`,`student_id`),
  ADD KEY `idx_class_id` (`class_id`),
  ADD KEY `idx_student_id` (`student_id`),
  ADD KEY `idx_status` (`status`);

--
-- Indexes for table `class_teachers`
--
ALTER TABLE `class_teachers`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_class_id` (`class_id`),
  ADD KEY `idx_teacher_id` (`teacher_id`);

--
-- Indexes for table `courses`
--
ALTER TABLE `courses`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `code` (`code`),
  ADD KEY `idx_code` (`code`),
  ADD KEY `idx_active` (`active`);

--
-- Indexes for table `course_registrations`
--
ALTER TABLE `course_registrations`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_user_id` (`user_id`),
  ADD KEY `idx_course_id` (`course_id`),
  ADD KEY `idx_status` (`status`);

--
-- Indexes for table `exams`
--
ALTER TABLE `exams`
  ADD PRIMARY KEY (`id`),
  ADD KEY `created_by` (`created_by`),
  ADD KEY `idx_course_id` (`course_id`),
  ADD KEY `idx_published` (`published`),
  ADD KEY `idx_exam_type` (`exam_type`),
  ADD KEY `idx_certificate_type` (`certificate_type`);

--
-- Indexes for table `exam_attempts`
--
ALTER TABLE `exam_attempts`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_exam_id` (`exam_id`),
  ADD KEY `idx_student_id` (`student_id`),
  ADD KEY `idx_status` (`status`);

--
-- Indexes for table `exam_configs`
--
ALTER TABLE `exam_configs`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_exam_id` (`exam_id`);

--
-- Indexes for table `exam_pattern_distribution`
--
ALTER TABLE `exam_pattern_distribution`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `unique_exam_pattern` (`exam_id`,`pattern_id`),
  ADD KEY `idx_exam_id` (`exam_id`),
  ADD KEY `idx_pattern_id` (`pattern_id`);

--
-- Indexes for table `exam_questions`
--
ALTER TABLE `exam_questions`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_exam_id` (`exam_id`),
  ADD KEY `idx_question_id` (`question_id`);

--
-- Indexes for table `exam_skills`
--
ALTER TABLE `exam_skills`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `code` (`code`),
  ADD KEY `idx_code` (`code`);

--
-- Indexes for table `exam_variants`
--
ALTER TABLE `exam_variants`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `unique_variant` (`exam_id`,`variant_code`),
  ADD KEY `idx_exam_id` (`exam_id`),
  ADD KEY `idx_variant_code` (`variant_code`);

--
-- Indexes for table `forum_categories`
--
ALTER TABLE `forum_categories`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_display_order` (`display_order`),
  ADD KEY `idx_active` (`active`);

--
-- Indexes for table `forum_comments`
--
ALTER TABLE `forum_comments`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_post_id` (`post_id`),
  ADD KEY `idx_user_id` (`user_id`);

--
-- Indexes for table `forum_likes`
--
ALTER TABLE `forum_likes`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `unique_user_like` (`user_id`,`post_id`,`comment_id`),
  ADD KEY `idx_post_id` (`post_id`),
  ADD KEY `idx_comment_id` (`comment_id`),
  ADD KEY `idx_user_id` (`user_id`);

--
-- Indexes for table `forum_posts`
--
ALTER TABLE `forum_posts`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_category_id` (`category_id`),
  ADD KEY `idx_user_id` (`user_id`),
  ADD KEY `idx_created_at` (`created_at`);

--
-- Indexes for table `invoices`
--
ALTER TABLE `invoices`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `invoice_number` (`invoice_number`),
  ADD KEY `idx_payment_id` (`payment_id`),
  ADD KEY `idx_invoice_number` (`invoice_number`);

--
-- Indexes for table `learning_reports`
--
ALTER TABLE `learning_reports`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_class_id` (`class_id`),
  ADD KEY `idx_student_id` (`student_id`),
  ADD KEY `idx_teacher_id` (`teacher_id`),
  ADD KEY `idx_report_date` (`report_date`);

--
-- Indexes for table `lesson_quizzes`
--
ALTER TABLE `lesson_quizzes`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_class_id` (`class_id`),
  ADD KEY `idx_lesson_number` (`lesson_number`),
  ADD KEY `idx_created_by` (`created_by`);

--
-- Indexes for table `login_history`
--
ALTER TABLE `login_history`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_user_id` (`user_id`),
  ADD KEY `idx_login_time` (`login_time`),
  ADD KEY `idx_status` (`status`);

--
-- Indexes for table `manual_grades`
--
ALTER TABLE `manual_grades`
  ADD PRIMARY KEY (`id`),
  ADD KEY `exam_question_id` (`exam_question_id`),
  ADD KEY `idx_attempt_id` (`attempt_id`),
  ADD KEY `idx_graded_by` (`graded_by`);

--
-- Indexes for table `notifications`
--
ALTER TABLE `notifications`
  ADD PRIMARY KEY (`id`),
  ADD KEY `sender_id` (`sender_id`),
  ADD KEY `idx_notification_type` (`notification_type`),
  ADD KEY `idx_created_at` (`created_at`);

--
-- Indexes for table `notification_recipients`
--
ALTER TABLE `notification_recipients`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `unique_notification_user` (`notification_id`,`user_id`),
  ADD KEY `idx_notification_id` (`notification_id`),
  ADD KEY `idx_user_id` (`user_id`),
  ADD KEY `idx_is_read` (`is_read`);

--
-- Indexes for table `payments`
--
ALTER TABLE `payments`
  ADD PRIMARY KEY (`id`),
  ADD KEY `payment_method_id` (`payment_method_id`),
  ADD KEY `idx_user_id` (`user_id`),
  ADD KEY `idx_status` (`status`),
  ADD KEY `idx_payment_type` (`payment_type`),
  ADD KEY `idx_transaction_id` (`transaction_id`);

--
-- Indexes for table `payment_methods`
--
ALTER TABLE `payment_methods`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `code` (`code`),
  ADD KEY `idx_code` (`code`),
  ADD KEY `idx_is_active` (`is_active`);

--
-- Indexes for table `questions`
--
ALTER TABLE `questions`
  ADD PRIMARY KEY (`id`),
  ADD KEY `created_by` (`created_by`),
  ADD KEY `idx_category_id` (`category_id`),
  ADD KEY `idx_question_type` (`question_type`),
  ADD KEY `idx_difficulty` (`difficulty`),
  ADD KEY `idx_active` (`active`),
  ADD KEY `idx_pattern_id` (`pattern_id`);

--
-- Indexes for table `question_approvals`
--
ALTER TABLE `question_approvals`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_question_id` (`question_id`),
  ADD KEY `idx_status` (`status`),
  ADD KEY `idx_submitted_by` (`submitted_by`),
  ADD KEY `idx_reviewed_by` (`reviewed_by`);

--
-- Indexes for table `question_categories`
--
ALTER TABLE `question_categories`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `name` (`name`),
  ADD KEY `idx_name` (`name`);

--
-- Indexes for table `question_options`
--
ALTER TABLE `question_options`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_question_id` (`question_id`);

--
-- Indexes for table `question_patterns`
--
ALTER TABLE `question_patterns`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `unique_pattern` (`skill_id`,`pattern_code`),
  ADD KEY `idx_pattern_code` (`pattern_code`),
  ADD KEY `idx_skill_id` (`skill_id`);

--
-- Indexes for table `question_usage_history`
--
ALTER TABLE `question_usage_history`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `unique_user_question` (`user_id`,`question_id`),
  ADD KEY `used_in_exam_id` (`used_in_exam_id`),
  ADD KEY `idx_user_id` (`user_id`),
  ADD KEY `idx_question_id` (`question_id`);

--
-- Indexes for table `registration_forms`
--
ALTER TABLE `registration_forms`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `form_number` (`form_number`),
  ADD KEY `processed_by` (`processed_by`),
  ADD KEY `idx_form_number` (`form_number`),
  ADD KEY `idx_status` (`status`),
  ADD KEY `idx_course_code` (`course_code`);

--
-- Indexes for table `schedule_change_requests`
--
ALTER TABLE `schedule_change_requests`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_schedule_id` (`schedule_id`),
  ADD KEY `idx_status` (`status`),
  ADD KEY `idx_requested_by` (`requested_by`),
  ADD KEY `idx_reviewed_by` (`reviewed_by`);

--
-- Indexes for table `student_answers`
--
ALTER TABLE `student_answers`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_attempt_id` (`attempt_id`),
  ADD KEY `idx_exam_question_id` (`exam_question_id`);

--
-- Indexes for table `test_access_history`
--
ALTER TABLE `test_access_history`
  ADD PRIMARY KEY (`id`),
  ADD KEY `exam_id` (`exam_id`),
  ADD KEY `idx_user_id` (`user_id`),
  ADD KEY `idx_access_type` (`access_type`),
  ADD KEY `idx_accessed_at` (`accessed_at`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `username` (`username`),
  ADD UNIQUE KEY `email` (`email`),
  ADD KEY `idx_username` (`username`),
  ADD KEY `idx_email` (`email`),
  ADD KEY `idx_role` (`role`);

--
-- Indexes for table `variant_questions`
--
ALTER TABLE `variant_questions`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `unique_variant_question` (`variant_id`,`question_id`),
  ADD KEY `idx_variant_id` (`variant_id`),
  ADD KEY `idx_question_id` (`question_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `ai_feedback`
--
ALTER TABLE `ai_feedback`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `ai_grading_results`
--
ALTER TABLE `ai_grading_results`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `attendance`
--
ALTER TABLE `attendance`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `audit_logs`
--
ALTER TABLE `audit_logs`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `chatbot_conversations`
--
ALTER TABLE `chatbot_conversations`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `chatbot_messages`
--
ALTER TABLE `chatbot_messages`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `classes`
--
ALTER TABLE `classes`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `class_schedules`
--
ALTER TABLE `class_schedules`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `class_students`
--
ALTER TABLE `class_students`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `class_teachers`
--
ALTER TABLE `class_teachers`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `courses`
--
ALTER TABLE `courses`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `course_registrations`
--
ALTER TABLE `course_registrations`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `exams`
--
ALTER TABLE `exams`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `exam_attempts`
--
ALTER TABLE `exam_attempts`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `exam_configs`
--
ALTER TABLE `exam_configs`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `exam_pattern_distribution`
--
ALTER TABLE `exam_pattern_distribution`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `exam_questions`
--
ALTER TABLE `exam_questions`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT for table `exam_skills`
--
ALTER TABLE `exam_skills`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `exam_variants`
--
ALTER TABLE `exam_variants`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `forum_categories`
--
ALTER TABLE `forum_categories`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `forum_comments`
--
ALTER TABLE `forum_comments`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `forum_likes`
--
ALTER TABLE `forum_likes`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `forum_posts`
--
ALTER TABLE `forum_posts`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `invoices`
--
ALTER TABLE `invoices`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `learning_reports`
--
ALTER TABLE `learning_reports`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `lesson_quizzes`
--
ALTER TABLE `lesson_quizzes`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `login_history`
--
ALTER TABLE `login_history`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `manual_grades`
--
ALTER TABLE `manual_grades`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `notifications`
--
ALTER TABLE `notifications`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `notification_recipients`
--
ALTER TABLE `notification_recipients`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `payments`
--
ALTER TABLE `payments`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `payment_methods`
--
ALTER TABLE `payment_methods`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `questions`
--
ALTER TABLE `questions`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=13;

--
-- AUTO_INCREMENT for table `question_approvals`
--
ALTER TABLE `question_approvals`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `question_categories`
--
ALTER TABLE `question_categories`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `question_options`
--
ALTER TABLE `question_options`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=25;

--
-- AUTO_INCREMENT for table `question_patterns`
--
ALTER TABLE `question_patterns`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;

--
-- AUTO_INCREMENT for table `question_usage_history`
--
ALTER TABLE `question_usage_history`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `registration_forms`
--
ALTER TABLE `registration_forms`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `schedule_change_requests`
--
ALTER TABLE `schedule_change_requests`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `student_answers`
--
ALTER TABLE `student_answers`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT for table `test_access_history`
--
ALTER TABLE `test_access_history`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT for table `variant_questions`
--
ALTER TABLE `variant_questions`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `ai_feedback`
--
ALTER TABLE `ai_feedback`
  ADD CONSTRAINT `ai_feedback_ibfk_1` FOREIGN KEY (`ai_grading_id`) REFERENCES `ai_grading_results` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `ai_grading_results`
--
ALTER TABLE `ai_grading_results`
  ADD CONSTRAINT `ai_grading_results_ibfk_1` FOREIGN KEY (`student_answer_id`) REFERENCES `student_answers` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `attendance`
--
ALTER TABLE `attendance`
  ADD CONSTRAINT `attendance_ibfk_1` FOREIGN KEY (`schedule_id`) REFERENCES `class_schedules` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `attendance_ibfk_2` FOREIGN KEY (`student_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `audit_logs`
--
ALTER TABLE `audit_logs`
  ADD CONSTRAINT `audit_logs_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE SET NULL;

--
-- Constraints for table `chatbot_conversations`
--
ALTER TABLE `chatbot_conversations`
  ADD CONSTRAINT `chatbot_conversations_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE SET NULL;

--
-- Constraints for table `chatbot_messages`
--
ALTER TABLE `chatbot_messages`
  ADD CONSTRAINT `chatbot_messages_ibfk_1` FOREIGN KEY (`conversation_id`) REFERENCES `chatbot_conversations` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `classes`
--
ALTER TABLE `classes`
  ADD CONSTRAINT `classes_ibfk_1` FOREIGN KEY (`course_id`) REFERENCES `courses` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `class_schedules`
--
ALTER TABLE `class_schedules`
  ADD CONSTRAINT `class_schedules_ibfk_1` FOREIGN KEY (`class_id`) REFERENCES `classes` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `class_students`
--
ALTER TABLE `class_students`
  ADD CONSTRAINT `class_students_ibfk_1` FOREIGN KEY (`class_id`) REFERENCES `classes` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `class_students_ibfk_2` FOREIGN KEY (`student_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `class_teachers`
--
ALTER TABLE `class_teachers`
  ADD CONSTRAINT `class_teachers_ibfk_1` FOREIGN KEY (`class_id`) REFERENCES `classes` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `class_teachers_ibfk_2` FOREIGN KEY (`teacher_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `course_registrations`
--
ALTER TABLE `course_registrations`
  ADD CONSTRAINT `course_registrations_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `course_registrations_ibfk_2` FOREIGN KEY (`course_id`) REFERENCES `courses` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `exams`
--
ALTER TABLE `exams`
  ADD CONSTRAINT `exams_ibfk_1` FOREIGN KEY (`course_id`) REFERENCES `courses` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `exams_ibfk_2` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `exam_attempts`
--
ALTER TABLE `exam_attempts`
  ADD CONSTRAINT `exam_attempts_ibfk_1` FOREIGN KEY (`exam_id`) REFERENCES `exams` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `exam_attempts_ibfk_2` FOREIGN KEY (`student_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `exam_configs`
--
ALTER TABLE `exam_configs`
  ADD CONSTRAINT `exam_configs_ibfk_1` FOREIGN KEY (`exam_id`) REFERENCES `exams` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `exam_pattern_distribution`
--
ALTER TABLE `exam_pattern_distribution`
  ADD CONSTRAINT `exam_pattern_distribution_ibfk_1` FOREIGN KEY (`exam_id`) REFERENCES `exams` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `exam_pattern_distribution_ibfk_2` FOREIGN KEY (`pattern_id`) REFERENCES `question_patterns` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `exam_questions`
--
ALTER TABLE `exam_questions`
  ADD CONSTRAINT `exam_questions_ibfk_1` FOREIGN KEY (`exam_id`) REFERENCES `exams` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `exam_questions_ibfk_2` FOREIGN KEY (`question_id`) REFERENCES `questions` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `exam_variants`
--
ALTER TABLE `exam_variants`
  ADD CONSTRAINT `exam_variants_ibfk_1` FOREIGN KEY (`exam_id`) REFERENCES `exams` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `forum_comments`
--
ALTER TABLE `forum_comments`
  ADD CONSTRAINT `forum_comments_ibfk_1` FOREIGN KEY (`post_id`) REFERENCES `forum_posts` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `forum_comments_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `forum_likes`
--
ALTER TABLE `forum_likes`
  ADD CONSTRAINT `forum_likes_ibfk_1` FOREIGN KEY (`post_id`) REFERENCES `forum_posts` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `forum_likes_ibfk_2` FOREIGN KEY (`comment_id`) REFERENCES `forum_comments` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `forum_likes_ibfk_3` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `forum_posts`
--
ALTER TABLE `forum_posts`
  ADD CONSTRAINT `forum_posts_ibfk_1` FOREIGN KEY (`category_id`) REFERENCES `forum_categories` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `forum_posts_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `invoices`
--
ALTER TABLE `invoices`
  ADD CONSTRAINT `invoices_ibfk_1` FOREIGN KEY (`payment_id`) REFERENCES `payments` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `learning_reports`
--
ALTER TABLE `learning_reports`
  ADD CONSTRAINT `learning_reports_ibfk_1` FOREIGN KEY (`class_id`) REFERENCES `classes` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `learning_reports_ibfk_2` FOREIGN KEY (`student_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `learning_reports_ibfk_3` FOREIGN KEY (`teacher_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `lesson_quizzes`
--
ALTER TABLE `lesson_quizzes`
  ADD CONSTRAINT `lesson_quizzes_ibfk_1` FOREIGN KEY (`class_id`) REFERENCES `classes` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `lesson_quizzes_ibfk_2` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `login_history`
--
ALTER TABLE `login_history`
  ADD CONSTRAINT `login_history_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `manual_grades`
--
ALTER TABLE `manual_grades`
  ADD CONSTRAINT `manual_grades_ibfk_1` FOREIGN KEY (`attempt_id`) REFERENCES `exam_attempts` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `manual_grades_ibfk_2` FOREIGN KEY (`exam_question_id`) REFERENCES `exam_questions` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `manual_grades_ibfk_3` FOREIGN KEY (`graded_by`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `notifications`
--
ALTER TABLE `notifications`
  ADD CONSTRAINT `notifications_ibfk_1` FOREIGN KEY (`sender_id`) REFERENCES `users` (`id`) ON DELETE SET NULL;

--
-- Constraints for table `notification_recipients`
--
ALTER TABLE `notification_recipients`
  ADD CONSTRAINT `notification_recipients_ibfk_1` FOREIGN KEY (`notification_id`) REFERENCES `notifications` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `notification_recipients_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `payments`
--
ALTER TABLE `payments`
  ADD CONSTRAINT `payments_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `payments_ibfk_2` FOREIGN KEY (`payment_method_id`) REFERENCES `payment_methods` (`id`) ON DELETE SET NULL;

--
-- Constraints for table `questions`
--
ALTER TABLE `questions`
  ADD CONSTRAINT `questions_ibfk_1` FOREIGN KEY (`category_id`) REFERENCES `question_categories` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `questions_ibfk_2` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `questions_ibfk_3` FOREIGN KEY (`pattern_id`) REFERENCES `question_patterns` (`id`) ON DELETE SET NULL;

--
-- Constraints for table `question_approvals`
--
ALTER TABLE `question_approvals`
  ADD CONSTRAINT `question_approvals_ibfk_1` FOREIGN KEY (`question_id`) REFERENCES `questions` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `question_approvals_ibfk_2` FOREIGN KEY (`submitted_by`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `question_approvals_ibfk_3` FOREIGN KEY (`reviewed_by`) REFERENCES `users` (`id`) ON DELETE SET NULL;

--
-- Constraints for table `question_options`
--
ALTER TABLE `question_options`
  ADD CONSTRAINT `question_options_ibfk_1` FOREIGN KEY (`question_id`) REFERENCES `questions` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `question_patterns`
--
ALTER TABLE `question_patterns`
  ADD CONSTRAINT `question_patterns_ibfk_1` FOREIGN KEY (`skill_id`) REFERENCES `exam_skills` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `question_usage_history`
--
ALTER TABLE `question_usage_history`
  ADD CONSTRAINT `question_usage_history_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `question_usage_history_ibfk_2` FOREIGN KEY (`question_id`) REFERENCES `questions` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `question_usage_history_ibfk_3` FOREIGN KEY (`used_in_exam_id`) REFERENCES `exams` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `registration_forms`
--
ALTER TABLE `registration_forms`
  ADD CONSTRAINT `registration_forms_ibfk_1` FOREIGN KEY (`processed_by`) REFERENCES `users` (`id`) ON DELETE SET NULL;

--
-- Constraints for table `schedule_change_requests`
--
ALTER TABLE `schedule_change_requests`
  ADD CONSTRAINT `schedule_change_requests_ibfk_1` FOREIGN KEY (`schedule_id`) REFERENCES `class_schedules` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `schedule_change_requests_ibfk_2` FOREIGN KEY (`requested_by`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `schedule_change_requests_ibfk_3` FOREIGN KEY (`reviewed_by`) REFERENCES `users` (`id`) ON DELETE SET NULL;

--
-- Constraints for table `student_answers`
--
ALTER TABLE `student_answers`
  ADD CONSTRAINT `student_answers_ibfk_1` FOREIGN KEY (`attempt_id`) REFERENCES `exam_attempts` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `student_answers_ibfk_2` FOREIGN KEY (`exam_question_id`) REFERENCES `exam_questions` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `test_access_history`
--
ALTER TABLE `test_access_history`
  ADD CONSTRAINT `test_access_history_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `test_access_history_ibfk_2` FOREIGN KEY (`exam_id`) REFERENCES `exams` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `variant_questions`
--
ALTER TABLE `variant_questions`
  ADD CONSTRAINT `variant_questions_ibfk_1` FOREIGN KEY (`variant_id`) REFERENCES `exam_variants` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `variant_questions_ibfk_2` FOREIGN KEY (`question_id`) REFERENCES `questions` (`id`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
