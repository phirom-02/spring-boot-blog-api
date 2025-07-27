package com.phirom_02.blog_api.controllers;

import com.phirom_02.blog_api.domain.dtos.AuthResponse;
import com.phirom_02.blog_api.domain.dtos.LoginPayload;
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

/**
 * REST controller for handling authentication-related operations such as login and sign-up.
 */
@RestController
@RequestMapping(path = "/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final AuthMapper authMapper;


    /**
     * Handles the login process. Authenticates the user using the provided credentials
     * and returns an authentication token.
     *
     * @param loginPayload the login request payload containing email and password
     * @return a {@link ResponseEntity} containing an {@link AuthResponse} with the authentication token
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginPayload loginPayload) {
        // Authenticate the user with the provided email and password
        UserDetails userDetails = authService.authenticate(
                loginPayload.getEmail(),
                loginPayload.getPassword()
        );

        // Generate JWT token for the authenticated user
        String token = authService.generateToken(userDetails);

        // Create and return an AuthResponse containing the token and expiry time
        AuthResponse authResponse = AuthResponse.builder()
                .token(token)
                .build();

        return ResponseEntity.ok(authResponse);
    }

    /**
     * Handles the sign-up process. Registers a new user with the provided sign-up details.
     *
     * @param payload the sign-up request payload containing user details (e.g., name, email, password)
     * @return a {@link ResponseEntity} containing the {@link UserDetails} of the newly registered user
     */
    @PostMapping("/sign-up")
    public ResponseEntity<UserDetails> signUp(@RequestBody @Valid SignUpPayload payload) {
        // Convert the sign-up payload to a DTO
        SignUpDto dto = authMapper.toSignUpDto(payload);

        // Register the user and retrieve their details
        UserDetails userDetails = authService.register(dto);

        // Return the user details in the response
        return ResponseEntity.ok(userDetails);
    }
}
