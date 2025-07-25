package com.phirom_02.blog_api.service;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;

public interface JwtService {

    String extractSubject(String token);

    String buildToken(UserDetails userDetails, Map<String, Object> extraClaims, Long tokenExpiration);
}
