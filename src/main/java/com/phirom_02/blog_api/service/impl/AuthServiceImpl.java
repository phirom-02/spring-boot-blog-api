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

    @Override
    public UserDetails register(SignUpDto dto) {
        String password = dto.getPassword();
        String confirmPassword = dto.getConfirmPassword();
        if (!password.equals(confirmPassword)) {
            throw new IllegalArgumentException("Password and confirmation password do not match");
        }
        CreateUserDto userToCreate = authMapper.toCreateUserDto(dto);
        userToCreate.setPassword(passwordEncoder.encode(password));
        User createdUser = userService.createUser(userToCreate);
        BlogUserDetails userDetails = new BlogUserDetails(createdUser);
        return userDetails;
    }

    @Override
    public UserDetails authenticate(String username, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        return userDetailsService.loadUserByUsername(username);
    }

    @Override
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return jwtService.buildToken(userDetails, claims, accessTokenExpiration);
    }

    @Override
    public UserDetails validateToken(String token) {
        String email = jwtService.extractSubject(token);
        return userDetailsService.loadUserByUsername(email);
    }
}
