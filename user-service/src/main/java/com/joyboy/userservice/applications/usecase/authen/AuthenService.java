package com.joyboy.userservice.applications.usecase.authen;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.joyboy.commonservice.common.exceptions.DataNotFoundException;
import com.joyboy.commonservice.common.exceptions.ExpiredTokenException;
import com.joyboy.userservice.infrastructure.config.jwt.JwtTokenUtils;
import com.joyboy.userservice.presentation.dtos.LogoutDTO;
import com.joyboy.userservice.presentation.dtos.UserLoginDTO;
import com.joyboy.userservice.presentation.dtos.ValidateTokenDTO;
import com.joyboy.userservice.domain.entities.models.Token;
import com.joyboy.userservice.domain.entities.models.User;
import com.joyboy.userservice.presentation.response.ValidateTokenResponse;
import com.joyboy.userservice.infrastructure.repositories.UserRepository;
import com.joyboy.userservice.applications.usecase.cached.CachedService;
import com.joyboy.userservice.applications.usecase.redis.IRedisService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthenService implements IAuthenticate{
    private final UserRepository userRepository;
    private final JwtTokenUtils jwtTokenUtil;
    private final AuthenticationManager authenticationManager;
    private final IRedisService redisService;
    private final ObjectMapper objectMapper;
    private final CachedService cachedService;
    private static final Logger logger = LoggerFactory.getLogger(AuthenService.class);

    @Override
    public String login(UserLoginDTO userLoginDTO) throws Exception {
        User user = cachedService.findUserByEmail(userLoginDTO.getEmail());

        if (!user.isActive()) {
            throw new DataNotFoundException("User is not active");
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userLoginDTO.getEmail(), userLoginDTO.getPassword()));

        String token = jwtTokenUtil.generateToken(user);

        return token;
    }

    @Override
    public User getUserDetailsFromToken(String token) throws Exception {
        if (jwtTokenUtil.isTokenExpired(token)) {
            throw new ExpiredTokenException("Token is expired");
        }

        String subject = jwtTokenUtil.getSubject(token);
        return userRepository.findByUsername(subject)
                .or(() -> userRepository.findByEmail(subject))
                .orElseThrow(() -> new DataNotFoundException("User not found"));
    }

    @Override
    public User getUserDetailsFromRefreshToken(String refreshToken) throws Exception {
        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new IllegalArgumentException("Refresh token cannot be null or empty");
        }

        Token existingToken = getTokenFromRefreshToken(refreshToken);
        return getUserDetailsFromToken(existingToken.getToken());
    }

    private Token getTokenFromRefreshToken(String refreshToken) throws ExpiredTokenException {
        String tokenKey = redisService.getToken("refreshToken:" + refreshToken);
        if (tokenKey == null) {
            throw new ExpiredTokenException("Refresh token is expired or revoked");
        }

        String rawToken = redisService.getToken(tokenKey);
        if (rawToken == null) {
            throw new ExpiredTokenException("Refresh token is expired or revoked");
        }

        Token existingToken;
        try {
            existingToken = objectMapper.readValue(rawToken, Token.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse token JSON", e);
        }

        if (existingToken.isRevoked() || existingToken.getRefreshExpirationDate().isBefore(LocalDateTime.now())) {
            throw new ExpiredTokenException("Refresh token is expired or revoked");
        }

        return existingToken;
    }

    @Override
    public ValidateTokenResponse validateToken(ValidateTokenDTO validateTokenDTO) {
        String token = validateTokenDTO.getToken();
        boolean isValid = true;
        List<String> roles = null;

        try {
            roles = jwtTokenUtil.extractClaim(token, claims -> {
                List<?> rawRoles = claims.get("roles", List.class);
                return rawRoles.stream()
                        .map(Object::toString)
                        .collect(Collectors.toList());
            });

            jwtTokenUtil.verifyToken(token, false);
        } catch (Exception e) {
            isValid = false;
        }

        return ValidateTokenResponse.builder().valid(isValid).role(roles).build();
    }

    @Override
    public void logout(LogoutDTO logoutDTO) {
        String token = logoutDTO.getToken();

        try {
            Token existingToken = getTokenFromJwtId(token);
            if (existingToken == null) {
                return;
            }

            updateTokenAsRevoked(existingToken, token);

        } catch (ExpiredJwtException e) {
            // log.info("Token already expired");
        } catch (JsonProcessingException e) {
            // log.error("Failed to serialize token", e);
        }
    }

    private Token getTokenFromJwtId(String token) throws JsonProcessingException {
        String jwtId = jwtTokenUtil.extractJwtId(token);
        String tokenKey = redisService.getToken("jwtId:" + jwtId);
        if (tokenKey == null) return null;

        String rawToken = redisService.getToken(tokenKey);
        if (rawToken == null) return null;

        return objectMapper.readValue(rawToken, Token.class);
    }

    public void updateTokenAsRevoked(Token existingToken, String token) throws JsonProcessingException {
        Date expiryTime = jwtTokenUtil.extractClaim(token, Claims::getExpiration);

        existingToken.setExpired(true);
        existingToken.setRevoked(true);
        existingToken.setRefreshExpirationDate(LocalDateTime.now());
        existingToken.setExpirationDate(expiryTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());

        String updatedTokenJson = objectMapper.writeValueAsString(existingToken);

        String tokenKey = "token:" + existingToken.getUserId() + ":" + existingToken.getJwtId();
        redisService.saveTokenToRedis(tokenKey, updatedTokenJson, (int) Duration.between(LocalDateTime.now(), existingToken.getExpirationDate()).getSeconds());
        redisService.saveTokenToRedis("jwtId:" + existingToken.getJwtId(), tokenKey, (int) Duration.between(LocalDateTime.now(), existingToken.getExpirationDate()).getSeconds());
        redisService.saveTokenToRedis("refreshToken:" + existingToken.getRefreshToken(), tokenKey, (int) Duration.between(LocalDateTime.now(), existingToken.getExpirationDate()).getSeconds());
        redisService.saveTokenToRedis("tokenValue:" + existingToken.getToken(), tokenKey, (int) Duration.between(LocalDateTime.now(), existingToken.getExpirationDate()).getSeconds());
    }
}
