package com.phirom_02.blog_api.controllers;

import com.phirom_02.blog_api.domain.dtos.AuthResponse;
import com.phirom_02.blog_api.domain.dtos.LoginRequest;
import com.phirom_02.blog_api.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginRequest loginRequest) {
        String token = authService.login(
                loginRequest.getEmail(),
                loginRequest.getPassword()
        );
        AuthResponse authResponse = AuthResponse.builder()
                .token(token)
                .expiresIn(86400)
                .build();
        return ResponseEntity.ok(authResponse);
    }
}
