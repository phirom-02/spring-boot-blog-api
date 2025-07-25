package com.phirom_02.blog_api.service.impl;

import com.phirom_02.blog_api.service.AuthService;
import com.phirom_02.blog_api.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access_token_expiration}")
    private Long accessTokenExpiration;

    @Value("${jwt.refresh_token_expiration}")
    private Long refreshTokenExpiration;

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
