package com.phirom_02.blog_api.mappers;

import com.phirom_02.blog_api.domain.dtos.CreateUserDto;
import com.phirom_02.blog_api.domain.dtos.SignUpDto;
import com.phirom_02.blog_api.domain.dtos.SignUpPayload;
import com.phirom_02.blog_api.domain.entities.User;
import com.phirom_02.blog_api.security.BlogUserDetails;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AuthMapper {
    SignUpDto toSignUpDto(SignUpPayload signUpPayload);

    CreateUserDto toCreateUserDto(SignUpDto signUpDto);

    BlogUserDetails toUserDetails(User user);
}
