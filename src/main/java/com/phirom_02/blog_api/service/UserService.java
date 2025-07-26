package com.phirom_02.blog_api.service;

import com.phirom_02.blog_api.domain.dtos.CreateUserDto;
import com.phirom_02.blog_api.domain.entities.User;

import java.util.UUID;

public interface UserService {

    User findUserById(UUID id);

    User createUser(CreateUserDto user);
}
