package com.venuehub.authservice.controller;

import com.venuehub.authservice.dto.CurrentUserDto;
import com.venuehub.authservice.response.JwkSetResponse;
import com.venuehub.commons.error.response.CustomAuthenticationErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Map;

public interface AuthApi {

    @Operation(summary = "Exposes the public jwt key for authorization", description = "Exposes the public jwt key for authorization")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successful operation",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = JwkSetResponse.class)
                            )
                    }
            )
    })
    Map<String, Object> getPublicKey();

    @Operation(summary = "Returns details for the current user", description = "This endpoint returns the details of the currently authenticated user. The user must have the 'USER' role to access this endpoint")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successful operation",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = CurrentUserDto.class)
                            )
                    }
            ),
            @ApiResponse(responseCode = "401", description = "User Unauthenticated", content = {
                    @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomAuthenticationErrorResponse.class)
                    )
            })
    })
    CurrentUserDto getUser(@AuthenticationPrincipal Jwt jwt);
}
