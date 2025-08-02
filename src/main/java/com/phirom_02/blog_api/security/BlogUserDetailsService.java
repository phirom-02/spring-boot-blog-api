package com.phirom_02.blog_api.security;

import com.phirom_02.blog_api.domain.entities.User;
import com.phirom_02.blog_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


/**
 * Custom implementation of {@link UserDetailsService} for loading user details.
 * This service fetches user details from the database using the {@link UserRepository},
 * and converts the {@link User} entity into a {@link BlogUserDetails} object.
 */
@Service
@RequiredArgsConstructor
public class BlogUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Loads the user by their username (email in this case).
     * If the user is not found, a {@link UsernameNotFoundException} is thrown.
     *
     * @param email the email of the user to load
     * @return the user details wrapped in {@link BlogUserDetails}
     * @throws UsernameNotFoundException if no user is found with the given email
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email " + email));

        return new BlogUserDetails(user);
    }
}
