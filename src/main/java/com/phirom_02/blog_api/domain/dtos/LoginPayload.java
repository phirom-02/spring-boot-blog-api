package com.phirom_02.blog_api.domain.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for user login request.
 * Contains the necessary fields for authenticating a user.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginPayload {
    private String email;
    private String password;
}
