package com.joyboy.userservice.presentation.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginDTO {
    private String username;

    private String email;

    @NotBlank(message = "Password cannot be blank")
    private String password;
}
