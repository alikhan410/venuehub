package com.venuehub.bookingservice.mapper;

import com.venuehub.bookingservice.dto.BookingDto;
import com.venuehub.bookingservice.dto.BookingDateDto;
import com.venuehub.bookingservice.model.Booking;
import com.venuehub.bookingservice.response.GetBookingByUsernameResponse;
import com.venuehub.bookingservice.response.GetBookingVendorResponse;


public class Mapper {
    public static BookingDto modeltoBookingDto(Booking booking) {
        return new BookingDto(
                booking.getEmail(),
                booking.getPhone(),
                booking.getStatus(),
                booking.getBookingDateTime(),
                booking.getGuests()
        );
    }

    public static BookingDateDto modelToBookingDateDto(Booking booking) {
        return new BookingDateDto(booking.getBookingDateTime());
    }

    public static GetBookingByUsernameResponse modelToResponse(Booking booking) {

        return new GetBookingByUsernameResponse(
                booking.getId(),
                booking.getStatus(),
                booking.getUsername(),
                booking.getVenue().getName(),
                booking.getBookingDateTime(),
                booking.getReservationExpiry()
        );
    }

    public static GetBookingVendorResponse modelToVendorResponse(Booking booking) {

        return new GetBookingVendorResponse(
                booking.getId(),
                booking.getVenue().getName(),
                booking.getUsername(),
                booking.getBookingDateTime(),
                booking.getStatus()
        );
    }

}
