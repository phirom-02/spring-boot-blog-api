package com.phirom_02.blog_api.security;

import com.phirom_02.blog_api.util.TestDataHelper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
class BlogUserDetailsServiceIntTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16:9");

    @Autowired
    private TestDataHelper testDataHelper;

    @Autowired
    private BlogUserDetailsService blogUserDetailsService;

    @BeforeEach
    @Rollback
    void setUp() {
        testDataHelper.createUser("John Smith", "john.smith@example.com");
    }

    @Test
    public void loadUserByUsername_shouldReturnAMatchingUser() {
        // Arrange
        String email = "john.smith@example.com";

        // Act
        UserDetails result = blogUserDetailsService.loadUserByUsername(email);

        // Assert
        assertThat(result.getUsername()).isEqualTo(email);
    }

    @Test
    public void loadUserByUsername_shouldThrowUsernameNotFoundException() {
        // Arrange
        String email = "johnsmith@example.com";
        Exception exception = new UsernameNotFoundException("User not found with email" + email);

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> blogUserDetailsService.loadUserByUsername(email));
        assertThat("User not found with email" + email).isEqualTo(exception.getMessage());
    }
}