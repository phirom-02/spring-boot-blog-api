package com.phirom_02.blog_api.domain.dtos;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SignUpPayload {

    @NotNull(message = "Email is required")
    @NotBlank(message = "Email is required")
    @Email(message = "Email is invalid")
    private String email;

    @NotNull(message = "Password is required")
    @NotEmpty(message = "Password is required")
    @Size(min = 8, message = "Password length should be at least {8} characters")
    private String password;

    @NotNull(message = "Confirm Password is required")
    @NotEmpty(message = "Confirm Password is required")
    private String confirmPassword;

    @NotNull(message = "Name is required")
    @NotBlank(message = "Name is required")
    private String name;
}
