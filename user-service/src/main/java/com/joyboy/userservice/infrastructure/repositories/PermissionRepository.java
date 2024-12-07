package com.joyboy.userservice.infrastructure.repositories;

import com.joyboy.userservice.domain.entities.models.Permission;
import com.joyboy.userservice.domain.entities.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for accessing {@link Permission} entities.
 * <p>
 * This repository provides methods for querying and modifying permissions in the database.
 * </p>
 *
 * <p><strong>Custom Queries:</strong></p>
 * <ul>
 *     <li>{@link #findByNamePermissions(String name)}: Retrieves a permission by its name.</li>
 *     <li>{@link #getPermissionsByRole(Role role)}: Retrieves a list of permissions associated with a given role.</li>
 *     <li>{@link #countRolePermissionsByPermissionId(Long permissionId)}: Counts the number of roles associated with a given permission.</li>
 *     <li>{@link #countRolePermission(Long roleId, Long permissionId)}: Counts the number of times a permission is assigned to a specific role.</li>
 *     <li>{@link #deleteFromRolePermission(Long roleId, Long permissionId)}: Deletes the association of a permission from a specific role.</li>
 * </ul>
 *
 * <p><strong>Annotations:</strong></p>
 * <ul>
 *     <li>{@link Repository}: Indicates that this interface is a Spring Data repository.</li>
 *     <li>{@link Modifying}: Indicates that the query modifies data.</li>
 *     <li>{@link Transactional}: Specifies that the method should be executed within a transaction.</li>
 *     <li>{@link Query}: Defines a custom SQL or JPQL query for the method.</li>
 * </ul>
 *
 * @author ThaoDien
 * @version 1.0
 */
@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    /**
     * Finds a permission by its name.
     *
     * @param name the name of the permission.
     * @return an Optional containing the permission if found, otherwise empty.
     */
    Optional<Permission> findByNamePermissions(String name);

    /**
     * Retrieves a list of permissions associated with a given role.
     *
     * @param role the role for which permissions are to be retrieved.
     * @return a list of permissions associated with the role.
     */
    List<Permission> getPermissionsByRole(Role role);

    /**
     * Counts the number of roles associated with a given permission.
     *
     * @param permissionId the ID of the permission.
     * @return the count of roles associated with the permission.
     */
    @Query(value = "SELECT COUNT(1) FROM role_permission WHERE permission_id = :permissionId LIMIT 1", nativeQuery = true)
    int countRolePermissionsByPermissionId(@Param("permissionId") Long permissionId);

    /**
     * Counts the number of times a permission is assigned to a specific role.
     *
     * @param roleId the ID of the role.
     * @param permissionId the ID of the permission.
     * @return the count of times the permission is assigned to the role.
     */
    @Query(value = "SELECT COUNT(*) FROM role_permission WHERE role_id = :roleId AND permission_id = :permissionId", nativeQuery = true)
    int countRolePermission(@Param("roleId") Long roleId, @Param("permissionId") Long permissionId);

    /**
     * Deletes the association of a permission from a specific role.
     *
     * @param roleId the ID of the role.
     * @param permissionId the ID of the permission.
     */
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM role_permission WHERE role_id = :roleId AND permission_id = :permissionId", nativeQuery = true)
    void deleteFromRolePermission(@Param("roleId") Long roleId, @Param("permissionId") Long permissionId);
}
