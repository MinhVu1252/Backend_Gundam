package com.joyboy.userservice.presentation.controllers.admin;

import com.joyboy.commonservice.common.exceptions.DataNotFoundException;
import com.joyboy.commonservice.common.exceptions.ValidationException;
import com.joyboy.commonservice.common.response.ResponseObject;
import com.joyboy.userservice.presentation.dtos.PermissionDTO;
import com.joyboy.userservice.domain.entities.models.Permission;
import com.joyboy.userservice.presentation.response.PermissionPageResponse;
import com.joyboy.userservice.applications.usecase.permission.IPermission;
import com.joyboy.userservice.applications.utils.PageConstant;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

/**
 * PermissionController handles administrative requests related to permissions.
 *
 * <p>This controller provides endpoints to create, retrieve, update, and delete permissions.</p>
 *
 * <p><strong>Endpoints:</strong></p>
 * <ul>
 *     <li>{@code POST /admin/permissions/add-permission}: Create a new permission.</li>
 *     <li>{@code GET /admin/permissions/{id}}: Retrieve a permission by its ID.</li>
 *     <li>{@code GET /admin/permissions}: Retrieve a paginated list of all permissions with sorting options.</li>
 *     <li>{@code PATCH /admin/permissions/{id}}: Update an existing permission by its ID.</li>
 *     <li>{@code DELETE /admin/permissions/{id}}: Delete a permission by its ID.</li>
 * </ul>
 *
 * <p><strong>Exceptions:</strong></p>
 * <ul>
 *     <li>{@link ValidationException}: Thrown when validation errors occur while creating or updating a permission.</li>
 *     <li>{@link DataNotFoundException}: Thrown when a permission cannot be found by its ID.</li>
 * </ul>
 *
 * @author ThaoDien
 * @version 1.0
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/permissions")
public class PermissionController {
    private final IPermission permissionService;

    @PostMapping("/add-permission")
    public ResponseEntity<ResponseObject> createPermission(@Valid @RequestBody PermissionDTO permissionDTO,
                                                           BindingResult bindingResult) throws Exception {
        if(bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }

        Permission permission = permissionService.createPermission(permissionDTO);
        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.CREATED)
                .data(permission)
                .message("Create permission successful")
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject> getPermissionById(@PathVariable Long id) throws DataNotFoundException {
        Permission permission = permissionService.getPermissionById(id);
        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.OK)
                .data(permission)
                .message("Get information permission successful")
                .build());
    }

    @GetMapping("")
    public ResponseEntity<ResponseObject> getAllPermissions(
            @RequestParam(defaultValue = PageConstant.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(defaultValue = PageConstant.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(defaultValue = PageConstant.SORT_BY) String permissionId,
            @RequestParam(defaultValue = PageConstant.SORT_DIR) String sortDir
    ) {
        PermissionPageResponse permissionPageResponse = permissionService.getAllPermission(pageNumber, pageSize, permissionId, sortDir);
        return ResponseEntity.ok(ResponseObject.builder()
                .data(permissionPageResponse)
                .message("Get list of permissions information successfully")
                .status(HttpStatus.OK)
                .build());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ResponseObject> updatePermission(@Valid
                                                           @PathVariable Long id,
                                                           @RequestBody PermissionDTO permissionDTO) throws DataNotFoundException {
        Permission permissionUpdate = permissionService.updatePermission(id, permissionDTO);
        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.OK)
                .data(permissionUpdate)
                .message("Update permission successful")
                .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseObject> deleteProduct(@PathVariable("id") Long id) throws Exception {
        permissionService.deletePermission(id);
        return ResponseEntity.ok(ResponseObject.builder()
                .data(null)
                .message(String.format("Permission with id = %d deleted successfully", id))
                .status(HttpStatus.OK)
                .build());
    }
}
