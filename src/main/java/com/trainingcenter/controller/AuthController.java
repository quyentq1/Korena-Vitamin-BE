package com.trainingcenter.controller;

import com.trainingcenter.dto.MessageResponse;
import com.trainingcenter.dto.auth.JwtResponse;
import com.trainingcenter.dto.auth.LoginRequest;
import com.trainingcenter.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @PostMapping("/logout")
    public ResponseEntity<MessageResponse> logout() {
        return ResponseEntity.ok(new MessageResponse("Logged out successfully"));
    }
}
