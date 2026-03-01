package com.trainingcenter.repository;

import com.trainingcenter.entity.PasswordResetToken;
import com.trainingcenter.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    /**
     * Find token by token string
     */
    Optional<PasswordResetToken> findByToken(String token);

    /**
     * Find token by user
     */
    Optional<PasswordResetToken> findByUser(User user);

    /**
     * Delete all tokens for a user
     */
    void deleteByUser(User user);

    /**
     * Check if token exists for user
     */
    boolean existsByUser(User user);
}
