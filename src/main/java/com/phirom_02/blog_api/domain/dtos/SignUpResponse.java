package com.phirom_02.blog_api.domain.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO representing a sign-up response, containing sign-up information.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SignUpResponse {
    private UUID id;
    private String email;
    private String name;
}
