package com.phirom_02.blog_api.service.impl;

import com.phirom_02.blog_api.domain.dtos.CreateUserDto;
import com.phirom_02.blog_api.domain.entities.User;
import com.phirom_02.blog_api.repository.UserRepository;
import com.phirom_02.blog_api.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User findUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No user found with id: " + id));
    }

    @Override
    public User createUser(CreateUserDto dto) {
        User userToCreate = new User();
        userToCreate.setEmail(dto.getEmail());
        userToCreate.setPassword(dto.getPassword());
        userToCreate.setName(dto.getName());

        return userRepository.save(userToCreate);
    }
}
