package com.phirom_02.blog_api.service.impl;

import com.phirom_02.blog_api.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    @Override
    public String login(String username, String password) {

        return null;
    }

    private UserDetails authenticate(String username, String password) {
        return null;
    }

    private String generateToken(UserDetails userDetails) {
        return "";
    }
}
