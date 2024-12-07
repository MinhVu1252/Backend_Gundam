package com.joyboy.userservice.applications.usecase.token;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.joyboy.commonservice.common.exceptions.ExpiredTokenException;
import com.joyboy.userservice.infrastructure.config.jwt.JwtTokenUtils;
import com.joyboy.userservice.domain.entities.models.Token;
import com.joyboy.userservice.domain.entities.models.User;
import com.joyboy.userservice.applications.usecase.redis.IRedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TokenService implements ITokenService {
    private static final int MAX_TOKENS = 3;

    @Value("${jwt.expiration}")
    private int expiration;

    @Value("${jwt.expiration-refresh-token}")
    private int expirationRefreshToken;

    private final JwtTokenUtils jwtTokenUtil;
    private final IRedisService redisService;
    private final ObjectMapper objectMapper;

    @Transactional
    @Override
    public Token addToken(User user, String token, boolean isMobileDevice) throws JsonProcessingException {
        String jwtId = jwtTokenUtil.extractJwtId(token);

        List<Token> userTokens = getTokensByUser(user);

        if (userTokens.size() >= MAX_TOKENS) {
            Token tokenToDelete = findTokenToDelete(userTokens);
            deleteToken(tokenToDelete, user);
        }

        LocalDateTime expirationDateTime = LocalDateTime.now().plusSeconds(expiration);

        Token newToken = createNewToken(user, token, jwtId, expirationDateTime, isMobileDevice);

        String tokenJson = objectMapper.writeValueAsString(newToken);

        saveToken(newToken, tokenJson);

        return newToken;
    }

    @Transactional
    @Override
    public Token refreshToken(String refreshToken, User user) throws Exception {
        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new IllegalArgumentException("Refresh token cannot be null or empty");
        }

        // Lấy key refreshtoken
        String tokenKey = redisService.getToken("refreshToken:" + refreshToken);
        if (tokenKey == null) {
            throw new ExpiredTokenException("Refresh token is expired or revoked");
        }

        String rawToken = redisService.getToken(tokenKey);
        if (rawToken == null) {
            throw new ExpiredTokenException("Refresh token is expired or revoked");
        }

        Token existingToken = parseToken(rawToken);

        validateToken(existingToken);

        String oldJwtId = existingToken.getJwtId();
        String oldToken = existingToken.getToken();
        String oldRefreshToken = existingToken.getRefreshToken();
        String oldTokenKey = "token:" + user.getId() + ":" + oldJwtId;

        // Cập nhật token
        Token updatedToken = updateToken(existingToken, user);
        String updatedTokenJson = objectMapper.writeValueAsString(updatedToken);

        redisService.deleteToken(oldTokenKey);
        redisService.deleteToken("jwtId:" + oldJwtId);
        redisService.deleteToken("refreshToken:" + oldRefreshToken);
        redisService.deleteToken("tokenValue:" + oldToken);

        // Lưu token đã cập nhật
        saveUpdatedToken(updatedToken, updatedTokenJson);

        return updatedToken;
    }

    private List<Token> getTokensByUser(User user) {
        String tokenKeyPattern = "token:" + user.getId() + ":*";
        Set<String> keys = redisService.getKeys(tokenKeyPattern);
        return keys.stream()
                .map(key -> {
                    String tokenJson = redisService.getToken(key);
                    if (tokenJson != null) {
                        try {
                            return objectMapper.readValue(tokenJson, Token.class);
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException("Failed to deserialize token", e);
                        }
                    } else {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }


    private Token findTokenToDelete(List<Token> userTokens) {
        return userTokens.stream()
                .filter(userToken -> !userToken.isMobile())
                .findFirst()
                .orElse(userTokens.get(0));
    }

    private void deleteToken(Token tokenToDelete, User user) {
        if (tokenToDelete != null) {
            redisService.deleteToken("token:" + user.getId() + ":" + tokenToDelete.getJwtId());
            redisService.deleteToken("jwtId:" + tokenToDelete.getJwtId());
            redisService.deleteToken("refreshToken:" + tokenToDelete.getRefreshToken());
            redisService.deleteToken("tokenValue:" + tokenToDelete.getToken());
        }
    }

    private Token createNewToken(User user, String token, String jwtId, LocalDateTime expirationDateTime, boolean isMobileDevice) {
        Token newToken = Token.builder()
                .userId(user.getId())
                .jwtId(jwtId)
                .token(token)
                .revoked(false)
                .expired(false)
                .tokenType("Bearer")
                .expirationDate(expirationDateTime)
                .isMobile(isMobileDevice)
                .build();

        newToken.setRefreshToken(UUID.randomUUID().toString());
        newToken.setRefreshExpirationDate(LocalDateTime.now().plusSeconds(expirationRefreshToken));

        return newToken;
    }

    private void saveToken(Token newToken, String tokenJson) {
        String tokenKey = "token:" + newToken.getUserId() + ":" + newToken.getJwtId();
        redisService.saveTokenToRedis(tokenKey, tokenJson, expiration);
        redisService.saveTokenToRedis("jwtId:" + newToken.getJwtId(), tokenKey, expiration);
        redisService.saveTokenToRedis("refreshToken:" + newToken.getRefreshToken(), tokenKey, expirationRefreshToken);
        redisService.saveTokenToRedis("tokenValue:" + newToken.getToken(), tokenKey, expiration);
    }

    private Token parseToken(String rawToken) {
        try {
            return objectMapper.readValue(rawToken, Token.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse token JSON", e);
        }
    }

    private void validateToken(Token existingToken) throws ExpiredTokenException {
        if (existingToken == null || existingToken.getRefreshExpirationDate().isBefore(LocalDateTime.now())
                || existingToken.isRevoked() || existingToken.isExpired()) {
            throw new ExpiredTokenException("Refresh token is expired or revoked");
        }
    }

    private Token updateToken(Token existingToken, User user) throws Exception {
        String newToken = jwtTokenUtil.generateToken(user);
        String newJwtId = jwtTokenUtil.extractJwtId(newToken);
        LocalDateTime expirationDateTime = LocalDateTime.now().plusSeconds(expiration);

        // Cập nhật token
        existingToken.setJwtId(newJwtId);
        existingToken.setToken(newToken);
        existingToken.setExpirationDate(expirationDateTime);

        String newRefreshToken = UUID.randomUUID().toString();
        existingToken.setRefreshToken(newRefreshToken);
        existingToken.setRefreshExpirationDate(LocalDateTime.now().plusSeconds(expirationRefreshToken));
        existingToken.setRevoked(false);
        existingToken.setExpired(false);

        return existingToken;
    }

    private void saveUpdatedToken(Token updatedToken, String updatedTokenJson) throws JsonProcessingException {
        String newTokenKey = "token:" + updatedToken.getUserId() + ":" + updatedToken.getJwtId();

        // Lưu token mới vào Redis
        redisService.saveTokenToRedis(newTokenKey, updatedTokenJson, expiration);
        redisService.saveTokenToRedis("jwtId:" + updatedToken.getJwtId(), newTokenKey, expiration);
        redisService.saveTokenToRedis("refreshToken:" + updatedToken.getRefreshToken(), newTokenKey, expirationRefreshToken);
        redisService.saveTokenToRedis("tokenValue:" + updatedToken.getToken(), newTokenKey, expiration);
    }
}
