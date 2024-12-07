package com.joyboy.userservice.applications.usecase.cached;

import com.joyboy.commonservice.common.exceptions.DataNotFoundException;
import com.joyboy.userservice.domain.entities.models.User;
import com.joyboy.userservice.infrastructure.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for managing cached user data.
 * <p>
 * This service handles user data retrieval with caching support. It utilizes Spring's caching abstraction
 * to cache results of user retrieval by email to improve performance and reduce database load.
 * </p>
 *
 * <p><strong>Dependencies:</strong></p>
 * <ul>
 *     <li>{@link UserRepository}: Repository for accessing user data from the database.</li>
 * </ul>
 *
 * @see UserRepository
 * @see User
 * @see DataNotFoundException
 *
 * @author ThaoDien
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class CachedService {
    private final UserRepository userRepository;

    /**
     * Retrieves a user by their email address with caching support.
     * <p>
     * This method retrieves user details from the cache if available. If not, it queries the database
     * and caches the result for subsequent requests. If the user is not found, a {@link DataNotFoundException} is thrown.
     * </p>
     *
     * @param email the email address of the user to retrieve.
     * @return the {@link User} object associated with the provided email.
     * @throws DataNotFoundException if no user is found with the specified email.
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "users", key = "#email")
    public User findUserByEmail(String email) throws DataNotFoundException {
        return userRepository.findByEmail(email).orElseThrow(() -> new DataNotFoundException("User not found"));
    }
}
