package com.phirom_02.blog_api.service;

import org.springframework.security.core.userdetails.UserDetails;

public interface AuthService {
    String login(String username, String password);
//    String generateToken(UserDetails userDetails);
}
