package com.joyboy.userservice.presentation.response;

import com.joyboy.userservice.domain.entities.models.User;

import java.util.List;

public record UserPageResponse(List<User> users,
                               Integer pageNumber,
                               Integer pageSize,
                               int totalElements,
                               int totalPages,
                               boolean isLast) {
}
