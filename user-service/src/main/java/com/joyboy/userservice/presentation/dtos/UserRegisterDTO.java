package com.joyboy.userservice.presentation.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserRegisterDTO {
    @NotBlank(message = "Username cannot be blank")
    @Size(min = 4, max = 15, message = "Username must be between 4 and 15 characters")
    private String username;

    @NotBlank(message = "Email cannot be blank")
    private String email;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 7, max = 35, message = "Password must be between 7 and 35 characters")
    private String password;
}

