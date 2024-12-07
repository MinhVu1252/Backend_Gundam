package com.joyboy.userservice.domain.entities.models;

import lombok.*;

import java.time.LocalDateTime;
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Token {
    private String jwtId;

    private String token;

    private String refreshToken;

    private String tokenType;

    private LocalDateTime expirationDate;

    private LocalDateTime refreshExpirationDate;

    private boolean isMobile;

    private boolean revoked;
    private boolean expired;

    private Long userId;
}
