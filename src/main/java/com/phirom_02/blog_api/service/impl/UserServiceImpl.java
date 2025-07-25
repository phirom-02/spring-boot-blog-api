package com.phirom_02.blog_api.service.impl;

import com.phirom_02.blog_api.domain.entities.User;
import com.phirom_02.blog_api.repository.UserRepository;
import com.phirom_02.blog_api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
