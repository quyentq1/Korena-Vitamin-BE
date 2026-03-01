package com.trainingcenter.service;

import com.trainingcenter.dto.auth.ForgotPasswordRequest;
import com.trainingcenter.dto.auth.JwtResponse;
import com.trainingcenter.dto.auth.LoginRequest;
import com.trainingcenter.dto.auth.ResetPasswordRequest;
import com.trainingcenter.dto.auth.SignupRequest;
import com.trainingcenter.entity.Exam;
import com.trainingcenter.entity.ExamAttempt;
import com.trainingcenter.entity.PasswordResetToken;
import com.trainingcenter.entity.User;
import com.trainingcenter.exception.BadRequestException;
import com.trainingcenter.exception.UnauthorizedException;
import com.trainingcenter.repository.ExamAttemptRepository;
import com.trainingcenter.repository.ExamRepository;
import com.trainingcenter.repository.PasswordResetTokenRepository;
import com.trainingcenter.repository.UserRepository;
import com.trainingcenter.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

        @Autowired
        private AuthenticationManager authenticationManager;

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private JwtTokenProvider tokenProvider;

        @Autowired
        private PasswordResetTokenRepository passwordResetTokenRepository;

        @Autowired
        private PasswordEncoder passwordEncoder;

        @Autowired
        private EmailService emailService;

        @Autowired
        private ExamAttemptRepository examAttemptRepository;

        @Autowired
        private ExamRepository examRepository;

        public JwtResponse login(LoginRequest loginRequest) {
                // 1. Check if username exists
                User user = userRepository.findByUsername(loginRequest.getUsername())
                                .orElseThrow(() -> new UnauthorizedException("Username not found"));

                try {
                        // 2. Attempt authentication
                        Authentication authentication = authenticationManager.authenticate(
                                        new UsernamePasswordAuthenticationToken(
                                                        loginRequest.getUsername(),
                                                        loginRequest.getPassword()));

                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        String jwt = tokenProvider.generateToken(authentication);

                        if (user.getExpirationDate() != null
                                        && user.getExpirationDate().isBefore(java.time.LocalDate.now())) {
                                throw new UnauthorizedException("Account has expired");
                        }

                        return new JwtResponse(
                                        jwt,
                                        user.getId(),
                                        user.getUsername(),
                                        user.getFullName(),
                                        user.getEmail(),
                                        user.getRole().name());

                } catch (org.springframework.security.core.AuthenticationException e) {
                        // 3. If authentication fails but user exists, it must be the wrong password
                        throw new UnauthorizedException("Incorrect password");
                }
        }

        /**
         * Handle forgot password request
         * Generates a reset token and sends email to user
         */
        public void forgotPassword(String email) {
                // Find user by email
                User user = userRepository.findByEmail(email)
                        .orElseThrow(() -> new BadRequestException("Email không tồn tại trong hệ thống / Email not found in system"));

                // Delete any existing reset tokens for this user
                passwordResetTokenRepository.deleteByUser(user);

                // Generate new reset token
                String resetToken = UUID.randomUUID().toString();
                LocalDateTime expiryDate = LocalDateTime.now().plusHours(1); // Token expires in 1 hour

                PasswordResetToken passwordResetToken = new PasswordResetToken();
                passwordResetToken.setToken(resetToken);
                passwordResetToken.setUser(user);
                passwordResetToken.setExpiryDate(expiryDate);
                passwordResetToken.setUsed(false);

                passwordResetTokenRepository.save(passwordResetToken);

                // Send password reset email
                sendPasswordResetEmail(user.getEmail(), user.getFullName(), resetToken);
        }

        /**
         * Reset password with token
         */
        public void resetPassword(ResetPasswordRequest request) {
                // Validate passwords match
                if (!request.getNewPassword().equals(request.getConfirmPassword())) {
                        throw new BadRequestException("Mật khẩu xác nhận không khớp / Passwords do not match");
                }

                // Find valid token
                PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(request.getToken())
                        .orElseThrow(() -> new BadRequestException("Token không hợp lệ / Invalid token"));

                // Check if token is expired
                if (resetToken.isExpired()) {
                        throw new BadRequestException("Token đã hết hạn / Token has expired");
                }

                // Check if token is already used
                if (resetToken.getUsed()) {
                        throw new BadRequestException("Token đã được sử dụng / Token already used");
                }

                // Get user
                User user = resetToken.getUser();

                // Update password
                user.setPassword(passwordEncoder.encode(request.getNewPassword()));
                userRepository.save(user);

                // Mark token as used
                resetToken.setUsed(true);
                passwordResetTokenRepository.save(resetToken);

                // Send confirmation email
                sendPasswordChangedEmail(user.getEmail(), user.getFullName());
        }

        /**
         * Send password reset email with token
         */
        private void sendPasswordResetEmail(String toEmail, String fullName, String resetToken) {
                String resetUrl = "http://localhost:5173/reset-password?token=" + resetToken;

                String subject = "Đặt lại mật khẩu | Reset Your Password - Korean Vitamin";

                String htmlBody = "<div style=\"font-family: Arial, sans-serif; padding: 20px; color: #333; max-width: 600px; margin: 0 auto;\">"
                        + "<h2 style=\"color: #4F46E5;\">Xin chào / Dear " + fullName + ",</h2>"
                        + "<p>Chúng tôi nhận được yêu cầu đặt lại mật khẩu cho tài khoản của bạn tại <strong>Korean Vitamin</strong>.</p>"
                        + "<p>Nếu bạn không yêu cầu điều này, vui lòng bỏ qua email này.</p>"
                        + "<p>Để đặt lại mật khẩu, vui lòng nhấp vào nút bên dưới:</p>"

                        + "<hr style=\"border: 1px solid #eee; margin: 25px 0;\" />"

                        + "<p style=\"color: #555;\"><em>We received a request to reset the password for your account at <strong>Korean Vitamin</strong>.</em></p>"
                        + "<p style=\"color: #555;\"><em>If you didn't make this request, please ignore this email.</em></p>"
                        + "<p style=\"color: #555;\"><em>To reset your password, please click the button below:</em></p>"

                        + "<div style=\"text-align: center; margin: 30px 0;\">"
                        + "<a href=\"" + resetUrl + "\" "
                        + "style=\"background-color: #4F46E5; color: white; padding: 15px 30px; "
                        + "text-decoration: none; border-radius: 8px; font-size: 16px; font-weight: bold; "
                        + "display: inline-block;\">"
                        + "Đặt lại mật khẩu / Reset Password"
                        + "</a>"
                        + "</div>"

                        + "<p style=\"color: #888; font-size: 14px; text-align: center;\">"
                        + "Hoặc copy đường dẫn này vào trình duyệt / Or copy this link to your browser:<br/>"
                        + "<a href=\"" + resetUrl + "\" style=\"color: #4F46E5; word-break: break-all;\">" + resetUrl + "</a>"
                        + "</p>"

                        + "<p style=\"color: #d97706;\"><strong>Lưu ý:</strong> Link này sẽ hết hạn sau 1 giờ.</p>"
                        + "<p style=\"color: #d97706;\"><em><strong>Note:</strong> This link will expire in 1 hour.</em></p>"

                        + "<hr style=\"border: 1px solid #eee; margin: 25px 0;\" />"

                        + "<p>Trân trọng / Best regards,</p>"
                        + "<strong>Korean Vitamin Team</strong>"
                        + "</div>";

                emailService.sendHtmlEmail(toEmail, subject, htmlBody);
        }

        /**
         * Send password changed confirmation email
         */
        private void sendPasswordChangedEmail(String toEmail, String fullName) {
                String subject = "Mật khẩu đã được thay đổi | Password Changed Successfully - Korean Vitamin";

                String htmlBody = "<div style=\"font-family: Arial, sans-serif; padding: 20px; color: #333; max-width: 600px; margin: 0 auto;\">"
                        + "<h2 style=\"color: #10B981;\">Xin chào / Dear " + fullName + ",</h2>"
                        + "<p>Mật khẩu của bạn đã được thay đổi thành công tại <strong>Korean Vitamin</strong>.</p>"
                        + "<p>Nếu bạn không thực hiện hành động này, vui lòng liên hệ với chúng tôi ngay lập tức.</p>"

                        + "<hr style=\"border: 1px solid #eee; margin: 25px 0;\" />"

                        + "<p style=\"color: #555;\"><em>Your password has been successfully changed at <strong>Korean Vitamin</strong>.</em></p>"
                        + "<p style=\"color: #555;\"><em>If you didn't perform this action, please contact us immediately.</em></p>"

                        + "<div style=\"background-color: #ECFDF5; padding: 15px; border-radius: 8px; margin: 25px 0; border-left: 4px solid #10B981;\">"
                        + "<p style=\"margin: 0;\"><strong>Hotline:</strong> 0123.456.789</p>"
                        + "<p style=\"margin: 5px 0 0 0;\"><strong>Email:</strong> support@koreanvitamin.com</p>"
                        + "</div>"

                        + "<hr style=\"border: 1px solid #eee; margin: 25px 0;\" />"

                        + "<p>Trân trọng / Best regards,</p>"
                        + "<strong>Korean Vitamin Team</strong>"
                        + "</div>";

                emailService.sendHtmlEmail(toEmail, subject, htmlBody);
        }

        /**
         * Signup new user with optional guest context migration
         * Migrates guest test history and course interests to new account
         */
        public JwtResponse signup(SignupRequest request) {
                // 1. Check if username already exists
                if (userRepository.existsByUsername(request.getUsername())) {
                        throw new BadRequestException("Username already exists / Tên đăng nhập đã tồn tại");
                }

                // 2. Check if email already exists
                if (userRepository.existsByEmail(request.getEmail())) {
                        throw new BadRequestException("Email already exists / Email đã được sử dụng");
                }

                // 3. Create new user
                User user = new User();
                user.setUsername(request.getUsername());
                user.setPassword(passwordEncoder.encode(request.getPassword()));
                user.setFullName(request.getFullName());
                user.setEmail(request.getEmail());
                user.setPhone(request.getPhone());
                user.setRole(User.UserRole.LEARNER); // Default role for new signups
                user.setActive(true);
                user.setFreeTestCount(0);
                user.setIsPremium(false);
                user.setCreatedAt(LocalDateTime.now());

                user = userRepository.save(user);

                // 4. Migrate guest context if provided
                if (request.getGuestTestHistory() != null && !request.getGuestTestHistory().isEmpty()) {
                        migrateGuestTestHistory(user, request.getGuestTestHistory());
                }

                // 5. Send welcome email
                sendWelcomeEmail(user.getEmail(), user.getFullName(), user.getUsername());

                // 6. Generate JWT token
                Authentication authentication = new UsernamePasswordAuthenticationToken(
                                request.getUsername(),
                                null,
                                java.util.Collections.emptyList()
                );
                SecurityContextHolder.getContext().setAuthentication(authentication);
                String jwt = tokenProvider.generateToken(authentication);

                return new JwtResponse(
                                jwt,
                                user.getId(),
                                user.getUsername(),
                                user.getFullName(),
                                user.getEmail(),
                                user.getRole().name()
                );
        }

        /**
         * Migrate guest test history to new user account
         * Links guest exam attempts to the new user account
         */
        private void migrateGuestTestHistory(User newUser, java.util.List<SignupRequest.GuestTestHistory> guestTestHistory) {
                for (SignupRequest.GuestTestHistory guestAttempt : guestTestHistory) {
                        if (guestAttempt.getExamAttemptId() != null) {
                                // Link existing exam attempt to new user
                                Optional<ExamAttempt> existingAttempt = examAttemptRepository.findById(guestAttempt.getExamAttemptId());
                                if (existingAttempt.isPresent()) {
                                        ExamAttempt attempt = existingAttempt.get();
                                        // Only migrate if this was a guest attempt (no user assigned)
                                        if (attempt.getStudent() == null ||
                                            "guest_".equals(attempt.getStudent().getUsername())) {
                                                attempt.setStudent(newUser);
                                                examAttemptRepository.save(attempt);
                                        }
                                }
                        }

                        // Record test access for new user (counts towards free tests)
                        if (guestAttempt.getExamId() != null) {
                                Optional<Exam> exam = examRepository.findById(guestAttempt.getExamId());
                                if (exam.isPresent()) {
                                        // The guest test will count towards the user's free test quota
                                        // This is intentional - guest tests are part of the 2 free tests
                                        newUser.setFreeTestCount(newUser.getFreeTestCount() + 1);
                                }
                        }
                }

                userRepository.save(newUser);
        }

        /**
         * Send welcome email to new user
         */
        private void sendWelcomeEmail(String toEmail, String fullName, String username) {
                // Use existing EmailService method if available, or create custom
                emailService.sendAccountCreatedEmail(toEmail, fullName, username, "Welcome123");
        }
}
