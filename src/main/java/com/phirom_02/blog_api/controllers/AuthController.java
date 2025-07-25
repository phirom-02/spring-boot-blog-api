package com.phirom_02.blog_api.controllers;

import com.phirom_02.blog_api.domain.dtos.AuthResponse;
import com.phirom_02.blog_api.domain.dtos.LoginRequest;
import com.phirom_02.blog_api.domain.dtos.SignUpDto;
import com.phirom_02.blog_api.domain.dtos.SignUpPayload;
import com.phirom_02.blog_api.mappers.AuthMapper;
import com.phirom_02.blog_api.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final AuthMapper authMapper;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginRequest loginRequest) {
        UserDetails userDetails = authService.authenticate(
                loginRequest.getEmail(),
                loginRequest.getPassword()
        );

        String token = authService.generateToken(userDetails);

        AuthResponse authResponse = AuthResponse.builder()
                .token(token)
                .expiresIn(86400)
                .build();

        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/sign-up")
    public ResponseEntity<UserDetails> signUp(@RequestBody @Valid SignUpPayload payload) {
        SignUpDto dto = authMapper.toSignUpDto(payload);
        UserDetails userDetails = authService.register(dto);
        return ResponseEntity.ok(userDetails);
    }
}
