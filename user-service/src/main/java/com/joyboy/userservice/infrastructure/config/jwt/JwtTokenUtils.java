package com.joyboy.userservice.infrastructure.config.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.joyboy.commonservice.common.exceptions.ExpiredTokenException;
import com.joyboy.commonservice.common.exceptions.InvalidParamException;
import com.joyboy.userservice.domain.entities.models.Role;
import com.joyboy.userservice.domain.entities.models.Token;
import com.joyboy.userservice.domain.entities.models.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.security.SecureRandom;
import java.util.*;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtTokenUtils {
    @Value("${jwt.expiration}")
    private int expiration;

    @Value("${jwt.expiration-refresh-token}")
    private int expirationRefreshToken;

    @Value("${jwt.secretkey}")
    private String secretKey;

    private final ObjectMapper objectMapper;

    private final RedisTemplate<String, Object> redisTemplate;

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenUtils.class);

    public String generateToken(User user) throws Exception {
        Map<String, Object> claims = new HashMap<>();

        String subject = getSubject(user);
        claims.put("subject", subject);

        claims.put("userId", user.getId());

        List<String> roleNames = new ArrayList<>();
        for (Role role : user.getRoles()) {
            roleNames.add(role.getNameRole());
        }
        claims.put("roles", roleNames);

        try {
            return Jwts.builder()
                    .setClaims(claims)
                    .setSubject(subject)
                    .setExpiration(new Date(System.currentTimeMillis() + expiration * 1000L))
                    .setId(UUID.randomUUID().toString())
                    .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                    .compact();
        } catch (Exception e) {
            throw new InvalidParamException("Cannot create jwt token, error: " + e.getMessage());
        }
    }


    private static String getSubject(User user) {
        String subject = user.getUsername();
        if (subject == null || subject.isBlank()) {
            subject = user.getEmail();
        }
        return subject;
    }

    private Key getSignInKey() {
        byte[] bytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(bytes);
    }

    private String generateSecretKey() {
        SecureRandom random = new SecureRandom();
        byte[] keyBytes = new byte[32]; // 256-bit key
        random.nextBytes(keyBytes);
        String secretKey = Encoders.BASE64.encode(keyBytes);
        return secretKey;
    }

    public String extractJwtId(String token) {
        return extractClaim(token, Claims::getId);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public  <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = this.extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public Map<String, Object> getClaimsFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(secretKey) // Đặt secret key của bạn
                .parseClaimsJws(token)
                .getBody();
        return new HashMap<>(claims);
    }


    //check expiration
    public boolean isTokenExpired(String token) {
        Date expirationDate = this.extractClaim(token, Claims::getExpiration);
        return expirationDate.before(new Date());
    }

    public String getSubject(String token) {
        return  extractClaim(token, Claims::getSubject);
    }

    public boolean validateToken(String token, User userDetails) {
        try {
            String subject = extractClaim(token, Claims::getSubject);
            return isTokenValid(token, subject, userDetails);
        } catch (JwtException | IllegalArgumentException e) {
            logJwtException(e);
            return false;
        }
    }

    private boolean isTokenValid(String token, String subject, User userDetails) {
        String tokenKey = "token:" + userDetails.getId() + ":" + token;
        Token existingToken = (Token) redisTemplate.opsForValue().get(tokenKey);

        if (existingToken == null || existingToken.isRevoked() || !userDetails.isActive()) {
            return false;
        }

        return subject.equals(userDetails.getUsername()) && !isTokenExpired(existingToken.getToken());
    }

    private static final Map<Class<? extends Exception>, String> JWT_EXCEPTION_MESSAGES = Map.of(
            MalformedJwtException.class, "Invalid JWT token: ",
            ExpiredJwtException.class, "JWT token is expired: ",
            UnsupportedJwtException.class, "JWT token is unsupported: ",
            IllegalArgumentException.class, "JWT claims string is empty: "
    );

    private void logJwtException(Exception e) {
        String message = JWT_EXCEPTION_MESSAGES.getOrDefault(e.getClass(), "JWT exception: ");
        logger.error("{}{}", message, e.getMessage());
    }

    public Claims verifyToken(String token, boolean isRefresh) throws Exception {
        try {
            // Trích xuất tất cả các claims từ token
            Claims claims = extractAllClaims(token);

            // Kiểm tra token có hết hạn không
            isTokenExpired(token);

            // Trích xuất userId và jwtId từ token
            String jwtId = extractJwtId(token);
            Integer userId = claims.get("userId", Integer.class); // Assuming userId is stored in claims

            if (jwtId == null || userId == null) {
                throw new Exception("Invalid token: missing jwtId or userId");
            }

            // Lấy tokenKey từ userId và jwtId
            String tokenKey = "token:" + userId + ":" + jwtId;
            String rawToken = (String) redisTemplate.opsForValue().get(tokenKey);
            if (rawToken == null) {
                throw new ExpiredTokenException("Token not found");
            }

            // Chuyển đổi JSON thành đối tượng Token
            Token existingToken = objectMapper.readValue(rawToken, Token.class);
            if (existingToken.isRevoked() || existingToken.isExpired()) {
                throw new ExpiredTokenException("Token is revoked or expired");
            }

            return claims;
        } catch (JwtException | IllegalArgumentException e) {
            throw new Exception("AUTHENTICATE", e);
        }
    }

}
