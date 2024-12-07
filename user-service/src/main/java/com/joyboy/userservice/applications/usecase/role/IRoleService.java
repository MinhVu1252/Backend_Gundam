package com.joyboy.userservice.applications.usecase.role;

import com.joyboy.commonservice.common.exceptions.DataNotFoundException;
import com.joyboy.userservice.presentation.dtos.PermissionDTO;
import com.joyboy.userservice.presentation.dtos.RoleDTO;
import com.joyboy.userservice.domain.entities.models.Role;

import java.util.List;
import java.util.Set;

/**
 * Interface for role management operations.
 * <p>
 * This interface defines methods for managing roles, including creating, retrieving, updating,
 * and deleting roles, as well as adding or removing permissions from roles. Implementations
 * of this interface handle the business logic related to role management.
 * </p>
 *
 * <p><strong>Methods:</strong></p>
 * <ul>
 *     <li>{@link #createRole(RoleDTO roleDTO)}: Creates a new role based on the provided role data transfer object (DTO).</li>
 *     <li>{@link #getAllRoles()}: Retrieves a list of all roles.</li>
 *     <li>{@link #getRoleById(long id)}: Retrieves a role by its ID. Throws {@link DataNotFoundException} if the role is not found.</li>
 *     <li>{@link #deletePermissionInRole(long roleId, long permissionId)}: Removes a permission from a role specified by its ID.
 *         Throws {@link DataNotFoundException} if the role or permission is not found.</li>
 *     <li>{@link #addPermissionToRole(Long roleId, Set<PermissionDTO>)}: Adds a set of permissions to a role specified
 *         by its ID. Throws {@link Exception} for general errors during the process.</li>
 *     <li>{@link #updateRole(Long roleId, RoleDTO roleDTO)}: Updates the details of an existing role with the provided role DTO.
 *         Throws {@link DataNotFoundException} if the role is not found.</li>
 *     <li>{@link #deleteRole(Long roleId)}: Deletes a role specified by its ID. Throws {@link DataNotFoundException} if the role is not found.</li>
 * </ul>
 *
 * <p><strong>Exceptions:</strong></p>
 * <ul>
 *     <li>{@link DataNotFoundException}: Thrown if a role or permission is not found during operations.</li>
 *     <li>{@link Exception}: Thrown for general errors that occur during role management operations.</li>
 * </ul>
 *
 * @see RoleDTO
 * @see Role
 * @see PermissionDTO
 * @see DataNotFoundException
 *
 * @author ThaoDien
 * @version 1.0
 */
public interface IRoleService {
    /**
     * Creates a new role based on the provided role data transfer object (DTO).
     *
     * @param roleDTO the role DTO containing the details of the role to be created.
     * @return the created role.
     * @throws Exception if an error occurs during the creation process.
     */
    Role createRole(RoleDTO roleDTO) throws Exception;

    /**
     * Retrieves a list of all roles.
     *
     * @return a list of all roles.
     */
    List<Role> getAllRoles();

    /**
     * Retrieves a role by its ID.
     *
     * @param id the ID of the role to be retrieved.
     * @return the role with the specified ID.
     * @throws DataNotFoundException if the role is not found.
     */
    Role getRoleById(long id) throws DataNotFoundException;

    /**
     * Removes a permission from a role specified by its ID.
     *
     * @param roleId the ID of the role from which the permission will be removed.
     * @param permissionId the ID of the permission to be removed.
     * @throws DataNotFoundException if the role or permission is not found.
     */
    void deletePermissionInRole(long roleId, long permissionId) throws DataNotFoundException;

    /**
     * Adds a set of permissions to a role specified by its ID.
     *
     * @param roleId the ID of the role to which permissions will be added.
     * @param permissionsDTO a set of permission DTOs to be added to the role.
     * @return the updated role with the added permissions.
     * @throws Exception if an error occurs during the addition process.
     */
    Role addPermissionToRole(Long roleId, Set<PermissionDTO> permissionsDTO) throws Exception;

    /**
     * Updates the details of an existing role with the provided role DTO.
     *
     * @param roleId the ID of the role to be updated.
     * @param roleDTO the role DTO containing the updated details.
     * @return the updated role.
     * @throws DataNotFoundException if the role is not found.
     */
    Role updateRole(Long roleId, RoleDTO roleDTO) throws DataNotFoundException;

    /**
     * Deletes a role specified by its ID.
     *
     * @param roleId the ID of the role to be deleted.
     * @throws DataNotFoundException if the role is not found.
     */
    void deleteRole(Long roleId) throws DataNotFoundException;
}

