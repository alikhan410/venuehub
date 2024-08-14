package com.venuehub.bookingservice.response;

import com.venuehub.bookingservice.dto.BookingDateDto;

import java.util.List;

public record BookingDateListResponse(List<BookingDateDto> bookingDateList) {
}
