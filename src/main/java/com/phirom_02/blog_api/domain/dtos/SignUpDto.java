package com.phirom_02.blog_api.domain.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for user sign-up request.
 * This object is used to pass sign up data to the service layer for processing.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SignUpDto {

    private String email;
    private String password;
    private String confirmPassword;
    private String name;
}
