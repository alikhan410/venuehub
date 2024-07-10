package com.venuehub.imageservice.controller;

import com.venuehub.commons.error.response.CustomAuthenticationErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


public interface ImageApi {

    @Operation(summary = "Retrieve an image", description = "Retrieves an image for a given vendor, venue, and file name")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successful operation",
                    content = {
                            @Content(mediaType = MediaType.IMAGE_JPEG_VALUE,
                                    schema = @Schema(implementation = Resource.class)
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Image not found",
                    content = @Content
            )
    })
    ResponseEntity<Resource> getImage(
            String vendorName,
            String venueName,
            String fileName);

    @Operation(summary = "Save images", description = "Saves multiple images for a given venue")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Images successfully saved",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User unauthenticated",
                    content = {
                            @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = CustomAuthenticationErrorResponse.class)
                            )
                    }
            )
    })
    ResponseEntity<HttpStatus> saveImage(
            String venueName,
            MultipartFile[] files,
            @AuthenticationPrincipal Jwt jwt) throws IOException;
}