package com.joyboy.userservice.presentation.response;

import com.joyboy.userservice.domain.entities.models.Permission;

import java.util.List;

public record PermissionPageResponse(List<Permission> permissions,
                                     Integer pageNumber,
                                     Integer pageSize,
                                     int totalElements,
                                     int totalPages,
                                     boolean isLast)  {
}
