package com.joyboy.userservice.applications.usecase.token;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.joyboy.userservice.domain.entities.models.Token;
import com.joyboy.userservice.domain.entities.models.User;

/**
 * Interface for token management operations.
 * <p>
 * This interface defines methods for managing authentication tokens, including adding and refreshing tokens.
 * Implementations of this interface handle the logic related to token creation and refreshing.
 * </p>
 *
 * <p><strong>Methods:</strong></p>
 * <ul>
 *     <li>{@link #addToken(User user, String token, boolean isMobileDevice)}: Adds a new token for the specified user.
 *         The token can be associated with either a web or mobile device, as indicated by the {@code isMobileDevice} flag.
 *         Throws {@link JsonProcessingException} if an error occurs while processing JSON data.</li>
 *     <li>{@link #refreshToken(String refreshToken, User user)}: Refreshes an existing token using the provided refresh token
 *         and user information. Returns the newly generated token. Throws {@link Exception} for general errors during the process.</li>
 * </ul>
 *
 * <p><strong>Exceptions:</strong></p>
 * <ul>
 *     <li>{@link JsonProcessingException}: Thrown if an error occurs while processing JSON data during token addition.</li>
 *     <li>{@link Exception}: Thrown for general errors that occur during token refreshing.</li>
 * </ul>
 *
 * @see Token
 * @see User
 *
 * @author ThaoDien
 * @version 1.0
 */
public interface ITokenService {
    /**
     * Adds a new token for the specified user.
     *
     * @param user the user to whom the token will be added.
     * @param token the token to be added.
     * @param isMobileDevice flag indicating whether the token is for a mobile device or not.
     * @return the created token.
     * @throws JsonProcessingException if an error occurs while processing JSON data.
     */
    Token addToken(User user, String token, boolean isMobileDevice) throws JsonProcessingException;

    /**
     * Refreshes an existing token using the provided refresh token and user information.
     *
     * @param refreshToken the refresh token used to generate a new token.
     * @param user the user for whom the token will be refreshed.
     * @return the newly generated token.
     * @throws Exception if an error occurs during the token refreshing process.
     */
    Token refreshToken(String refreshToken, User user) throws Exception;
}

