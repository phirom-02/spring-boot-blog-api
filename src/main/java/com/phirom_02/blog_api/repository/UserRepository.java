package com.phirom_02.blog_api.repository;

import com.phirom_02.blog_api.domain.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for managing {@link User} entities.
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * Finds a user by their email address.
     *
     * @param email the email of the user
     * @return an {@link Optional} containing the user if found, or empty if no user exists with the given email
     */
    Optional<User> findByEmail(String email);
}
