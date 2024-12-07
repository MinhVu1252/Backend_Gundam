package com.joyboy.userservice.presentation.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginResponse {
    private String token;
    private String refreshToken;
    private Long id;
    private String username;
    private List<String> roles;
}
