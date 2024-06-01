package com.venuehub.paymentservice.dto;

import com.venuehub.paymentservice.model.OrderStatus;

public record OrderDto(Long orderId, String username, String clientSecret, int amount, Long bookingId,
                       OrderStatus status) {
}
