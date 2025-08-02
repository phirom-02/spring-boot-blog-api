package com.phirom_02.blog_api.mappers;

import com.phirom_02.blog_api.domain.dtos.CreateUserDto;
import com.phirom_02.blog_api.domain.dtos.SignUpDto;
import com.phirom_02.blog_api.domain.dtos.SignUpPayload;
import com.phirom_02.blog_api.domain.dtos.SignUpResponse;
import com.phirom_02.blog_api.domain.entities.User;
import com.phirom_02.blog_api.security.BlogUserDetails;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Mapper interface for converting between various authentication-related data objects.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AuthMapper {

    /**
     * Maps a {@link SignUpPayload} to a {@link SignUpDto}.
     * Converts the raw sign-up payload from the client to a DTO for further processing.
     *
     * @param signUpPayload the raw sign-up data
     * @return the corresponding {@link SignUpDto}
     */
    SignUpDto toSignUpDto(SignUpPayload signUpPayload);

    /**
     * Maps a {@link SignUpDto} to a {@link CreateUserDto}.
     * Converts the validated sign-up data (DTO) to a format suitable for user creation.
     *
     * @param signUpDto the validated sign-up data
     * @return the corresponding {@link CreateUserDto}
     */
    CreateUserDto toCreateUserDto(SignUpDto signUpDto);

    /**
     * Map a {@link UserDetails} to a {@link SignUpResponse}.
     *
     * @param userDetails the signed-up information of a new user
     * @return the corresponding {@link SignUpResponse}
     */
    default SignUpResponse userDetailsToSignUpResponse(UserDetails userDetails) {
        User user = ((BlogUserDetails) userDetails).getUser();

        return SignUpResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }
}
