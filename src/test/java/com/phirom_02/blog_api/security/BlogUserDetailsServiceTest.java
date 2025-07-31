package com.phirom_02.blog_api.security;

import com.phirom_02.blog_api.domain.entities.User;
import com.phirom_02.blog_api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BlogUserDetailsServiceTest {

    @InjectMocks
    BlogUserDetailsService blogUserDetailsService;

    @Mock
    private UserRepository userRepository;

    private User user;
    private BlogUserDetails blogUserDetails;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(UUID.randomUUID())
                .name("John Smith")
                .email("john.smith@example.com")
                .password("#@password")
                .build();

        blogUserDetails = new BlogUserDetails(user);
    }

    @Test
    public void loadUserByUsername_shouldReturnAMatchingUser() {
        // Arrange
        String email = "john.smith@example.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

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

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> blogUserDetailsService.loadUserByUsername(email));
        assertThat("User not found with email" + email).isEqualTo(exception.getMessage());
    }
}