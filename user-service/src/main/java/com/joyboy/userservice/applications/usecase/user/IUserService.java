package com.joyboy.userservice.applications.usecase.user;

import com.joyboy.commonservice.common.exceptions.DataNotFoundException;
import com.joyboy.commonservice.common.exceptions.InvalidPasswordException;
import com.joyboy.userservice.presentation.dtos.ResetPasswordDTO;
import com.joyboy.userservice.presentation.dtos.UpdateUserDTO;
import com.joyboy.userservice.presentation.dtos.UserRegisterDTO;
import com.joyboy.userservice.domain.entities.models.User;
import com.joyboy.userservice.presentation.response.UserPageResponse;
import org.springframework.web.multipart.MultipartFile;

/**
 * Interface for user management operations.
 * <p>
 * This interface defines methods for creating, updating, retrieving, and managing user accounts.
 * Implementations of this interface handle various user-related operations, including password reset, profile management, and account status changes.
 * </p>
 *
 * <p><strong>Methods:</strong></p>
 * <ul>
 *     <li>{@link #createUser(UserRegisterDTO userDTO)}: Creates a new user with the provided registration details.
 *         Throws {@link Exception} if an error occurs during user creation.</li>
 *     <li>{@link #updateUser(Long userId, UpdateUserDTO updateUserDTO, String token)}: Updates the details of an existing user specified by the user ID.
 *         Throws {@link DataNotFoundException} if the user is not found.</li>
 *     <li>{@link #findAllUsers(Integer pageNumber, Integer pageSize, String sortBy, String dir)}: Retrieves a paginated list of users with optional sorting.
 *         Returns a {@link UserPageResponse} containing the user data.</li>
 *     <li>{@link #resetPassword(Long userId, ResetPasswordDTO resetPasswordDTO, String token)}: Resets the password for the specified user.
 *         Throws {@link InvalidPasswordException} if the provided old password is incorrect, and {@link DataNotFoundException} if the user is not found.</li>
 *     <li>{@link #blockOrEnable(Long userId, Boolean active)}: Blocks or enables a user account based on the provided status.
 *         Throws {@link DataNotFoundException} if the user is not found.</li>
 *     <li>{@link #changeProfileImage(Long userId, String imageName, String token)}: Changes the profile image for the specified user.
 *         Throws {@link Exception} if an error occurs while updating the profile image.</li>
 *     <li>{@link #forgotPassword(String email)}: Initiates the password recovery process for the user associated with the provided email.
 *         Throws {@link DataNotFoundException} if no user is associated with the email.</li>
 *     <li>{@link #verifyOtpAndResetPassword(String email, String otp, String newPassword)}: Verifies the OTP and resets the password for the user associated with the provided email.
 *         Throws {@link DataNotFoundException} if the user is not found.</li>
 *     <li>{@link #uploadAvatar(Long userId, MultipartFile file, String token)}: Uploads a new avatar image for the user.
 *         Throws {@link Exception} if an error occurs during the file upload.</li>
 *     <li>{@link #profileUser(Long userId, String token)}: Retrieves the profile information for the specified user.
 *         Throws {@link Exception} if an error occurs while retrieving the profile.</li>
 * </ul>
 *
 * <p><strong>Exceptions:</strong></p>
 * <ul>
 *     <li>{@link Exception}: Thrown for general errors during user operations.</li>
 *     <li>{@link DataNotFoundException}: Thrown if a user is not found for the given ID or email.</li>
 *     <li>{@link InvalidPasswordException}: Thrown if the provided old password is incorrect during password reset.</li>
 * </ul>
 *
 * @see UserRegisterDTO
 * @see UpdateUserDTO
 * @see ResetPasswordDTO
 * @see UserPageResponse
 * @see User
 * @see MultipartFile
 *
 * @author ThaoDien
 * @version 1.0
 */
public interface IUserService {
    /**
     * Creates a new user with the provided registration details.
     *
     * @param userDTO the user registration data transfer object containing the registration details.
     * @return the created user.
     * @throws Exception if an error occurs during user creation.
     */
    User createUser(UserRegisterDTO userDTO) throws Exception;

    /**
     * Updates the details of an existing user specified by the user ID.
     *
     * @param userId the ID of the user to be updated.
     * @param updateUserDTO the data transfer object containing updated user details.
     * @return the updated user.
     * @throws DataNotFoundException if the user is not found.
     */
    User updateUser(Long userId, UpdateUserDTO updateUserDTO, String token) throws Exception;

    /**
     * Retrieves a paginated list of users with optional sorting.
     *
     * @param pageNumber the page number to retrieve.
     * @param pageSize the number of users per page.
     * @param sortBy the field to sort by.
     * @param dir the direction of sorting (e.g., ascending or descending).
     * @return a {@link UserPageResponse} containing the paginated user data.
     */
    UserPageResponse findAllUsers(Integer pageNumber, Integer pageSize, String sortBy, String dir);

    /**
     * Resets the password for the specified user.
     *
     * @param userId the ID of the user whose password is to be reset.
     * @param resetPasswordDTO the data transfer object containing the new password and other reset details.
     * @throws InvalidPasswordException if the provided old password is incorrect.
     * @throws DataNotFoundException if the user is not found.
     */
    void resetPassword(Long userId, ResetPasswordDTO resetPasswordDTO, String token)
            throws Exception;

    /**
     * Blocks or enables a user account based on the provided status.
     *
     * @param userId the ID of the user to be blocked or enabled.
     * @param active flag indicating whether to block or enable the user account.
     * @throws DataNotFoundException if the user is not found.
     */
    void blockOrEnable(Long userId, Boolean active) throws DataNotFoundException;

    /**
     * Changes the profile image for the specified user.
     *
     * @param userId the ID of the user whose profile image is to be changed.
     * @param imageName the name of the new profile image.
     * @throws Exception if an error occurs while updating the profile image.
     */
    void changeProfileImage(Long userId, String imageName, String token) throws Exception;

    /**
     * Initiates the password recovery process for the user associated with the provided email.
     *
     * @param email the email of the user for whom the password recovery process is to be initiated.
     * @throws DataNotFoundException if no user is associated with the email.
     */
    void forgotPassword(String email) throws DataNotFoundException;

    /**
     * Verifies the OTP and resets the password for the user associated with the provided email.
     *
     * @param email the email of the user requesting password reset.
     * @param otp the OTP to verify.
     * @param newPassword the new password to set.
     * @return {@code true} if the OTP is verified and the password is reset successfully, {@code false} otherwise.
     * @throws DataNotFoundException if the user is not found.
     */
    boolean verifyOtpAndResetPassword(String email, String otp, String newPassword) throws DataNotFoundException;

    /**
     * Uploads a new avatar image for the user.
     *
     * @param userId the ID of the user for whom the avatar is to be uploaded.
     * @param file the avatar image file to upload.
     * @throws Exception if an error occurs during the file upload.
     */
    void uploadAvatar(Long userId, MultipartFile file, String token) throws Exception;

    /**
     * Retrieves the profile information for the specified user.
     *
     * @param userId the ID of the user whose profile information is to be retrieved.
     * @return the user's profile information.
     * @throws Exception if an error occurs while retrieving the profile.
     */
    User profileUser(Long userId, String token) throws Exception;

    User getUserById(Long userId) throws DataNotFoundException;
}

