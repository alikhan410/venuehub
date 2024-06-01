package com.venuehub.venueservice.dto;

import com.venuehub.venueservice.model.ImageData;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record VenueListDto(
        @NotNull Long id,
        @NotNull @NotBlank(message = "Name can not be blank or empty") String name,
        @NotNull @NotBlank(message = "Venue type can not be blank or empty") String venueType,
        @NotNull @NotBlank(message = "Location can not be blank or empty") String location,
        ImageData image,
        @Min(value = 0, message = "Invalid value") String estimate
) {
}
