package com.venuehub.venueservice.dto;

import com.venuehub.venueservice.model.ImageUri;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record VenueDto(
        Long id,
        @NotNull(message = "Venue Name can not be empty") @NotBlank(message = "Venue Name can not be blank") String name,
        String username,
        @NotNull(message = "description can not be empty") @NotBlank(message = "description can not be blank") String description,
        @NotNull(message = "Venue type can not be empty") @NotBlank(message = "Venue type can not be blank") String venueType,
        @NotNull(message = "Location can not be empty") @NotBlank(message = "Location can not be blank") String location,
        @Min(value = 20, message = "Enter a capacity more than 20") @NotBlank(message = "Capacity can not be blank") String capacity,
        List<ImageUri> imageUris,
        @NotNull(message = "Phone can not be empty") @NotBlank(message = "Phone can not be blank") String phone,
        @NotNull(message = "Estimate can not be empty") @NotBlank(message = "Estimate can not be blank") String estimate,
        List<BookingDto> bookings
) {
}
