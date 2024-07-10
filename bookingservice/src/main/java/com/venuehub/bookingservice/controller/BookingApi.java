package com.venuehub.bookingservice.controller;

import com.venuehub.bookingservice.dto.BookingDateTimeDto;
import com.venuehub.bookingservice.dto.BookingDto;
import com.venuehub.bookingservice.response.BookingDateListResponse;
import com.venuehub.bookingservice.response.BookingResponse;
import com.venuehub.bookingservice.response.GetBookingsResponse;
import com.venuehub.commons.exception.BookingUnavailableException;
import com.venuehub.commons.exception.NoSuchBookingException;
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

import java.util.List;

public interface BookingApi {

    @Operation(summary = "Adds a booking", description = "Adds a booking for the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Successful operation",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = BookingDto.class)
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Booking unavailable or invalid request"
            )
    })
    ResponseEntity<BookingDto> addBooking(long venueId, BookingDto body, @AuthenticationPrincipal Jwt jwt) throws BookingUnavailableException;

    @Operation(summary = "Retrieves bookings by venue", description = "Retrieves all bookings for a given venue")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successful operation",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = BookingDateListResponse.class)
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Venue not found"
            )
    })
    ResponseEntity<BookingDateListResponse> getBookingByVenue(Long venueId);

    @Operation(summary = "Retrieves booking status", description = "Retrieves the status of a specific booking")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successful operation",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = BookingResponse.class)
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Booking not found"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "User not authorized to access booking"
            )
    })
    ResponseEntity<BookingResponse> getBookingStatus(long bookingId, @AuthenticationPrincipal Jwt jwt) throws NoSuchBookingException;

    @Operation(summary = "Retrieves bookings by user", description = "Retrieves all bookings associated with the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successful operation",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = GetBookingsResponse.class)
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found or no bookings found"
            )
    })
    ResponseEntity<List<GetBookingsResponse>> getBookingByUser(@AuthenticationPrincipal Jwt jwt) throws NoSuchBookingException;

    @Operation(summary = "Retrieves bookings by vendor", description = "Retrieves all bookings associated with the authenticated vendor")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successful operation",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = GetBookingsResponse.class)
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Vendor not found or no bookings found"
            )
    })
    ResponseEntity<List<GetBookingsResponse>> getBookingByVendor(@AuthenticationPrincipal Jwt jwt) throws NoSuchBookingException;

    @Operation(summary = "Cancels a booking", description = "Cancels a specific booking by the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Successful operation"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Booking cannot be cancelled or invalid request"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "User not authorized to cancel booking"
            )
    })
    ResponseEntity<HttpStatus> cancelBooking(long bookingId, @AuthenticationPrincipal Jwt jwt) throws Exception;

    @Operation(summary = "Updates booking date", description = "Updates the date of a specific booking by the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Successful operation"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Booking date cannot be updated or invalid request"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "User not authorized to update booking date"
            )
    })
    ResponseEntity<HttpStatus> updateBookingDate(long bookingId, BookingDateTimeDto body, @AuthenticationPrincipal Jwt jwt) throws Exception;
}

