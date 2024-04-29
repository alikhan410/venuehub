package com.venuehub.paymentservice.dto;

public record OrderDto(String username, int amount, Long bookingId, Long venueId) {
}
