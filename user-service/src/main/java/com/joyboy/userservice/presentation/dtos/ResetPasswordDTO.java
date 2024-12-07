package com.joyboy.userservice.presentation.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResetPasswordDTO {
    @NotBlank(message = "Password cannot be blank")
    @Size(min = 7, max = 35, message = "Password must be between 7 and 35 characters")
    private String oldPassword;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 7, max = 35, message = "Password must be between 7 and 35 characters")
    private String newPassword;
}
