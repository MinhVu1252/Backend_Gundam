package com.joyboy.userservice.applications.usecase.permission;

import com.joyboy.commonservice.common.exceptions.DataNotFoundException;
import com.joyboy.userservice.presentation.dtos.PermissionDTO;
import com.joyboy.userservice.domain.entities.models.Permission;
import com.joyboy.userservice.presentation.response.PermissionPageResponse;

/**
 * Interface for permission-related operations.
 * <p>
 * This interface defines methods for managing permissions within the application, including
 * creating, retrieving, updating, and deleting permissions. Implementations of this interface
 * are responsible for handling permission management logic, such as persisting permission data
 * and performing necessary operations to manage permissions effectively.
 * </p>
 *
 * <p><strong>Methods:</strong></p>
 * <ul>
 *     <li>{@link #createPermission(PermissionDTO permissionDTO)}: Creates a new permission based on the provided
 *         {@link PermissionDTO} and returns the created {@link Permission} object.</li>
 *     <li>{@link #getAllPermission(Integer pageNumber, Integer pageSize, String sortBy, String dir)}: Retrieves a
 *         paginated and sorted list of all permissions, returning a {@link PermissionPageResponse} object.</li>
 *     <li>{@link #getPermissionById(long id)}: Retrieves a permission by its ID. If no permission is found with the
 *         specified ID, a {@link DataNotFoundException} is thrown.</li>
 *     <li>{@link #updatePermission(Long permissionId, PermissionDTO permissionDTO)}: Updates the details of an
 *         existing permission based on the provided {@link PermissionDTO}. If no permission is found with the
 *         specified ID, a {@link DataNotFoundException} is thrown.</li>
 *     <li>{@link #deletePermission(Long permissionId)}: Deletes a permission by its ID. If no permission is found with
 *         the specified ID, a {@link DataNotFoundException} is thrown.</li>
 * </ul>
 *
 * <p><strong>Exceptions:</strong></p>
 * <ul>
 *     <li>{@link Exception}: Thrown for general exceptions that occur during permission operations.</li>
 *     <li>{@link DataNotFoundException}: Thrown if a permission with the specified ID is not found.</li>
 * </ul>
 *
 * @see PermissionDTO
 * @see PermissionPageResponse
 * @see Permission
 * @see DataNotFoundException
 *
 * @author ThaoDien
 * @version 1.0
 */
public interface IPermission {
    /**
     * Creates a new permission.
     *
     * @param permissionDTO the data transfer object containing the details of the permission to be created.
     * @return the newly created {@link Permission} object.
     * @throws Exception if an error occurs during the creation of the permission.
     */
    Permission createPermission(PermissionDTO permissionDTO) throws Exception;

    /**
     * Retrieves a paginated and sorted list of all permissions.
     *
     * @param pageNumber the page number to retrieve (zero-based index).
     * @param pageSize the number of permissions per page.
     * @param sortBy the field to sort the results by.
     * @param dir the direction of sorting ("asc" for ascending, "desc" for descending).
     * @return a {@link PermissionPageResponse} object containing the list of permissions.
     */
    PermissionPageResponse getAllPermission(Integer pageNumber, Integer pageSize, String sortBy, String dir);

    /**
     * Retrieves a permission by its ID.
     *
     * @param id the ID of the permission to retrieve.
     * @return the {@link Permission} object with the specified ID.
     * @throws DataNotFoundException if no permission is found with the given ID.
     */
    Permission getPermissionById(long id) throws DataNotFoundException;

    /**
     * Updates an existing permission.
     *
     * @param permissionId the ID of the permission to update.
     * @param permissionDTO the data transfer object containing the updated details of the permission.
     * @return the updated {@link Permission} object.
     * @throws DataNotFoundException if no permission is found with the given ID.
     */
    Permission updatePermission(Long permissionId, PermissionDTO permissionDTO) throws DataNotFoundException;

    /**
     * Deletes a permission by its ID.
     *
     * @param permissionId the ID of the permission to delete.
     * @throws DataNotFoundException if no permission is found with the given ID.
     */
    void deletePermission(Long permissionId) throws DataNotFoundException;
}

