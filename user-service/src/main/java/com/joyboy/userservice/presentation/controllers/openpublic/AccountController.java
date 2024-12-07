package com.joyboy.userservice.presentation.controllers.openpublic;

import com.joyboy.commonservice.common.exceptions.DataNotFoundException;
import com.joyboy.commonservice.common.exceptions.ValidationException;
import com.joyboy.commonservice.common.response.ResponseObject;
import com.joyboy.userservice.presentation.dtos.UserRegisterDTO;
import com.joyboy.userservice.domain.entities.models.User;
import com.joyboy.userservice.presentation.request.PasswordResetRequest;
import com.joyboy.userservice.presentation.request.PasswordResetVerifyRequest;
import com.joyboy.userservice.applications.usecase.user.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

/**
 * AccountController handles account-related operations such as user registration,
 * password reset, and user profile retrieval.
 *
 * <p>This controller provides endpoints for user account management, including:
 * registering new users, handling forgotten passwords, and resetting passwords.</p>
 *
 * <p><strong>Endpoints:</strong></p>
 * <ul>
 *     <li>{@code POST /account/register}: Register a new user account.</li>
 *     <li>{@code POST /account/forgot-password}: Request a password reset by sending an OTP to the user's email.</li>
 *     <li>{@code POST /account/reset-password}: Verify the OTP and reset the password.</li>
 *     <li>{@code GET /account/user/{userId}}: Retrieve user profile information by user ID.</li>
 * </ul>
 *
 * <p><strong>Exceptions:</strong></p>
 * <ul>
 *     <li>{@link ValidationException}: Thrown when validation errors occur during user registration.</li>
 *     <li>{@link DataNotFoundException}: Thrown when a requested user or email is not found.</li>
 * </ul>
 *
 * @author ThaoDien
 * @version 1.0
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/account")
public class AccountController {
    private final IUserService userService;

    @PostMapping("/register")
    public ResponseEntity<ResponseObject> createUsers(@Valid @RequestBody UserRegisterDTO userDTO,
                                                       BindingResult result) throws Exception {
        if(result.hasErrors()) {
            throw new ValidationException(result);
        }

        User user = userService.createUser(userDTO);
        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.CREATED)
                .message("Account registration successful")
                .build());
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody PasswordResetRequest request) throws DataNotFoundException {
        userService.forgotPassword(request.getEmail());
        return ResponseEntity.ok("OTP sent to your email.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> verifyOtpAndResetPassword(@RequestBody PasswordResetVerifyRequest request) throws DataNotFoundException {
        boolean success = userService.verifyOtpAndResetPassword(request.getEmail(), request.getOtp(), request.getNewPassword());
        if (success) {
            return ResponseEntity.ok("Password reset successful.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid OTP or OTP has expired.");
        }
    }

}
