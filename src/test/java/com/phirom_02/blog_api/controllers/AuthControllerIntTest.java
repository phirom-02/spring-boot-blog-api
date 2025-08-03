package com.phirom_02.blog_api.controllers;

import com.phirom_02.blog_api.IntegrationTest;
import com.phirom_02.blog_api.domain.dtos.*;
import com.phirom_02.blog_api.security.BlogUserDetailsService;
import com.phirom_02.blog_api.service.AuthService;
import com.phirom_02.blog_api.util.TestDataHelper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Objects;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestDataHelper.class)
@Transactional
class AuthControllerIntTest extends IntegrationTest {

    private static final Logger log = LoggerFactory.getLogger(AuthControllerIntTest.class);
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest");

    @Autowired
    BlogUserDetailsService userDetailsService;

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    AuthService authService;

    @BeforeAll
    @Rollback
    void setUp() {
        SignUpDto payload = SignUpDto.builder()
                .name("John Smith")
                .email("john.smith@example.com")
                .password("@#Password")
                .confirmPassword("@#Password")
                .build();

        authService.register(payload);
    }

    @Test
    @Rollback
    public void signUp_shouldReturnUserDetails() {
        // Arrange
        SignUpPayload payload = SignUpPayload.builder()
                .name("New User")
                .email("new.user@example.com")
                .password("@#Password")
                .confirmPassword("@#Password")
                .build();

        // Act
        ResponseEntity<SignUpResponse> response = restTemplate.postForEntity("/api/v1/auth/sign-up", payload, SignUpResponse.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(Objects.requireNonNull(response.getBody()).getEmail()).isEqualTo("new.user@example.com");
        assertThat(Objects.requireNonNull(response.getBody()).getName()).isEqualTo("New User");
    }

    @Test
    public void login_shouldReturnToken() {
        // Arrange
        LoginPayload payload = LoginPayload.builder()
                .email("john.smith@example.com")
                .password("@#Password")
                .build();

        // Act
        ResponseEntity<AuthResponse> response = restTemplate.postForEntity("/api/v1/auth/login", payload, AuthResponse.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(response.getBody()).getToken()).isNotNull();
    }
}