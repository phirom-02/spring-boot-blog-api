package com.phirom_02.blog_api.service.impl;

import com.phirom_02.blog_api.domain.dtos.CreateUserDto;
import com.phirom_02.blog_api.domain.entities.User;
import com.phirom_02.blog_api.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        UUID userId = UUID.randomUUID();

        user = User.builder()
                .id(userId)
                .name("John Smith")
                .email("john.smith@example.com")
                .build();
    }

    @Test
    public void findUserById_shouldRetrieveAMatchingUser() {
        // Arrange
        UUID id = user.getId();

        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(user));

        // Act
        User result = userService.findUserById(id);

        // Assert
        assertThat(result.getId()).isEqualTo(id);
    }

    @Test
    public void findUserById_shouldThrowEntityNotFoundException() {
        // Arrange
        UUID id = UUID.randomUUID();
        Exception exception = new EntityNotFoundException("No user found with id: " + id);

        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> userService.findUserById(id));
        assertThat("No user found with id: " + id).isEqualTo(exception.getMessage());
    }

    @Test
    public void createUser_shouldReturnUserAfterCreate() {
        // Arrange
        CreateUserDto dto = CreateUserDto.builder()
                .name("new user")
                .email("new.user@example.com")
                .password("@#password")
                .build();

        User user = User.builder()
                .name("new user")
                .email("new.user@example.com")
                .password("@#password")
                .build();

        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        User result = userService.createUser(dto);

        // Assert
        assertThat(result.getName()).isEqualTo(dto.getName());
        assertThat(result.getEmail()).isEqualTo(dto.getEmail());
        assertThat(result.getPassword()).isEqualTo(dto.getPassword());
    }
}