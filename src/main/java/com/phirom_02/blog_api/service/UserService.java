package com.phirom_02.blog_api.service;

import com.phirom_02.blog_api.domain.entities.User;

import java.util.Optional;

public interface UserService {

    Optional<User> findUserByEmail(String email);
}
