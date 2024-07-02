package com.venuehub.venueservice.dto;

import com.venuehub.broker.constants.BookingStatus;

public record BookingDto(Long id, String username, BookingStatus status) {
}
