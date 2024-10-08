package com.venuehub.bookingservice.mapper;

import com.venuehub.bookingservice.dto.BookingDateDto;
import com.venuehub.bookingservice.dto.BookingDto;
import com.venuehub.bookingservice.model.Booking;
import com.venuehub.bookingservice.response.GetBookingsResponse;


public class Mapper {
    public static BookingDto modeltoBookingDto(Booking booking) {
        return new BookingDto(
                booking.getPhone(),
                booking.getStatus(),
                booking.getBookingDate(),
                booking.getGuests()
        );
    }

    public static BookingDateDto modelToBookingDateDto(Booking booking) {
        return new BookingDateDto(booking.getBookingDate());
    }

    public static GetBookingsResponse modelToResponse(Booking booking) {

        return new GetBookingsResponse(
                booking.getId(),
                booking.getStatus(),
                booking.getUsername(),
                booking.getVenue().getName(),
                booking.getVenue().getId(),
                booking.getBookingDate(),
                booking.getReservationExpiry()
        );
    }

}
