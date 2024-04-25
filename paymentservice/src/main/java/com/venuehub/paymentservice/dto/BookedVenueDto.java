package com.venuehub.paymentservice.dto;

import com.venuehub.broker.constants.BookingStatus;

public record BookedVenueDto(Long id, int amount, String username, BookingStatus status) {
}
