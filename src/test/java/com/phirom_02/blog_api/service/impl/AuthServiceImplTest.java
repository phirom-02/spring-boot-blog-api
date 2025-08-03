package com.phirom_02.blog_api.service.impl;

import com.phirom_02.blog_api.domain.dtos.CreateUserDto;
import com.phirom_02.blog_api.domain.dtos.SignUpDto;
import com.phirom_02.blog_api.domain.entities.User;
import com.phirom_02.blog_api.mappers.AuthMapper;
import com.phirom_02.blog_api.security.BlogUserDetails;
import com.phirom_02.blog_api.service.JwtService;
import com.phirom_02.blog_api.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @InjectMocks
    private AuthServiceImpl authService;

    @Mock
    private UserDetailsService userDetailsService;
    @Mock
    private UserService userService;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtService jwtService;
    @Mock
    private AuthMapper authMapper;

    @Test
    public void register_shouldReturnUserDetailsAfterCreate() {
        // Arrange
        SignUpDto signUpDto = SignUpDto.builder()
                .email("john.smith@example.com")
                .name("John Smith")
                .password("#@password")
                .confirmPassword("#@password")
                .build();

        CreateUserDto createUserDto = CreateUserDto.builder()
                .email("john.smith@example.com")
                .name("John Smith")
                .password("#@password")
                .build();

        User user = User.builder()
                .email("john.smith@example.com")
                .name("John Smith")
                .password("#@password")
                .build();


        when(authMapper.toCreateUserDto(signUpDto)).thenReturn(createUserDto);
        when(userService.createUser(createUserDto)).thenReturn(user);

        // Act
        UserDetails userDetails = authService.register(signUpDto);

        // Assert
        assertThat(userDetails.getUsername()).isEqualTo("john.smith@example.com");
    }

    @Test
    public void register_shouldThrowIllegalArgumentException() {
        // Arrange
        SignUpDto signUpDto = SignUpDto.builder()
                .email("john.smith@example.com")
                .name("John Smith")
                .password("#@password")
                .confirmPassword("@#password")
                .build();
        Exception exception = new IllegalArgumentException("Password and confirmation password do not match");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> authService.register(signUpDto));
        assertThat("Password and confirmation password do not match").isEqualTo(exception.getMessage());
    }

    @Test
    public void authenticate_shouldAuthenticateAndLoadUserDetails() {
        // Arrange
        String username = "john.smith@example.com";
        String password = "@#password";

        User user = User.builder()
                .email("john.smith@example.com")
                .password("@#password")
                .build();

        UserDetails userDetails = new BlogUserDetails(user);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);

        // Act
        UserDetails result = authService.authenticate(username, password);

        // Assert
        assertThat(result.getUsername()).isEqualTo(username);
        verify(authenticationManager, times(1)).authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }

    @Test
    public void generateToken_shouldReturnToken() {
        // Arrange
        UserDetails userDetails = new BlogUserDetails(new User());
        when(jwtService.buildToken(any(), any(), any())).thenReturn("token123");

        // Act
        String token = authService.generateToken(userDetails);

        // Assert
        assertThat(token).isEqualTo("token123");
    }

    @Test
    public void validateToken_shouldReturnUserDetails() {
        // Arrange
        String token = "token123";
        String email = "john.smith@example.com";
        User user = User.builder()
                .email(email)
                .build();
        UserDetails userDetails = new BlogUserDetails(user);

        when(jwtService.extractSubject(token)).thenReturn(email);
        when(userDetailsService.loadUserByUsername(email)).thenReturn(userDetails);

        // Act
        UserDetails result = authService.validateToken(token);

        // Assert
        assertThat(result.getUsername()).isEqualTo(email);
    }
}