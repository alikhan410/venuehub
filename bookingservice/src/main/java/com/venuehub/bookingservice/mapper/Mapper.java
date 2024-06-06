package com.venuehub.bookingservice.mapper;

import com.venuehub.bookingservice.dto.BookedVenueDto;
import com.venuehub.bookingservice.dto.BookingDateDto;
import com.venuehub.bookingservice.model.BookedVenue;
import com.venuehub.bookingservice.response.GetBookingByUsernameResponse;

import java.time.LocalDateTime;
import java.time.ZoneId;


public class Mapper {
    public static BookedVenueDto modelToDto(BookedVenue bookedVenue) {
        return new BookedVenueDto(
                bookedVenue.getEmail(),
                bookedVenue.getPhone(),
                bookedVenue.getStatus(),
                bookedVenue.getBookingDateTime(),
                bookedVenue.getGuests()
        );
    }

    public static BookingDateDto modelToBookingDateDto(BookedVenue bookedVenue) {
        return new BookingDateDto(bookedVenue.getBookingDateTime());
    }

    public static GetBookingByUsernameResponse modelToResponse(BookedVenue bookedVenue) {

        return new GetBookingByUsernameResponse(
                bookedVenue.getId(),
                bookedVenue.getStatus(),
                bookedVenue.getUsername(),
                bookedVenue.getVenue().getName(),
                bookedVenue.getBookingDateTime(),
                bookedVenue.getReservationExpiry()
        );
    }

}
