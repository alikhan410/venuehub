package com.venuehub.authservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserDto(
        @NotNull(message = "Username can not be null") @NotBlank(message = "Username can not be blank") String username,
        @NotNull(message = "Password can not be null") @NotBlank(message = "Password can not be blank") String password,
        @NotNull(message = "Email can not be null") @NotBlank(message = "Email can not be blank") @Email(message = "Enter a valid email") String email,
        @NotNull(message = "First Name can not be null") @NotBlank(message = "First Name can not be blank") String firstName,
        @NotNull(message = "Last Name can not be null") @NotBlank(message = "Last Name can not be blank") String lastName
) {
}
