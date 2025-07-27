package com.phirom_02.blog_api.service.impl;

import com.phirom_02.blog_api.domain.dtos.CreateUserDto;
import com.phirom_02.blog_api.domain.dtos.SignUpDto;
import com.phirom_02.blog_api.domain.entities.User;
import com.phirom_02.blog_api.mappers.AuthMapper;
import com.phirom_02.blog_api.security.BlogUserDetails;
import com.phirom_02.blog_api.service.AuthService;
import com.phirom_02.blog_api.service.JwtService;
import com.phirom_02.blog_api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of the {@link AuthService} interface.
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserDetailsService userDetailsService;
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final AuthMapper authMapper;
    private final PasswordEncoder passwordEncoder;

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access_token_expiration}")
    private Long accessTokenExpiration;

    @Value("${jwt.refresh_token_expiration}")
    private Long refreshTokenExpiration;

    /**
     * Registers a new user by validating the sign-up data, hashing the password,
     * and creating the user in the system.
     *
     * @param dto the sign-up data transfer object containing user details.
     * @return the {@link UserDetails} object for the newly created user.
     * @throws IllegalArgumentException if the password and confirmation password do not match.
     */
    @Override
    public UserDetails register(SignUpDto dto) {
        String password = dto.getPassword();
        String confirmPassword = dto.getConfirmPassword();
        if (!password.equals(confirmPassword)) {
            throw new IllegalArgumentException("Password and confirmation password do not match");
        }
        CreateUserDto userToCreate = authMapper.toCreateUserDto(dto);
        userToCreate.setPassword(passwordEncoder.encode(password)); // Encrypting password
        User createdUser = userService.createUser(userToCreate);
        BlogUserDetails userDetails = new BlogUserDetails(createdUser);
        return userDetails;
    }

    /**
     * Authenticates a user by validating the username and password.
     * If valid, returns the {@link UserDetails} object of the user.
     *
     * @param username the username (usually email).
     * @param password the plain-text password to authenticate.
     * @return the {@link UserDetails} of the authenticated user.
     */
    @Override
    public UserDetails authenticate(String username, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        return userDetailsService.loadUserByUsername(username);
    }

    /**
     * Generates a JWT token for the authenticated user.
     *
     * @param userDetails the {@link UserDetails} object containing user information.
     * @return the generated JWT token.
     */
    @Override
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return jwtService.buildToken(userDetails, claims, accessTokenExpiration); // Build JWT token
    }

    /**
     * Validates the provided JWT token and retrieves the user details associated with it.
     *
     * @param token the JWT token to validate.
     * @return the {@link UserDetails} of the user associated with the token.
     */
    @Override
    public UserDetails validateToken(String token) {
        String email = jwtService.extractSubject(token); // Extract user info (email) from token
        return userDetailsService.loadUserByUsername(email);
    }
}
