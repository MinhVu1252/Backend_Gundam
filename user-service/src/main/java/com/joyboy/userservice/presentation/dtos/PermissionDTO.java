package com.joyboy.userservice.presentation.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PermissionDTO {
    @NotBlank
    @Size(min = 3, max = 35, message = "Permission name must be between 3 and 35 characters")
    private String name_permission;

    @Size(max = 150, message = "Description permission must be max 150 characters")
    private String description_permission;
}
