package com.phirom_02.blog_api.service.impl;

import com.phirom_02.blog_api.domain.entities.User;
import com.phirom_02.blog_api.security.BlogUserDetails;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
class JwtServiceImplTest {

    private JwtServiceImpl jwtService;

    private final Long JWT_ACCESS_TOKEN_EXPIRES_IN_SECONDS = 86400000L;

    private final String jwtSecret = "a2V5MTIzNDU2Nzg5MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTIzNDU2Nzg5MDEyMw";

    private BlogUserDetails testUser;

    @BeforeEach
    void setUp() {
        jwtService = new JwtServiceImpl();
        jwtService.jwtSecret = jwtSecret;

        User user = User.builder()
                .id(UUID.randomUUID())
                .name("John Smith")
                .email("john.smith@example.com")
                .password("@#password")
                .build();

        testUser = new BlogUserDetails(user);
    }


    @Test
    public void extractSubject_shouldReturnSubject() {
        // Arrange
        String token = jwtService.buildToken(testUser, new HashMap<>(), JWT_ACCESS_TOKEN_EXPIRES_IN_SECONDS);

        // Act
        String subject = jwtService.extractSubject(token);

        // Assert
        assertThat("john.smith@example.com").isEqualTo(subject);
    }

    @Test
    public void buildToken_buildTokenWithExtraClaims() {
        // Arrange
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("userId", testUser.getId());

        // Act
        String token = jwtService.buildToken(testUser, extraClaims, 1000L * 60 * 5);
        String userId = jwtService.extractClaim(token, claims -> claims.get("userId", String.class));

        // Assert
        assertThat(userId).isEqualTo(testUser.getId().toString());
    }

    @Test
    public void buildToken_shouldReturnValidExpirationDate() {
        // Arrange
        String token = jwtService.buildToken(testUser, new HashMap<>(), JWT_ACCESS_TOKEN_EXPIRES_IN_SECONDS);

        // Act
        Date expirationDate = jwtService.extractClaim(token, Claims::getExpiration);

        // Assert
        assertThat(expirationDate).isAfter(new Date());
    }
}