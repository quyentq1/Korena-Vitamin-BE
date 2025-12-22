package com.trainingcenter.repository;

import com.trainingcenter.entity.RegistrationForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegistrationFormRepository extends JpaRepository<RegistrationForm, Long> {
    Optional<RegistrationForm> findByFormNumber(String formNumber);
    List<RegistrationForm> findByStatus(RegistrationForm.FormStatus status);
    List<RegistrationForm> findByCourseCode(String courseCode);
    Boolean existsByFormNumber(String formNumber);
}
