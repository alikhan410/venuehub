package com.venuehub.paymentservice.dto;

import com.venuehub.broker.constants.BookingStatus;
import jakarta.validation.constraints.NotNull;

public record BookingDto(Long bookingId) {
}
