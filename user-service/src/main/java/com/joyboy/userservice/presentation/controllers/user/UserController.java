package com.joyboy.userservice.presentation.controllers.user;

import com.joyboy.commonservice.common.exceptions.DataNotFoundException;
import com.joyboy.commonservice.common.exceptions.InvalidPasswordException;
import com.joyboy.commonservice.common.exceptions.ValidationException;
import com.joyboy.commonservice.common.response.ResponseObject;
import com.joyboy.userservice.presentation.dtos.ResetPasswordDTO;
import com.joyboy.userservice.presentation.dtos.UpdateUserDTO;
import com.joyboy.userservice.domain.entities.models.User;
import com.joyboy.userservice.applications.usecase.user.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * UserController handles user-related operations including updating user details,
 * resetting passwords, uploading avatars, and retrieving user profiles.
 *
 * <p>This controller provides endpoints for managing user accounts. It supports updating
 * user details, resetting passwords, uploading profile avatars, and retrieving user profile information.</p>
 *
 * <p><strong>Endpoints:</strong></p>
 * <ul>
 *     <li>{@code PATCH /users/update/{userId}}: Update user details.</li>
 *     <li>{@code POST /users/change-password/{userId}}: Reset user password.</li>
 *     <li>{@code POST /users/uploads/{userId}}: Upload user avatar image.</li>
 *     <li>{@code GET /users/profile/{userId}}: Retrieve user profile information.</li>
 * </ul>
 *
 * <p><strong>Exceptions:</strong></p>
 * <ul>
 *     <li>{@link ValidationException}: Thrown when validation errors occur during user update or password reset.</li>
 *     <li>{@link InvalidPasswordException}: Thrown when the old password provided for resetting is invalid.</li>
 *     <li>{@link DataNotFoundException}: Thrown when the specified user is not found.</li>
 *     <li>{@link Exception}: General exception for other errors during user operations.</li>
 * </ul>
 *
 * @author ThaoDien
 * @version 1.0
 */

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final IUserService userService;

    @PatchMapping("/update/{userId}")
    public ResponseEntity<ResponseObject> updateUser(@Valid @RequestBody UpdateUserDTO updateUserDTO,
                                                     @PathVariable Long userId,
                                                     @RequestHeader("Authorization") String authorizationHeader,
                                                     BindingResult result) throws Exception {
        if(result.hasErrors()) {
            throw new ValidationException(result);
        }
        String extractedToken = authorizationHeader.substring(7);
        User updateUser = userService.updateUser(userId, updateUserDTO, extractedToken);
        return ResponseEntity.ok().body(
                ResponseObject.builder()
                        .message("Update user detail successfully")
                        .data(updateUser)
                        .status(HttpStatus.OK)
                        .build()
        );
    }

    @PostMapping("/change-password/{userId}")
    public ResponseEntity<ResponseObject> resetPassword(@Valid @RequestBody ResetPasswordDTO request,
                                                        BindingResult result,
                                                        @RequestHeader("Authorization") String authorizationHeader,
                                                        @PathVariable Long userId) {
        if(result.hasErrors()) {
            throw new ValidationException(result);
        }

        try {
            String extractedToken = authorizationHeader.substring(7);
            userService.resetPassword(userId, request, extractedToken);
            return ResponseEntity.ok(ResponseObject.builder()
                    .data(null)
                    .message("Reset password successfully")
                    .status(HttpStatus.OK)
                    .build());
        } catch (InvalidPasswordException e) {
            return ResponseEntity.ok(ResponseObject.builder()
                    .message("Invalid old password")
                    .data("")
                    .status(HttpStatus.BAD_REQUEST)
                    .build());
        } catch (DataNotFoundException e) {
            return ResponseEntity.ok(ResponseObject.builder()
                    .message("User not found")
                    .data("")
                    .status(HttpStatus.BAD_REQUEST)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.ok(ResponseObject.builder()
                    .message("An error occurred")
                    .data("")
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build());
        }
    }

    @PostMapping("/uploads/{userId}")
    public ResponseEntity<ResponseObject> uploadAvatarUser(
            @PathVariable("userId") Long userId,
            @RequestParam("file") MultipartFile file,
            @RequestHeader("Authorization") String authorizationHeader
    ) {

        try {
            String extractedToken = authorizationHeader.substring(7);
            userService.uploadAvatar(userId, file, extractedToken);
            return ResponseEntity.ok().body(ResponseObject.builder()
                    .message("Upload image avatar successfully")
                    .status(HttpStatus.CREATED)
                    .data(null)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseObject.builder()
                            .message("Error uploading image: " + e.getMessage())
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .build());
        }
    }

    @GetMapping("/profile/{userId}")
    public ResponseEntity<ResponseObject> getProfileUser( @PathVariable("userId") Long userId,
                                                          @RequestHeader("Authorization") String authorizationHeader) {
        try {
            String extractedToken = authorizationHeader.substring(7);
            User user = userService.profileUser(userId, extractedToken);
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
