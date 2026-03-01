package com.trainingcenter.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class SignupRequest {
    @NotBlank(message = "{validation.fullName.required}")
    @Size(min = 2, max = 100, message = "{validation.fullName.length}")
    private String fullName;

    @NotBlank(message = "{validation.username.required}")
    @Size(min = 3, max = 50, message = "{validation.username.length}")
    private String username;

    @NotBlank(message = "{validation.email.required}")
    @Email(message = "{validation.email.invalid}")
    private String email;

    @NotBlank(message = "{validation.phone.required}")
    @Size(min = 10, max = 20, message = "{validation.phone.length}")
    private String phone;

    @NotBlank(message = "{validation.password.required}")
    @Size(min = 6, max = 50, message = "{validation.password.length}")
    private String password;

    // Guest context data - optional
    private List<GuestTestHistory> guestTestHistory;
    private List<Long> interestedCourseIds;
    private String redirectPath; // The page user was viewing before signup

    @Data
    public static class GuestTestHistory {
        private Long examId;
        private Long examAttemptId;
        private Integer score;
        private Integer correctAnswers;
        private String completedAt;
    }
}
