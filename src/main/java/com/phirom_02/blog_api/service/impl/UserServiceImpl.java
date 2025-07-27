package com.phirom_02.blog_api.service.impl;

import com.phirom_02.blog_api.domain.dtos.CreateUserDto;
import com.phirom_02.blog_api.domain.entities.User;
import com.phirom_02.blog_api.repository.UserRepository;
import com.phirom_02.blog_api.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Implementation of {@link UserService} responsible for managing user-related operations,
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    /**
     * Finds a user by their unique identifier.
     *
     * @param id the UUID of the user to retrieve
     * @return the found {@link User} entity
     * @throws EntityNotFoundException if the user with the given ID is not found
     */
    @Override
    public User findUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No user found with id: " + id));
    }

    /**
     * Creates and persists a new user in the database.
     *
     * @param dto the DTO containing user creation information
     * @return the saved {@link User} entity
     */
    @Override
    public User createUser(CreateUserDto dto) {
        User userToCreate = new User();
        userToCreate.setEmail(dto.getEmail());
        userToCreate.setPassword(dto.getPassword());
        userToCreate.setName(dto.getName());

        return userRepository.save(userToCreate);
    }
}
