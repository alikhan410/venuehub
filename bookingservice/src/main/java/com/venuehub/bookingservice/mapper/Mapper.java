package com.venuehub.bookingservice.mapper;

import com.venuehub.bookingservice.dto.BookedVenueDto;
import com.venuehub.bookingservice.model.BookedVenue;


public class Mapper {
    public static BookedVenueDto modelToDto(BookedVenue bookedVenue){
        return new BookedVenueDto(
                bookedVenue.getEmail(),
                bookedVenue.getPhone(),
                bookedVenue.getStatus(),
                bookedVenue.getBookingDateTime(),
                bookedVenue.getGuests()
        );
    }
}
