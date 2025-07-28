package com.phirom_02.blog_api.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.phirom_02.blog_api.domain.dtos.CreateUserDto;
import com.phirom_02.blog_api.domain.entities.User;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.asm.TypeReference;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;

@Component
@RequiredArgsConstructor
public class UserDataLoader {

    private static final Logger log = LoggerFactory.getLogger(UserDataLoader.class);

    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void load() {
        if (userRepository.count() == 0) {
            String userJson = "/data/users.json";
            log.info("Loading user data from {}", userJson);
            try (InputStream inputStream = TypeReference.class.getResourceAsStream(userJson)) {
                CreateUserDtoList createUserDtoList = objectMapper.readValue(inputStream, CreateUserDtoList.class);
                List<User> users = createUserDtoList.users().stream().map(dto -> {
                    User user = new User();
                    user.setEmail(dto.getEmail());
                    String password = passwordEncoder.encode(dto.getPassword());
                    user.setPassword(password);
                    user.setName(dto.getName());
                    return user;
                }).toList();
                userRepository.saveAll(users);
            } catch (Exception e) {
                throw new RuntimeException("Failed to load users from " + userJson, e);
            }
        }
    }

    private record CreateUserDtoList(List<CreateUserDto> users) {
    }
}
