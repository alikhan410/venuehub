package com.venuehub.authservice.controller;

import com.venuehub.authservice.dto.CurrentUserDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Map;

public interface AuthApi {

    @Operation(summary = "Retrieve public key", description = "Retrieves the public key used for JWT verification")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved public key",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Map.class)
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content
            )
    })
    Map<String, Object> getPublicKey();

    @Operation(summary = "Retrieve current user information", description = "Retrieves information about the currently authenticated user")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved current user information",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = CurrentUserDto.class)
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content
            )
    })
    CurrentUserDto getUser(@AuthenticationPrincipal Jwt jwt);
}
