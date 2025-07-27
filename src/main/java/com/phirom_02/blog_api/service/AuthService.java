package com.phirom_02.blog_api.service;

import com.phirom_02.blog_api.domain.dtos.SignUpDto;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Service interface for handling authentication and user management operations.
 */
public interface AuthService {

    /**
     * Registers a new user based on the provided sign-up data.
     * This method processes the sign-up data (DTO), validates it, and creates a new user in the system.
     *
     * @param dto the sign-up data transfer object containing the user's registration details
     * @return the registered user's details as a {@link UserDetails} object
     */
    UserDetails register(SignUpDto dto);

    /**
     * Authenticates a user by validating their username and password.
     * This method verifies the user's credentials and returns the user's details if successful.
     *
     * @param username the username of the user attempting to log in
     * @param password the password provided by the user for authentication
     * @return the authenticated user's details as a {@link UserDetails} object
     */
    UserDetails authenticate(String username, String password);

    /**
     * Generates a JWT token for the given user details.
     * This method creates a token that can be used for subsequent API requests to verify the user's identity.
     *
     * @param userDetails the authenticated user's details
     * @return a JWT token as a {@link String}
     */
    String generateToken(UserDetails userDetails);

    /**
     * Validates the provided JWT token and retrieves the associated user details.
     * This method verifies the authenticity of the token and returns the user details if the token is valid.
     *
     * @param token the JWT token to validate
     * @return the user details associated with the valid token as a {@link UserDetails} object
     */
    UserDetails validateToken(String token);
}
