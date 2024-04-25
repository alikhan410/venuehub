package com.venuehub.venueservice.dto;

import com.venuehub.venueservice.model.BookedVenue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record VenueDto(
        @NotNull @NotBlank(message = "Name can not be blank or empty") String name,
        @NotNull @NotBlank(message = "Venue type can not be blank or empty") String venueType,
        @NotNull @NotBlank(message = "Location can not be blank or empty") String location,
        @Min(value = 0, message = "Invalid value") int capacity,
        byte[] image,
        @NotNull @NotBlank(message = "Phone can not be blank or empty") String phone,
        @Min(value = 0, message = "Invalid value") String estimate,
        List<BookedVenue> bookings
) {
}
