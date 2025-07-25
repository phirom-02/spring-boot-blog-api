package com.phirom_02.blog_api.service;

import com.phirom_02.blog_api.domain.dtos.SignUpDto;
import org.springframework.security.core.userdetails.UserDetails;

public interface AuthService {
    UserDetails register(SignUpDto dto);

    UserDetails authenticate(String username, String password);

    String generateToken(UserDetails userDetails);

    UserDetails validateToken(String token);
}
