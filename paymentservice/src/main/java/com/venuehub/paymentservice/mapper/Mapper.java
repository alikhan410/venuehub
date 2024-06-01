package com.venuehub.paymentservice.mapper;

import com.venuehub.paymentservice.dto.OrderDto;
import com.venuehub.paymentservice.model.BookingOrder;

public class Mapper {
    public static OrderDto modelToDto(BookingOrder order) {
        return new OrderDto(
                order.getId(),
                order.getUsername(),
                order.getClientSecret(),
                order.getAmount(),
                order.getBookingId(),
                order.getOrderStatus()
        );
    }
}
