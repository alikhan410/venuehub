package com.venuehub.bookingservice.response;

import com.venuehub.bookingservice.dto.BookingDateTimeDto;

import java.util.List;

public record BookingDateListResponse(List<BookingDateTimeDto> bookingDateTimeDtoList) {
}
