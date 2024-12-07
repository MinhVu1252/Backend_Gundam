package com.joyboy.userservice.applications.usecase.role;

import com.joyboy.commonservice.common.exceptions.DataNotFoundException;
import com.joyboy.commonservice.common.exceptions.ExistsException;
import com.joyboy.userservice.presentation.dtos.PermissionDTO;
import com.joyboy.userservice.presentation.dtos.RoleDTO;
import com.joyboy.userservice.domain.entities.models.Permission;
import com.joyboy.userservice.domain.entities.models.Role;
import com.joyboy.userservice.infrastructure.repositories.PermissionRepository;
import com.joyboy.userservice.infrastructure.repositories.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RoleService implements IRoleService {
   private final RoleRepository roleRepository;
   private final PermissionRepository permissionRepository;

   @Transactional
    @Override
    public Role createRole(RoleDTO roleDTO) throws Exception {
        Set<Permission> validPermissions = new HashSet<>();

        for (Permission permission : roleDTO.getPermissions()) {
            Optional<Permission> existingPermission = permissionRepository.findById(permission.getId());
            if (existingPermission.isPresent()) {
                validPermissions.add(existingPermission.get());
            } else {
                throw new DataNotFoundException("Permission with ID " + permission.getId() + " does not exist");
            }
        }

        Role role = Role.builder()
                .nameRole(roleDTO.getRole_name())
                .description(roleDTO.getRole_description())
                .permissions(validPermissions)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return roleRepository.save(role);
    }

    @Override
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    @Override
    public Role getRoleById(long id) throws DataNotFoundException {

        return roleRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Role with ID " + id + " does not exist"));
    }

    @Override
    public void deletePermissionInRole(long roleId, long permissionId) throws DataNotFoundException {
        int count = permissionRepository.countRolePermission(roleId, permissionId);

        if (count == 0) {
            throw new DataNotFoundException("Permission with ID " + permissionId + " is not assigned to Role with ID " + roleId);
        }

        permissionRepository.deleteFromRolePermission(roleId, permissionId);
    }

    @Transactional
    @Override
    public Role addPermissionToRole(Long roleId,  Set<PermissionDTO> permissionsDTO) throws Exception {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new DataNotFoundException("Role with ID " + roleId + " does not exist"));

        for (PermissionDTO permissionDTO : permissionsDTO) {
            Optional<Permission> permissionOptional = permissionRepository.findByNamePermissions(permissionDTO.getName_permission());
            if (permissionOptional.isEmpty()) {
                throw new DataNotFoundException("Permission with name " + permissionDTO.getName_permission() + " does not exist");
            }

            Permission permission = permissionOptional.get();

            if (role.getPermissions().contains(permission)) {
                throw new ExistsException("Permission with name " + permission.getNamePermissions() + " already exists in the role");
            }

            role.getPermissions().add(permission);
        }
        role.setUpdatedAt(LocalDateTime.now());

        return roleRepository.save(role);
    }

    @Transactional
    @Override
    public Role updateRole(Long roleId, RoleDTO roleDTO) throws DataNotFoundException {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new DataNotFoundException("Role with ID " + roleId + " does not exist"));

        Optional.ofNullable(roleDTO.getRole_name())
                .filter(role_name -> !role_name.isEmpty())
                .ifPresent(role::setNameRole);

        Optional.ofNullable(roleDTO.getRole_description())
                .filter(role_description -> !role_description.isEmpty())
                .ifPresent(role::setDescription);

        role.setUpdatedAt(LocalDateTime.now());

        return roleRepository.save(role);
    }

    @Transactional
    @Override
    public void deleteRole(Long roleId) throws DataNotFoundException {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new DataNotFoundException("Role with ID " + roleId + " does not exist"));

        List<Permission> permissions = permissionRepository.getPermissionsByRole(role);

        if(!permissions.isEmpty()) {
            throw new IllegalStateException("Cannot delete role with associated permissions");
        } else {
            roleRepository.deleteById(roleId);
        }
    }
}
