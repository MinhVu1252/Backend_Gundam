package com.joyboy.userservice.presentation.controllers.openpublic;

import com.joyboy.commonservice.common.exceptions.ValidationException;
import com.joyboy.commonservice.common.response.ResponseObject;
import com.joyboy.userservice.presentation.dtos.LogoutDTO;
import com.joyboy.userservice.presentation.dtos.RefreshTokenDTO;
import com.joyboy.userservice.presentation.dtos.UserLoginDTO;
import com.joyboy.userservice.presentation.dtos.ValidateTokenDTO;
import com.joyboy.userservice.domain.entities.models.Token;
import com.joyboy.userservice.domain.entities.models.User;
import com.joyboy.userservice.presentation.response.LoginResponse;
import com.joyboy.userservice.presentation.response.LogoutResponse;
import com.joyboy.userservice.presentation.response.ValidateTokenResponse;
import com.joyboy.userservice.applications.usecase.authen.IAuthenticate;
import com.joyboy.userservice.applications.usecase.token.ITokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

/**
 * AuthenController handles authentication and token management operations, including login,
 * token validation, logout, and token refresh.
 *
 * <p>This controller provides endpoints for user authentication, token validation,
 * and managing session tokens. It supports login with credentials, validation of JWT tokens,
 * logout functionality, and refreshing expired tokens.</p>
 *
 * <p><strong>Endpoints:</strong></p>
 * <ul>
 *     <li>{@code POST /auth/login}: Authenticate a user and issue a new JWT token.</li>
 *     <li>{@code POST /auth/validate-token}: Validate an existing JWT token.</li>
 *     <li>{@code POST /auth/logout}: Invalidate the current session and logout the user.</li>
 *     <li>{@code POST /auth/refreshToken}: Refresh an expired JWT token using a refresh token.</li>
 * </ul>
 *
 * <p><strong>Exceptions:</strong></p>
 * <ul>
 *     <li>{@link ValidationException}: Thrown when validation errors occur during token refresh.</li>
 *     <li>{@link Exception}: General exception for other errors during authentication and token operations.</li>
 * </ul>
 *
 * @author ThaoDien
 * @version 1.0
 */

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenController {
    private final IAuthenticate authenticate;
    private final ITokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<ResponseObject> login(
            @Valid @RequestBody UserLoginDTO userLoginDTO,
            HttpServletRequest request
    ) throws Exception {
        String token = authenticate.login(userLoginDTO);
        String userAgent = request.getHeader("User-Agent");
        User userDetail = authenticate.getUserDetailsFromToken(token);
        Token jwtToken = tokenService.addToken(userDetail, token, isMobileDevice(userAgent));

        LoginResponse loginResponse = LoginResponse.builder()
                .token(jwtToken.getToken())
                .refreshToken(jwtToken.getRefreshToken())
                .username(userDetail.getUsername())
                .roles(userDetail.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                .id(userDetail.getId())
                .build();
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Login successfully")
                .data(loginResponse)
                .status(HttpStatus.OK)
                .build());
    }

    private boolean isMobileDevice(String userAgent) {
        // Kiểm tra User-Agent header để xác định thiết bị di động
        // Ví dụ đơn giản:
        return userAgent.toLowerCase().contains("mobile");
    }

    @PostMapping("/validate-token")
    public ResponseEntity<ValidateTokenResponse> validateToken(@RequestBody ValidateTokenDTO request) throws Exception {
        ValidateTokenResponse result = authenticate.validateToken(request);

        if (result.isValid()) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<LogoutResponse> logout(@RequestBody LogoutDTO logoutDTO) {
        try {
            authenticate.logout(logoutDTO);
            return ResponseEntity.ok(LogoutResponse.builder().message("Logout successful").build());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(LogoutResponse.builder().message("Logout failed: " + e.getMessage()).build());
        }
    }

    @PostMapping("/refreshToken")
    public ResponseEntity<ResponseObject> refreshToken(
            @Valid @RequestBody RefreshTokenDTO refreshTokenDTO,
            BindingResult result
    ) throws Exception {
        if(result.hasErrors()) {
            throw new ValidationException(result);
        }

        User userDetail = authenticate.getUserDetailsFromRefreshToken(refreshTokenDTO.getRefreshToken());
        Token jwtToken = tokenService.refreshToken(refreshTokenDTO.getRefreshToken(), userDetail);
        LoginResponse loginResponse = LoginResponse.builder()
                .token(jwtToken.getToken())
                .refreshToken(jwtToken.getRefreshToken())
                .username(userDetail.getUsername())
                .roles(userDetail.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                .id(userDetail.getId()).build();
        return ResponseEntity.ok().body(
                ResponseObject.builder()
                        .data(loginResponse)
                        .message("Refresh token successfully")
                        .status(HttpStatus.OK)
                        .build());

    }
}
