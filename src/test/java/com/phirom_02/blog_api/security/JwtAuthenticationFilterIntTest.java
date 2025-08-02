package com.phirom_02.blog_api.security;

import com.phirom_02.blog_api.IntegrationTest;
import com.phirom_02.blog_api.domain.entities.User;
import com.phirom_02.blog_api.service.AuthService;
import com.phirom_02.blog_api.util.TestDataHelper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@Testcontainers
@Import(TestDataHelper.class)
@Transactional
@AutoConfigureMockMvc
class JwtAuthenticationFilterIntTest extends IntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16:9");

    @Autowired
    TestDataHelper testDataHelper;

    @Autowired
    AuthService authService;

    @Autowired
    JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    MockMvc mockMvc;

    @Test
    @Rollback
    public void doFilterInternal_shouldSetUserIdOnValidToken() throws Exception {
        // Arrange
        User user = testDataHelper.createUser("John Smith", "john.smith@example.com");
        UserDetails userDetails = new BlogUserDetails(user);
        String token = authService.generateToken(userDetails);

        // Act & Assert
        mockMvc.perform(get("/test/secure")
                        .header("Authorization", "Bearer " + token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Authenticated as: " + user.getId()));
    }

    @Test
    public void doFilterInternal_shouldNotSetUserIdOnInvalidToken() throws Exception {
        mockMvc.perform(get("/test/secure")
                        .header("Authorization", "Bearer invalid")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}