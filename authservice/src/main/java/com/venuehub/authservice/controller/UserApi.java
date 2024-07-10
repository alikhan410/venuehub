package com.venuehub.authservice.controller;

import com.venuehub.authservice.dto.LoginDto;
import com.venuehub.authservice.dto.UserDto;
import com.venuehub.authservice.response.LoginResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.RequestBody;

public interface UserApi {

    @Operation(summary = "Register a new user", description = "Registers a new user with the application")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "User successfully registered",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = LoginResponse.class)
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request",
                    content = @Content
            )
    })
    ResponseEntity<LoginResponse> registerUser(@RequestBody @Valid UserDto body);

    @Operation(summary = "Login user", description = "Logs in a user with provided credentials")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User successfully logged in",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = LoginResponse.class)
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content
            )
    })
    ResponseEntity<LoginResponse> loginUser(@RequestBody @Valid LoginDto body);

    @Operation(summary = "Logout user", description = "Logs out the currently authenticated user")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User successfully logged out",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = LoginResponse.class)
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content
            )
    })
    ResponseEntity<LoginResponse> logoutUser(@AuthenticationPrincipal Jwt jwt);
}