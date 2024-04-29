package com.venuehub.bookingservice.dto;


import com.venuehub.bookingservice.validator.NotNullNotBlank;
import com.venuehub.broker.constants.BookingStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

public record BookedVenueDto(
        @Email(message = "Email can not be null or empty") String email,
        @NotNull @NotBlank(message = "Phone can not be null or blank") String phone,
        BookingStatus status,
        @NotNull @NotBlank(message = "Date can not be null or empty") String bookingDateTime,
        @Min(value = 0, message = "Invalid value") int guests
) {}
