package com.phirom_02.blog_api.service;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;

/**
 * Service interface responsible for handling operations related to JSON Web Tokens (JWT).
 * This interface includes methods for extracting information from JWTs and building new tokens.
 */
public interface JwtService {

    /**
     * Extracts the subject (typically the username or email) from a JWT token.
     * This method decodes the provided token and retrieves the subject information.
     *
     * @param token the JWT token from which to extract the subject
     * @return the subject (e.g., username or email) of the token
     */
    String extractSubject(String token);

    /**
     * Builds a new JWT token for the given user with optional additional claims.
     * This method generates a token that includes the user details and any extra claims passed in the parameters.
     * The token is created with a specified expiration time.
     *
     * @param userDetails     the user details (usually containing username/email and roles)
     * @param extraClaims     a map of additional claims to include in the token
     * @param tokenExpiration the expiration time for the token in milliseconds
     * @return a newly generated JWT token as a string
     */
    String buildToken(UserDetails userDetails, Map<String, Object> extraClaims, Long tokenExpiration);
}
