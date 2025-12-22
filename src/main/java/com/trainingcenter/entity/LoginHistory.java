package com.trainingcenter.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "login_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @CreationTimestamp
    @Column(name = "login_time", nullable = false, updatable = false)
    private LocalDateTime loginTime;

    @Column(name = "logout_time")
    private LocalDateTime logoutTime;

    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    @Column(name = "device_info", columnDefinition = "TEXT")
    private String deviceInfo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoginStatus status = LoginStatus.SUCCESS;

    @Column(name = "failure_reason", length = 200)
    private String failureReason;

    public enum LoginStatus {
        SUCCESS,
        FAILED
    }
}
