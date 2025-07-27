package com.phirom_02.blog_api.service;

import com.phirom_02.blog_api.domain.dtos.CreateUserDto;
import com.phirom_02.blog_api.domain.entities.User;

import java.util.UUID;


/**
 * Service interface for handling user-related operations,
 */
public interface UserService {

    /**
     * Retrieves a user by their unique identifier.
     *
     * @param id the UUID of the user to find
     * @return the found {@link User} entity
     */
    User findUserById(UUID id);

    /**
     * Creates a new user based on the provided information.
     *
     * @param user the data required to create a user
     * @return the created {@link User} entity
     */
    User createUser(CreateUserDto user);
}
