package com.trainingcenter.controller;

import com.trainingcenter.dto.MessageResponse;
import com.trainingcenter.dto.auth.ForgotPasswordRequest;
import com.trainingcenter.dto.auth.JwtResponse;
import com.trainingcenter.dto.auth.LoginRequest;
import com.trainingcenter.dto.auth.ResetPasswordRequest;
import com.trainingcenter.dto.auth.SignupRequest;
import com.trainingcenter.exception.BadRequestException;
import com.trainingcenter.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private com.trainingcenter.repository.UserRepository userRepository;

    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    /**
     * Signup - Register new user with optional guest context migration
     * POST /auth/signup
     * Body: {
     *   "fullName": "John Doe",
     *   "username": "johndoe",
     *   "email": "john@example.com",
     *   "phone": "0123456789",
     *   "password": "password123",
     *   "guestTestHistory": [...],  // optional - guest test attempts
     *   "interestedCourseIds": [1, 2, 3],  // optional - courses guest viewed
     *   "redirectPath": "/courses"  // optional - page to redirect after signup
     * }
     */
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest request) {
        try {
            JwtResponse response = authService.signup(request);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Đăng ký thành công! / Registration successful!",
                    "user", response,
                    "redirectPath", request.getRedirectPath() != null ? request.getRedirectPath() : "/learner-dashboard"
            ));
        } catch (BadRequestException e) {
            return ResponseEntity.status(400).body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<MessageResponse> logout() {
        return ResponseEntity.ok(new MessageResponse("Logged out successfully"));
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody java.util.Map<String, String> request,
            org.springframework.security.core.Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()
                || authentication.getName().equals("anonymousUser")) {
            return ResponseEntity.status(401).body(java.util.Map.of("message", "Unauthorized"));
        }
        String username = authentication.getName();
        com.trainingcenter.entity.User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new com.trainingcenter.exception.UnauthorizedException("User not found"));

        String currentPassword = request.get("currentPassword");
        String newPassword = request.get("newPassword");

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            return ResponseEntity.status(400).body(java.util.Map.of("message", "Mật khẩu hiện tại không chính xác"));
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return ResponseEntity.ok(java.util.Map.of("success", true, "message", "Đổi mật khẩu thành công"));
    }

    /**
     * Forgot Password - Request password reset email
     * POST /auth/forgot-password
     * Body: { "email": "user@example.com" }
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request.getEmail());
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Đã gửi email đặt lại mật khẩu. Vui lòng kiểm tra hộp thư của bạn. / Password reset email sent. Please check your inbox."
        ));
    }

    /**
     * Reset Password - Reset password with token
     * POST /auth/reset-password
     * Body: { "token": "uuid-token", "newPassword": "newpass", "confirmPassword": "newpass" }
     */
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Đặt lại mật khẩu thành công. Vui lòng đăng nhập với mật khẩu mới. / Password reset successfully. Please login with your new password."
        ));
    }
}
