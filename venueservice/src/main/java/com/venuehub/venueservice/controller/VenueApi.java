package com.venuehub.venueservice.controller;

import com.venuehub.venueservice.dto.VenueDto;
import com.venuehub.venueservice.response.VenueAddedResponse;
import com.venuehub.venueservice.response.VenueListResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

public interface VenueApi {

    @Operation(
            summary = "Get Venue by ID",
            description = "Retrieve details of a specific venue by its unique identifier."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Success: Retrieved venue details.",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = VenueDto.class)
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not Found: Venue with the specified ID does not exist."
            )
    })
    ResponseEntity<VenueDto> getVenueById(@PathVariable Long id);

    @Operation(
            summary = "Add Venue",
            description = "Create a new venue under the authenticated user's account."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Success: Venue created successfully.",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = VenueAddedResponse.class)
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad Request: Invalid data provided for venue creation."
            )
    })
    ResponseEntity<VenueAddedResponse> addVenue(@RequestBody @Valid VenueDto body, @AuthenticationPrincipal Jwt jwt);


    @Operation(
            summary = "Delete Venue",
            description = "Delete a venue by its ID, restricted to the authenticated user."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Success: Venue deleted successfully.",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = VenueListResponse.class)
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden: User not authorized to delete the venue."
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not Found: Venue with the specified ID does not exist."
            )
    })
    ResponseEntity<VenueListResponse> deleteVenue(@PathVariable long id, @AuthenticationPrincipal Jwt jwt);


    @Operation(
            summary = "Get All Venues",
            description = "Retrieve a list of all venues."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Success: Retrieved list of all venues.",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = VenueListResponse.class)
                            )
                    }
            )
    })
    ResponseEntity<VenueListResponse> getVenue();


    @Operation(
            summary = "Get Venues by Vendor",
            description = "Retrieve a list of venues associated with the authenticated vendor."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Success: Retrieved list of venues associated with the vendor.",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = VenueListResponse.class)
                            )
                    }
            )
    })
    ResponseEntity<VenueListResponse> getVenueByVendorName(@AuthenticationPrincipal Jwt jwt);


    @Operation(
            summary = "Update Venue",
            description = "Update details of an existing venue by its ID, restricted to the authenticated user."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Success: Venue updated successfully."
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad Request: Invalid data provided for venue update."
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden: User not authorized to update the venue."
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not Found: Venue with the specified ID does not exist."
            )
    })
    ResponseEntity<HttpStatus> updateVenue(@PathVariable long id, @RequestBody VenueDto body, @AuthenticationPrincipal Jwt jwt) throws Exception;
}