package com.joyboy.userservice.presentation.controllers.admin;

import com.joyboy.commonservice.common.exceptions.DataNotFoundException;
import com.joyboy.commonservice.common.exceptions.ValidationException;
import com.joyboy.commonservice.common.response.ResponseObject;
import com.joyboy.userservice.presentation.dtos.PermissionDTO;
import com.joyboy.userservice.presentation.dtos.RoleDTO;
import com.joyboy.userservice.domain.entities.models.Role;
import com.joyboy.userservice.applications.usecase.role.IRoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * RoleController handles administrative requests related to roles.
 *
 * <p>This controller provides endpoints to create, update, retrieve, and delete roles, as well as manage permissions associated with roles.</p>
 *
 * <p><strong>Endpoints:</strong></p>
 * <ul>
 *     <li>{@code POST /admin/roles/add-role}: Create a new role.</li>
 *     <li>{@code POST /admin/roles/{roleId}/add-permission}: Add permissions to an existing role.</li>
 *     <li>{@code DELETE /admin/roles/{id}}: Delete a role by its ID.</li>
 *     <li>{@code PATCH /admin/roles/{id}}: Update an existing role by its ID.</li>
 *     <li>{@code GET /admin/roles}: Retrieve a list of all roles.</li>
 *     <li>{@code GET /admin/roles/{id}}: Retrieve a role by its ID.</li>
 *     <li>{@code DELETE /admin/roles/{roleId}/permission/{permissionId}}: Remove a permission from a role.</li>
 * </ul>
 *
 * <p><strong>Exceptions:</strong></p>
 * <ul>
 *     <li>{@link ValidationException}: Thrown when validation errors occur while creating or updating a role.</li>
 *     <li>{@link DataNotFoundException}: Thrown when a role cannot be found by its ID.</li>
 * </ul>
 *
 * @author ThaoDien
 * @version 1.0
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/roles")
public class RoleController {
    private final IRoleService roleService;

    @PostMapping("/add-role")
    public ResponseEntity<ResponseObject> createRole(@Valid @RequestBody RoleDTO roleDTO,
                                                     BindingResult bindingResult) throws Exception {
        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }

        Role role = roleService.createRole(roleDTO);
        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.CREATED)
                .data(role)
                .message("Create Role successful")
                .build());
    }

    @PostMapping("/{roleId}/add-permission")
    public ResponseEntity<ResponseObject> addPermissionToRole(@Valid
                                                              @PathVariable Long roleId,
                                                              @RequestBody Set<PermissionDTO> permissionDTO,
                                                              BindingResult bindingResult) throws Exception {
        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }

        Role role = roleService.addPermissionToRole(roleId, permissionDTO);
        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.CREATED)
                .data(role)
                .message("Add permission to role successful")
                .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseObject> deleteRole(@PathVariable Long id) throws Exception {
        roleService.deleteRole(id);
        return ResponseEntity.ok(
                ResponseObject.builder()
                        .status(HttpStatus.OK)
                        .message("Delete role successfully")
                        .build());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ResponseObject> updateRole(@Valid @PathVariable Long id,
                                                     @RequestBody RoleDTO roleDTO) throws Exception {
        Role updateRole = roleService.updateRole(id, roleDTO);
        return ResponseEntity.ok(
                ResponseObject.builder()
                        .status(HttpStatus.OK)
                        .message("Update role successfully")
                        .data(updateRole)
                        .build());
    }

    @GetMapping("")
    public ResponseEntity<ResponseObject> getAllRoles() {
        List<Role> role = roleService.getAllRoles();
        return ResponseEntity.ok(
                ResponseObject.builder()
                        .status(HttpStatus.OK)
                        .message("Get list role successfully")
                        .data(role)
                        .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject> getRoleById(@PathVariable Long id) throws DataNotFoundException {
        Role role = roleService.getRoleById(id);
        return ResponseEntity.ok(
                ResponseObject.builder()
                        .status(HttpStatus.OK)
                        .message("Get information role successfully")
                        .data(role)
                        .build());
    }

    @DeleteMapping("/{roleId}/permission/{permissionId}")
    public ResponseEntity<ResponseObject> deletePermissionFromRole(@PathVariable Long roleId, @PathVariable Long permissionId) throws Exception {
        roleService.deletePermissionInRole(roleId, permissionId);
        return ResponseEntity.ok(
                ResponseObject.builder()
                        .status(HttpStatus.OK)
                        .message("Delete permission from successfully")
                        .build());
    }
}
