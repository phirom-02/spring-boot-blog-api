package com.phirom_02.blog_api.service.impl;

import com.phirom_02.blog_api.domain.dtos.SignUpDto;
import com.phirom_02.blog_api.util.TestDataHelper;
import com.sun.nio.sctp.IllegalUnbindException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.annotation.Rollback;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Testcontainers
@Import(TestDataHelper.class)
@Transactional
class AuthServiceImplIntTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16:9");

    @Autowired
    AuthServiceImpl authService;

    UserDetails userDetails;

    @BeforeEach
    @Rollback
    void setUp() {
        SignUpDto signUpDto = SignUpDto.builder()
                .name("John Smith")
                .email("john.smith@example.com")
                .password("@#password")
                .confirmPassword("@#password")
                .build();

        userDetails = authService.register(signUpDto);

    }

    @Test
    public void register_shouldReturnUserDetailsAfterCreate() {
        // Arrange
        SignUpDto signUpDto = SignUpDto.builder()
                .name("New User")
                .email("new.user@example.com")
                .password("@#password")
                .confirmPassword("@#password")
                .build();

        // Act
        UserDetails userDetails = authService.register(signUpDto);

        // Assert
        assertThat(userDetails.getUsername()).isEqualTo("new.user@example.com");
        assertThat(userDetails.getPassword()).isNotNull();
    }

    @Test
    public void register_shouldThrowIllegalArgumentException() {
        // Arrange
        SignUpDto signUpDto = SignUpDto.builder()
                .name("John Smith")
                .email("john.smith@example.com")
                .password("@#password")
                .confirmPassword("@password")
                .build();

        Exception exception = new IllegalUnbindException("Password and confirmation password do not match");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> authService.register(signUpDto));
        assertThat("Password and confirmation password do not match").isEqualTo(exception.getMessage());
    }

    @Test
    public void authenticate_shouldAuthenticateAndLoadUserDetails() {
        // Arrange
        String username = "john.smith@example.com";
        String password = "@#password";

        // Act
        UserDetails userDetails = authService.authenticate(username, password);

        // Assert
        assertThat(userDetails.getUsername()).isEqualTo("john.smith@example.com");
    }

    @Test
    public void generateToken_shouldReturnTokenAndValidateToken() {
        // Arrange & Act
        String token = authService.generateToken(userDetails);
        UserDetails validateUserDetails = authService.validateToken(token);


        // Assert
        assertThat(token).isNotNull();
        assertThat(validateUserDetails).isNotNull();
    }
}