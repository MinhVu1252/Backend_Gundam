package com.joyboy.userservice.presentation.controllers.internal;

import com.joyboy.commonservice.common.response.ResponseObject;
import com.joyboy.userservice.applications.usecase.user.IUserService;
import com.joyboy.userservice.domain.entities.models.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/internal/users")
@RequiredArgsConstructor
public class InternalAPI {
    private final IUserService userService;

    @GetMapping("/{userId}")
    public ResponseEntity<ResponseObject> getProfileUser(@PathVariable("userId") Long userId) {
        try {
            User user = userService.getUserById(userId);
            return ResponseEntity.ok().body(ResponseObject.builder()
                    .message("Get profile user successfully")
                    .status(HttpStatus.CREATED)
                    .data(user)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseObject.builder()
                            .message("Error to get profile: " + e.getMessage())
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .build());
        }
    }
}
