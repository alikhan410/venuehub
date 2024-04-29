package com.venuehub.paymentservice.dto;

import com.venuehub.broker.constants.BookingStatus;
import jakarta.validation.constraints.NotNull;

public record BookedVenueDto(Long id, @NotNull int amount, String username, BookingStatus status) {
}
