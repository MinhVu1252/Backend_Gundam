package com.joyboy.userservice.presentation.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserDTO {
    @NotBlank(message = "Username cannot be blank")
    @Size(min = 4, max = 15, message = "Username must be between 4 and 15 characters")
    private String username;

    @NotBlank(message = "Firstname cannot be blank")
    @Size(min = 2, max = 35, message = "Firstname must be between 2 and 35 characters")
    private String firstName;

    @NotBlank(message = "Lastname cannot be blank")
    @Size(min = 2, max = 35, message = "Lastname must be between 2 and 35 characters")
    private String lastName;

    @NotBlank(message = "Email cannot be blank")
    private String email;

    private String phoneNumber;
}
