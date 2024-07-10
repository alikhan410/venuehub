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

public interface VendorApi {

    @Operation(summary = "Register a new vendor", description = "Registers a new vendor with the application")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Vendor successfully registered",
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
    ResponseEntity<LoginResponse> registerVendor(@RequestBody @Valid UserDto body);

    @Operation(summary = "Login vendor", description = "Logs in a vendor with provided credentials")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Vendor successfully logged in",
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
    ResponseEntity<LoginResponse> loginVendor(@RequestBody @Valid LoginDto body);

    @Operation(summary = "Logout vendor", description = "Logs out the currently authenticated vendor")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Vendor successfully logged out",
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
    ResponseEntity<LoginResponse> logoutVendor(@AuthenticationPrincipal Jwt jwt);
}
