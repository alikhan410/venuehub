package com.venuehub.paymentservice.response;

import com.venuehub.paymentservice.model.BookingOrder;

import java.util.List;

public record BookingOrderListResponse(List<BookingOrder> bookingOrders) {
}
