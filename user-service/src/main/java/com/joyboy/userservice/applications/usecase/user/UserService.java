package com.joyboy.userservice.applications.usecase.user;

import com.joyboy.commonservice.common.exceptions.DataNotFoundException;
import com.joyboy.commonservice.common.exceptions.ExistsException;
import com.joyboy.commonservice.common.exceptions.ExpiredTokenException;
import com.joyboy.commonservice.common.exceptions.InvalidPasswordException;
import com.joyboy.commonservice.common.request.OtpRequest;
import com.joyboy.userservice.infrastructure.config.jwt.JwtTokenUtils;
import com.joyboy.userservice.presentation.dtos.ResetPasswordDTO;
import com.joyboy.userservice.presentation.dtos.UpdateUserDTO;
import com.joyboy.userservice.presentation.dtos.UserRegisterDTO;
import com.joyboy.userservice.domain.entities.models.Role;
import com.joyboy.userservice.domain.entities.models.User;
import com.joyboy.userservice.presentation.response.UserPageResponse;
import com.joyboy.userservice.infrastructure.repositories.RoleRepository;
import com.joyboy.userservice.infrastructure.repositories.UserRepository;
import com.joyboy.userservice.applications.utils.RoleConstant;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final WebClient.Builder webClientBuilder;
    private final JwtTokenUtils jwtTokenUtils;
    private final RedisTemplate<String, Object> redisTemplate;

    @Transactional
    @Override
    public User createUser(UserRegisterDTO userDTO) throws ExistsException {
        if(!userDTO.getEmail().isBlank() && userRepository.existsByEmail(userDTO.getEmail())) {
            throw new ExistsException("Email already exists");
        }

        if(!userDTO.getUsername().isBlank() && userRepository.existsByUsername(userDTO.getUsername())) {
            throw new ExistsException("Username already exists");
        }

        Set<Role> roles = new HashSet<>();
        Role role = roleRepository.findByName(RoleConstant.USER_ROLE);
        roles.add(role);

        User user = User.builder()
                .username(userDTO.getUsername())
                .email(userDTO.getEmail())
                .password(passwordEncoder.encode(userDTO.getPassword()))
                .roles(roles)
                .active(true)
                .create_at(LocalDateTime.now())
                .build();

        return userRepository.save(user);
    }

    @Override
    public User updateUser(Long userId, UpdateUserDTO updateUserDTO, String token) throws Exception {
        User userToken = getUserDetailsFromToken(token);

        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User not found"));

        if(!existingUser.getId().equals(userToken.getId())) {
            throw new DataNotFoundException("User ID in token does not match the user ID in the request.");
        }

        Optional.ofNullable(updateUserDTO.getUsername())
                .filter(name -> !name.isEmpty())
                .ifPresent( existingUser::setUsername);

        Optional.ofNullable(updateUserDTO.getFirstName())
                .filter(firstName -> !firstName.isEmpty())
                .ifPresent( existingUser::setFirstName);

        Optional.ofNullable(updateUserDTO.getLastName())
                .filter(lastName -> !lastName.isEmpty())
                .ifPresent(existingUser::setLastName);

        Optional.ofNullable(updateUserDTO.getEmail())
                .filter(email -> !email.isEmpty())
                .ifPresent(existingUser::setEmail);

        Optional.ofNullable(updateUserDTO.getPhoneNumber())
                .filter(phoneNumber -> !phoneNumber.isEmpty())
                .ifPresent(existingUser::setPhoneNumber);

        return userRepository.save(existingUser);
    }

    public User getUserDetailsFromToken(String token) throws Exception {
        if (jwtTokenUtils.isTokenExpired(token)) {
            throw new ExpiredTokenException("Token is expired");
        }

        Map<String, Object> claims = jwtTokenUtils.getClaimsFromToken(token);
        Integer userIdInteger = (Integer) claims.get("userId");

        if (userIdInteger == null) {
            throw new IllegalArgumentException("User ID not found in token");
        }

        Long userId = userIdInteger.longValue();

        Optional<User> user = userRepository.findById(userId);

        if (user.isPresent()) {
            return user.get();
        } else {
            throw new Exception("User not found");
        }
    }

    @Override
    public UserPageResponse findAllUsers(Integer pageNumber, Integer pageSize, String sortBy, String dir) {
        Sort sort = dir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<User> userPage = userRepository.findAll(pageable);
        return getUsersPageResponse(pageNumber, pageSize, userPage);
    }

    private UserPageResponse getUsersPageResponse(Integer pageNumber, Integer pageSize, Page<User> userPage) {
        List<User> users = userPage.getContent();

        if(users.isEmpty()) {
            return new UserPageResponse(null, 0, 0, 0, 0, true);
        }

        List<User> listUser = new ArrayList<>(users);

        int totalPages = userPage.getTotalPages();
        int totalElements = (int) userPage.getTotalElements();
        boolean isLast = userPage.isLast();

        return new UserPageResponse(listUser, pageNumber, pageSize, totalElements, totalPages, isLast);
    }

    @Override
    public void resetPassword(Long userId, ResetPasswordDTO resetPasswordDTO, String token) throws Exception {
        User userToken = getUserDetailsFromToken(token);

        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User not found"));

        if(!existingUser.getId().equals(userToken.getId())) {
            throw new DataNotFoundException("User ID in token does not match the user ID in the request.");
        }

        if (!passwordEncoder.matches(resetPasswordDTO.getOldPassword(), existingUser.getPassword())) {
            throw new InvalidPasswordException("Old password is incorrect");
        }

        String encodedPassword = passwordEncoder.encode(resetPasswordDTO.getNewPassword());
        existingUser.setPassword(encodedPassword);

        userRepository.save(existingUser);

        Set<String> userTokenKeys = redisTemplate.keys("token:" + userId + ":*");
        if (userTokenKeys != null) {
            redisTemplate.delete(userTokenKeys);
        }
    }

    @Override
    public void blockOrEnable(Long userId, Boolean active) throws DataNotFoundException {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User not found"));
        existingUser.setActive(active);
        userRepository.save(existingUser);
    }

    @Override
    public void changeProfileImage(Long userId, String imageName, String token) throws Exception {
        User userToken = getUserDetailsFromToken(token);

        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User not found"));

        if(!existingUser.getId().equals(userToken.getId())) {
            throw new DataNotFoundException("User ID in token does not match the user ID in the request.");
        }
        existingUser.setProfileImage(imageName);
        userRepository.save(existingUser);
    }

    @Override
    public void forgotPassword(String email) throws DataNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new DataNotFoundException("User not found"));

        String otp = generateOtp();
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(1);

        user.setOtp(passwordEncoder.encode(otp));
        user.setExpiryOtp(expiryTime);
        userRepository.save(user);

        sendOtpToNotificationService(user.getEmail(), otp);
    }

    @Override
    public boolean verifyOtpAndResetPassword(String email, String otp, String newPassword) throws DataNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new DataNotFoundException("User not found"));

        if (passwordEncoder.matches(otp, user.getOtp()) && user.getExpiryOtp().isAfter(LocalDateTime.now())) {
            user.setPassword(passwordEncoder.encode(newPassword));
            user.setOtp(null);  // Clear OTP
            user.setExpiryOtp(null);  // Clear OTP expiry time
            userRepository.save(user);
            return true;
        }

        return false;
    }

    @Override
    public void uploadAvatar(Long userId, MultipartFile file, String token) throws Exception {
        User userToken = getUserDetailsFromToken(token);

        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User not found"));

        if(!existingUser.getId().equals(userToken.getId())) {
            throw new DataNotFoundException("User ID in token does not match the user ID in the request.");
        }

        validateFileUpload(file);

        WebClient webClient = webClientBuilder.build();

        String imageAvatar = webClient.post()
                .uri("http://localhost:5002/api/v1/internal/files/upload")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData("file", file.getResource()))
                .retrieve()
                .bodyToMono(String.class)
                .block();


        changeProfileImage(existingUser.getId(), imageAvatar, token);
    }

    @Cacheable(value = "usersProfile", key = "#userId")
    @Override
    public User profileUser(Long userId, String token) throws Exception {
        User userToken = getUserDetailsFromToken(token);

        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User not found"));

        if(!existingUser.getId().equals(userToken.getId())) {
            throw new DataNotFoundException("User ID in token does not match the user ID in the request.");
        }

        return existingUser;
    }

    @Override
    public User getUserById(Long userId) throws DataNotFoundException {
        return userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User not found"));
    }

    private String generateOtp() {
        Random random = new Random();
        int randomNumber = random.nextInt(999999);
        StringBuilder output = new StringBuilder(Integer.toString(randomNumber));

        while (output.length() < 6) {
            output.insert(0, "0");
        }
        return output.toString();
    }

    private void sendOtpToNotificationService(String email, String otp) {
        WebClient webClient = webClientBuilder.build();
        String notificationServiceUrl = "http://localhost:5009/api/v1/notification/sendOtp";

        OtpRequest otpRequest = new OtpRequest(email, otp);
        webClient.post()
                .uri(notificationServiceUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(otpRequest))
                .retrieve()
                .bodyToMono(String.class)
                .block();  // Blocking for immediate response
    }

    private void validateFileUpload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Please select a file to upload");
        }

        if (file.getSize() > 10 * 1024 * 1024) {
            throw new IllegalArgumentException("File is too large! Maximum size is 10MB");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("File must be an image");
        }
    }
}
