package com.phirom_02.blog_api.security;

import com.phirom_02.blog_api.domain.entities.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * Custom implementation of {@link UserDetails} for Spring Security.
 * This class wraps a {@link User} entity and provides the necessary details
 * for authentication and authorization within the Spring Security framework.
 */
@Getter
@RequiredArgsConstructor
public class BlogUserDetails implements UserDetails {
    private final User user;

    /**
     * Returns the authorities granted to the user. In this case, a simple role "ROLE_USER".
     *
     * @return a list of granted authorities for the user
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    /**
     * Returns the password of the user.
     *
     * @return the user's password
     */
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    /**
     * Returns the username of the user, which is the user's email in this case.
     *
     * @return the user's email as the username
     */
    @Override
    public String getUsername() {
        return user.getEmail();
    }

    /**
     * Indicates whether the account is expired.
     *
     * @return true, as accounts are not expired by default
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Indicates whether the account is locked.
     *
     * @return true, as accounts are not locked by default
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Indicates whether the user's credentials are expired.
     *
     * @return true, as credentials are not expired by default
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Indicates whether the user is enabled.
     *
     * @return true, as users are enabled by default
     */
    @Override
    public boolean isEnabled() {
        return true;
    }

    /**
     * Returns the unique identifier of the user.
     *
     * @return the user's ID
     */
    public UUID getId() {
        return user.getId();
    }
}
