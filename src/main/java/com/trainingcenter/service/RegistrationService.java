package com.trainingcenter.service;

import com.trainingcenter.entity.Course;
import com.trainingcenter.entity.CourseRegistration;
import com.trainingcenter.entity.User;
import com.trainingcenter.exception.BadRequestException;
import com.trainingcenter.exception.ResourceNotFoundException;
import com.trainingcenter.repository.CourseRegistrationRepository;
import com.trainingcenter.repository.CourseRepository;
import com.trainingcenter.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
@Transactional
public class RegistrationService {

    @Autowired
    private CourseRegistrationRepository registrationRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public CourseRegistration createRegistration(CourseRegistration registration) {
        // Validate course exists
        Course course = courseRepository.findById(registration.getCourse().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        // Validate user exists
        User user = userRepository.findById(registration.getUser().getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        registration.setCourse(course);
        registration.setUser(user);
        registration.setStatus(CourseRegistration.RegistrationStatus.PENDING);

        return registrationRepository.save(registration);
    }

    public CourseRegistration approveRegistration(Long id) {
        CourseRegistration registration = registrationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registration not found with id: " + id));

        registration.setStatus(CourseRegistration.RegistrationStatus.APPROVED);
        return registrationRepository.save(registration);
    }

    public CourseRegistration rejectRegistration(Long id, String reason) {
        CourseRegistration registration = registrationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registration not found with id: " + id));

        registration.setStatus(CourseRegistration.RegistrationStatus.REJECTED);
        registration.setNotes(reason);
        return registrationRepository.save(registration);
    }

    public List<CourseRegistration> getAllRegistrations() {
        return registrationRepository.findAll();
    }

    public List<CourseRegistration> getRegistrationsByStatus(CourseRegistration.RegistrationStatus status) {
        return registrationRepository.findByStatus(status);
    }

    public CourseRegistration getRegistrationById(Long id) {
        return registrationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registration not found with id: " + id));
    }

    public User createStudentAccountFromRegistration(Long registrationId, String studentName, String email, String phone) {
        CourseRegistration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new ResourceNotFoundException("Registration not found"));

        if (registration.getStatus() != CourseRegistration.RegistrationStatus.APPROVED) {
            throw new BadRequestException("Registration must be approved first");
        }

        // Check if student account already exists
        if (email != null && userRepository.existsByEmail(email)) {
            throw new BadRequestException("Email already exists");
        }

        // Generate username from name
        String username = generateUsername(studentName);
        int counter = 1;
        while (userRepository.existsByUsername(username)) {
            username = generateUsername(studentName) + counter++;
        }

        // Generate random password
        String password = generateRandomPassword();

        // Create student account
        User student = new User();
        student.setUsername(username);
        student.setPassword(passwordEncoder.encode(password));
        student.setFullName(studentName);
        student.setEmail(email);
        student.setPhone(phone);
        student.setRole(User.UserRole.STUDENT);
        student.setActive(true);

        student = userRepository.save(student);

        // Update registration with created user
        registration.setUser(student);
        registration.setStatus(CourseRegistration.RegistrationStatus.COMPLETED);
        registrationRepository.save(registration);

        // Note: In real application, you would send username and password to student via email
        // For now, store it in notes
        registration.setNotes("Username: " + username + ", Temporary Password: " + password);
        registrationRepository.save(registration);

        return student;
    }

    private String generateUsername(String fullName) {
        // Remove Vietnamese diacritics and convert to lowercase
        String normalized = fullName.toLowerCase()
                .replaceAll("[àáạảãâầấậẩẫăằắặẳẵ]", "a")
                .replaceAll("[èéẹẻẽêềếệểễ]", "e")
                .replaceAll("[ìíịỉĩ]", "i")
                .replaceAll("[òóọỏõôồốộổỗơờớợởỡ]", "o")
                .replaceAll("[ùúụủũưừứựửữ]", "u")
                .replaceAll("[ỳýỵỷỹ]", "y")
                .replaceAll("[đ]", "d")
                .replaceAll("\\s+", "");
        return normalized.length() > 15 ? normalized.substring(0, 15) : normalized;
    }

    private String generateRandomPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }
        return password.toString();
    }
}
