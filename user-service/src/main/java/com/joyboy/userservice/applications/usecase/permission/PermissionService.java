package com.joyboy.userservice.applications.usecase.permission;

import com.joyboy.commonservice.common.exceptions.DataNotFoundException;
import com.joyboy.commonservice.common.exceptions.ExistsException;
import com.joyboy.userservice.presentation.dtos.PermissionDTO;
import com.joyboy.userservice.domain.entities.models.Permission;
import com.joyboy.userservice.presentation.response.PermissionPageResponse;
import com.joyboy.userservice.infrastructure.repositories.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PermissionService implements IPermission {
    private final PermissionRepository permissionRepository;

    @Transactional
    @Override
    public Permission createPermission(PermissionDTO permissionDTO) throws ExistsException {
        Optional<Permission> permissionOptional = permissionRepository.findByNamePermissions(permissionDTO.getName_permission());
        if (permissionOptional.isPresent()) {
            throw new ExistsException("Permission with name " + permissionDTO.getName_permission() + " already exists");
        }

        Permission permission = Permission.builder()
                .namePermissions(permissionDTO.getName_permission())
                .description(permissionDTO.getDescription_permission())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return permissionRepository.save(permission);
    }

    @Override
    public PermissionPageResponse getAllPermission(Integer pageNumber, Integer pageSize, String sortBy, String dir) {
        Sort sort =  dir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<Permission> permissionPage = permissionRepository.findAll(pageable);

        return getPermissionPageResponse(pageNumber, pageSize, permissionPage);
    }

    private PermissionPageResponse getPermissionPageResponse(Integer pageNumber, Integer pageSize, Page<Permission> permissionsPage) {
        List<Permission> permissions = permissionsPage.getContent();

        if(permissions.isEmpty()) {
            return new PermissionPageResponse(null, 0, 0, 0, 0, true);
        }

        List<Permission> productList = new ArrayList<>(permissions);

        int totalPages = permissionsPage.getTotalPages();
        int totalElements = (int) permissionsPage.getTotalElements();
        boolean isLast = permissionsPage.isLast();

        return new PermissionPageResponse(productList, pageNumber, pageSize, totalElements, totalPages, isLast);
    }

    @Override
    public Permission getPermissionById(long id) throws DataNotFoundException {
        return permissionRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Permission with id " + id + " not found"));
    }

    @Transactional
    @Override
    public Permission updatePermission(Long permissionId, PermissionDTO permissionDTO) throws DataNotFoundException {
        Permission extistingPermission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new DataNotFoundException("Permission with id " + permissionId + " not found"));

        Optional.ofNullable(permissionDTO.getName_permission())
                .filter(name_permission -> !name_permission.isEmpty())
                .ifPresent(extistingPermission::setNamePermissions);

        Optional.ofNullable(permissionDTO.getDescription_permission())
                .filter(description_permission -> !description_permission.isEmpty())
                .ifPresent(extistingPermission::setDescription);

        extistingPermission.setUpdatedAt(LocalDateTime.now());
        return permissionRepository.save(extistingPermission);
    }

    @Transactional
    @Override
    public void deletePermission(Long permissionId) throws DataNotFoundException {
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new DataNotFoundException("Permission not found"));

        int count = permissionRepository.countRolePermissionsByPermissionId(permissionId);

        if (count > 0) {
            throw new IllegalStateException("Cannot delete permission because it is assigned to a role");
        }

        permissionRepository.delete(permission);
    }
}
