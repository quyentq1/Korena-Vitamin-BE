package com.trainingcenter.service;

import java.io.UnsupportedEncodingException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;

    public void sendHtmlEmail(String to, String subject, String htmlBody) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(senderEmail, "AutoSale Support");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true); // true indicates HTML content

            mailSender.send(message);
        } catch (MessagingException | UnsupportedEncodingException e) {
            e.printStackTrace();
            // In a real application, you would log this error and potentially throw a
            // custom exception
        }
    }

    public void sendWelcomeEmail(String toEmail, String fullName) {
        String subject = "Xác nhận đăng ký tư vấn/khóa học | Consultation/Course Registration Confirmation";
        String htmlBody = "<div style=\"font-family: Arial, sans-serif; padding: 20px; color: #333; max-width: 600px; margin: 0 auto;\">"
                + "<h2 style=\"color: #4F46E5;\">Kính chào / Dear " + fullName + ",</h2>"

                + "<p>Cảm ơn bạn đã quan tâm và đăng ký tư vấn/khóa học tại <strong>Hệ thống Trung tâm Hàn ngữ Korean Vitamin</strong>.</p>"
                + "<p>Chúng tôi đã nhận được thông tin của bạn. Đội ngũ tư vấn viên của chúng tôi sẽ liên hệ với bạn trong vòng 24 giờ tới để hỗ trợ chi tiết.</p>"
                + "<p>Nếu bạn có câu hỏi khẩn cấp, vui lòng liên hệ hotline: <strong>0123.456.789</strong>.</p>"

                + "<hr style=\"border: 1px solid #eee; margin: 25px 0;\" />"

                + "<p style=\"color: #555;\"><em>Thank you for your interest and registration for consultation/courses at <strong>Korean Vitamin Language Center</strong>.</em></p>"
                + "<p style=\"color: #555;\"><em>We have received your information. Our consulting team will contact you within the next 24 hours to provide detailed support.</em></p>"
                + "<p style=\"color: #555;\"><em>If you have any urgent questions, please contact our hotline: <strong>0123.456.789</strong>.</em></p>"
                + "<br/>"

                + "<p>Trân trọng / Best regards,</p>"
                + "<strong>Korean Staff | Korean Vitamin Team</strong>"
                + "</div>";

        sendHtmlEmail(toEmail, subject, htmlBody);
    }

    public void sendAccountCreatedEmail(String toEmail, String fullName, String username, String tempPassword) {
        String subject = "Tài khoản học viên của bạn đã được khởi tạo | Your Student Account has been created";
        String htmlBody = "<div style=\"font-family: Arial, sans-serif; padding: 20px; color: #333; max-width: 600px; margin: 0 auto;\">"
                + "<h2 style=\"color: #4F46E5;\">Kính chào / Dear " + fullName + ",</h2>"

                + "<p>Chúc mừng bạn đã chính thức trở thành học viên của <strong>Korean Vitamin</strong>.</p>"
                + "<p>Hệ thống đã tự động cấp cho bạn một tài khoản để truy cập vào cổng học viên (Lịch học, tài liệu, thi thử, ...).</p>"
                + "<p style=\"color: #d97706;\"><strong>Lưu ý:</strong> Bạn vui lòng đăng nhập và đổi mật khẩu ngay trong lần đầu tiên để đảm bảo bảo mật.</p>"

                + "<hr style=\"border: 1px solid #eee; margin: 25px 0;\" />"

                + "<p style=\"color: #555;\"><em>Congratulations on officially becoming a student of <strong>Korean Vitamin</strong>.</em></p>"
                + "<p style=\"color: #555;\"><em>The system has automatically granted you an account to access the student portal (Class schedules, materials, mock exams, etc.).</em></p>"
                + "<p style=\"color: #d97706;\"><em><strong>Note:</strong> Please log in and change your password immediately upon your first login to ensure security.</em></p>"

                + "<div style=\"background-color: #f3f4f6; padding: 20px; border-radius: 8px; margin: 25px 0; border-left: 4px solid #4F46E5;\">"
                + "<h4 style=\"margin-top: 0; margin-bottom: 10px;\">Thông tin Đăng nhập / Login Information:</h4>"
                + "<p style=\"margin: 5px 0;\"><strong>Trang đăng nhập / Login Page:</strong> <a href=\"http://localhost:5173/login\" style=\"color: #4F46E5; text-decoration: none;\">Cổng Học Viên / Student Portal</a></p>"
                + "<p style=\"margin: 5px 0;\"><strong>Tên đăng nhập / Username:</strong> " + username + "</p>"
                + "<p style=\"margin: 5px 0;\"><strong>Mật khẩu tạm thời / Temporary Password:</strong> " + tempPassword
                + "</p>"
                + "</div>"

                + "<p>Trân trọng / Best regards,</p>"
                + "<strong>AutoSale Support | Korean Vitamin Team</strong>"
                + "</div>";

        sendHtmlEmail(toEmail, subject, htmlBody);
    }
}
