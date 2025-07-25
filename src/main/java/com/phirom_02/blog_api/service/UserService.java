package com.phirom_02.blog_api.service;

import com.phirom_02.blog_api.domain.entities.User;

import java.util.UUID;

public interface UserService {

    User findUserByEmail(String email);

    User findUserById(UUID id);
}
