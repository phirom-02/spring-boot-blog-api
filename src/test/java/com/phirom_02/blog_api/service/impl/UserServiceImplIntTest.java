package com.phirom_02.blog_api.service.impl;

import com.phirom_02.blog_api.IntegrationTest;
import com.phirom_02.blog_api.domain.dtos.CreateUserDto;
import com.phirom_02.blog_api.domain.entities.User;
import com.phirom_02.blog_api.repository.UserRepository;
import com.phirom_02.blog_api.util.TestDataHelper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.Rollback;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Testcontainers
@Import(TestDataHelper.class)
@Transactional
class UserServiceImplIntTest extends IntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16:9");

    @Autowired
    private TestDataHelper testDataHelper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserServiceImpl userService;

    @BeforeEach
    @Rollback
    void setUp() {
        testDataHelper.createUser("user A", "user.a@example.com");
        testDataHelper.createUser("user B", "user.b@example.com");
        testDataHelper.createUser("user C", "user.c@example.com");
    }

    @Test
    public void findUserById_shouldRetrieveAMatchingUser() {
        // Arrange
        UUID id = testDataHelper.getUserByEmail("user.a@example.com").get().getId();

        // Act
        User result = userRepository.findById(id).get();

        // Assert
        assertThat(result.getId()).isEqualTo(id);
    }

    @Test
    public void findUserById_shouldThrowEntityNotFoundException() {
        // Arrange
        UUID id = UUID.randomUUID();
        Exception exception = new EntityNotFoundException("No user found with id: " + id);

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> userService.findUserById(id));
        assertThat("No user found with id: " + id).isEqualTo(exception.getMessage());
    }

    @Test
    @Rollback
    public void createUser_shouldReturnUserAfterCreate() {
        // Arrange
        CreateUserDto dto = CreateUserDto.builder()
                .name("new user")
                .email("new.user@example.com")
                .password("@#password")
                .build();

        // Act
        User result = userService.createUser(dto);

        // Assert
        assertThat(result.getName()).isEqualTo(dto.getName());
        assertThat(result.getEmail()).isEqualTo(dto.getEmail());
    }
}