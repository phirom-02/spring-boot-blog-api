package com.phirom_02.blog_api.security;

import com.phirom_02.blog_api.domain.entities.User;
import com.phirom_02.blog_api.service.AuthService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @InjectMocks
    JwtAuthenticationFilter jwtAuthenticationFilter;

    @Mock
    AuthService authService;
    @Mock
    HttpServletRequest request;
    @Mock
    HttpServletResponse response;
    @Mock
    FilterChain filterChain;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void doFilterInternal_shouldSetAuthenticationOnValidToken() throws Exception {
        // Arrange
        String token = "valid.token.here";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

        User user = User.builder()
                .id(UUID.randomUUID())
                .build();
        BlogUserDetails userDetails = new BlogUserDetails(user);
        when(authService.validateToken(token)).thenReturn(userDetails);


        Map<String, Object> attributes = new HashMap<>();
        doAnswer(invocation -> {
            String key = invocation.getArgument(0);
            Object value = invocation.getArgument(1);
            attributes.put(key, value);
            return null;
        }).when(request).setAttribute(anyString(), any());

        when(request.getAttribute(anyString())).thenAnswer(invocation -> {
            String key = invocation.getArgument(0);
            return attributes.get(key);
        });

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        // Assert
        assertThat(authentication).isNotNull();
        assertThat(authentication.getPrincipal()).isEqualTo(userDetails);
        assertThat(request.getAttribute("userId")).isEqualTo(user.getId());
    }

    @Test
    public void doFilterInternal_shouldNotSetAuthenticationOnInvalidToken() throws Exception {
        // Arrange
        String token = "invalid.token.here";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(authService.validateToken(token)).thenThrow(new RuntimeException("Invalid token"));

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
    }

    @Test
    public void doFilterInternal_shouldDoNothingOnNoTokenHeader() throws Exception {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn(null);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }
}