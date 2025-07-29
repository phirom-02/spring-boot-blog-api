package com.phirom_02.blog_api.repository;

import com.phirom_02.blog_api.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//@Transactional
class UserRepositoryIntTest extends IntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16:9");

//    @Autowired
//    private UserRepository userRepository;

    @Test
    void connectionEstablished() {
        assertThat(postgres.isCreated()).isTrue();
        assertThat(postgres.isRunning()).isTrue();
    }

//    @BeforeEach
//    @Rollback
//    void setUp() {
//        User user = new User();
//        user.setEmail("john.smith@example.com");
//        user.setPassword("password");
//        user.setName("John Smith");
//        userRepository.save(user);
//    }
//
//    @Test
//    public void findByEmail_shouldRetrieveUserByEmail() {
//        String email = "john.smith@example.com";
//        Optional<User> user = userRepository.findByEmail(email);
//        assertThat(user).isPresent();
//        assertThat(user.get().getEmail()).isEqualTo(email);
//
//    }
}