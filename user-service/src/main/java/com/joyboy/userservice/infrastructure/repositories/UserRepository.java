package com.joyboy.userservice.infrastructure.repositories;

import com.joyboy.userservice.domain.entities.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for accessing {@link User} entities.
 * <p>
 * This repository provides methods for querying and modifying users in the database.
 * </p>
 *
 * <p><strong>Custom Queries:</strong></p>
 * <ul>
 *     <li>{@code existsByUsername(String username)}: Checks if a user with the specified username exists.</li>
 *     <li>{@code existsByEmail(String email)}: Checks if a user with the specified email exists.</li>
 *     <li>{@code findByEmail(String email)}: Retrieves a user by their email address.</li>
 *     <li>{@code findByUsername(String username)}: Retrieves a user by their username.</li>
 * </ul>
 *
 * <p><strong>Annotations:</strong></p>
 * <ul>
 *     <li>{@link Repository}: Indicates that this interface is a Spring Data repository.</li>
 * </ul>
 *
 * @author ThaoDien
 * @version 1.0
 */

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Checks if a user with the specified username exists.
     *
     * @param username the username to check.
     * @return {@code true} if a user with the specified username exists; {@code false} otherwise.
     */
    boolean existsByUsername(String username);

    /**
     * Checks if a user with the specified email exists.
     *
     * @param email the email to check.
     * @return {@code true} if a user with the specified email exists; {@code false} otherwise.
     */
    boolean existsByEmail(String email);

    /**
     * Retrieves a user by their email address.
     *
     * @param email the email address of the user.
     * @return an {@link Optional} containing the user with the specified email if found, or an empty {@link Optional} if no such user exists.
     */
    Optional<User> findByEmail(String email);

    /**
     * Retrieves a user by their username.
     *
     * @param username the username of the user.
     * @return an {@link Optional} containing the user with the specified username if found, or an empty {@link Optional} if no such user exists.
     */
    Optional<User> findByUsername(String username);
}
