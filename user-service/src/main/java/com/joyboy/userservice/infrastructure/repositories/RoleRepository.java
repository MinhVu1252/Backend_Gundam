package com.joyboy.userservice.infrastructure.repositories;

import com.joyboy.userservice.domain.entities.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for accessing {@link Role} entities.
 * <p>
 * This repository provides methods for querying and modifying roles in the database.
 * </p>
 *
 * <p><strong>Custom Queries:</strong></p>
 * <ul>
 *     <li>{@code findByName(String roleName)}: Retrieves a role by its name.</li>
 * </ul>
 *
 * <p><strong>Annotations:</strong></p>
 * <ul>
 *     <li>{@link Repository}: Indicates that this interface is a Spring Data repository.</li>
 *     <li>{@link Query}: Defines a custom JPQL query for the method.</li>
 * </ul>
 *
 * @author ThaoDien
 * @version 1.0
 */

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    /**
     * Finds a role by its name.
     *
     * @param roleName the name of the role.
     * @return the role with the specified name, or {@code null} if no such role exists.
     */
    @Query("SELECT r FROM Role r WHERE r.nameRole = :roleName")
    Role findByName(String roleName);
}
