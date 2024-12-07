package com.joyboy.userservice.applications.usecase.authen;

import com.joyboy.userservice.presentation.dtos.LogoutDTO;
import com.joyboy.userservice.presentation.dtos.UserLoginDTO;
import com.joyboy.userservice.presentation.dtos.ValidateTokenDTO;
import com.joyboy.userservice.domain.entities.models.User;
import com.joyboy.userservice.presentation.response.ValidateTokenResponse;
import com.joyboy.userservice.domain.entities.models.Token;
import com.joyboy.userservice.presentation.dtos.RefreshTokenDTO;

/**
 * Interface for authentication-related operations.
 * <p>
 * This interface defines methods for user login, token validation, and logout operations.
 * Implementations of this interface are responsible for handling authentication logic,
 * including token management and user details retrieval.
 * </p>
 *
 * <p><strong>Methods:</strong></p>
 * <ul>
 *     <li>{@link #login(UserLoginDTO userLoginDTO)}: Authenticates a user with the provided login credentials and returns an authentication token.</li>
 *     <li>{@link #getUserDetailsFromToken(String token)}: Retrieves user details from the provided authentication token.</li>
 *     <li>{@link #getUserDetailsFromRefreshToken(String refreshToken)}: Retrieves user details from the provided refresh token.</li>
 *     <li>{@link #validateToken(ValidateTokenDTO validateTokenDTO)}: Validates the provided token and returns a response indicating its validity.</li>
 *     <li>{@link #logout(LogoutDTO logoutDTO)}: Logs out the user and invalidates their session or tokens.</li>
 * </ul>
 *
 * <p><strong>Exceptions:</strong></p>
 * <ul>
 *     <li>{@link Exception}: Thrown for general exceptions that occur during authentication operations.</li>
 * </ul>
 *
 * @see UserLoginDTO
 * @see ValidateTokenDTO
 * @see LogoutDTO
 * @see ValidateTokenResponse
 * @see User
 * @see Token
 * @see RefreshTokenDTO
 *
 * @author ThaoDien
 * @version 1.0
 */
public interface IAuthenticate {
    /**
     * Authenticates a user with the provided login credentials.
     *
     * @param userLoginDTO the user login data transfer object containing credentials.
     * @return the authentication token.
     * @throws Exception if an error occurs during the authentication process.
     */
    String login(UserLoginDTO userLoginDTO) throws Exception;

    /**
     * Retrieves user details from the provided authentication token.
     *
     * @param token the authentication token.
     * @return the user details associated with the token.
     * @throws Exception if an error occurs while retrieving user details from the token.
     */
    User getUserDetailsFromToken(String token) throws Exception;

    /**
     * Retrieves user details from the provided refresh token.
     *
     * @param refreshToken the refresh token.
     * @return the user details associated with the refresh token.
     * @throws Exception if an error occurs while retrieving user details from the refresh token.
     */
    User getUserDetailsFromRefreshToken(String refreshToken) throws Exception;

    /**
     * Validates the provided token.
     *
     * @param validateTokenDTO the data transfer object containing the token to be validated.
     * @return a response indicating whether the token is valid or not.
     * @throws Exception if an error occurs during token validation.
     */
    ValidateTokenResponse validateToken(ValidateTokenDTO validateTokenDTO) throws Exception;

    /**
     * Logs out the user and invalidates their session or tokens.
     *
     * @param logoutDTO the data transfer object containing logout details.
     * @throws Exception if an error occurs during the logout process.
     */
    void logout(LogoutDTO logoutDTO) throws Exception;
}
