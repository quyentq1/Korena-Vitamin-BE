package com.trainingcenter.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "guest_access_control")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GuestAccessControl {

    @Id
    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    @Column(name = "attempt_count", nullable = false)
    private Integer attemptCount = 0;

    @UpdateTimestamp
    @Column(name = "last_attempt_at")
    private LocalDateTime lastAttemptAt;
}
