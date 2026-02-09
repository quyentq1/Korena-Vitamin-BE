package com.trainingcenter.service;

import com.trainingcenter.dto.auth.JwtResponse;
import com.trainingcenter.dto.auth.LoginRequest;
import com.trainingcenter.entity.User;
import com.trainingcenter.exception.UnauthorizedException;
import com.trainingcenter.repository.UserRepository;
import com.trainingcenter.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

        @Autowired
        private AuthenticationManager authenticationManager;

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private JwtTokenProvider tokenProvider;

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
}
