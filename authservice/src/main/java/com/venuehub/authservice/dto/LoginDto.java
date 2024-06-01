package com.venuehub.authservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LoginDto(
        @NotNull(message = "Username can not be null") @NotBlank(message = "Username can not be blank") String username,
        @NotNull(message = "Password can not be null") @NotBlank(message = "Password can not be blank") String password
        ) {
}
