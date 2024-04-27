package com.venuehub.bookingservice.mapper;

import com.venuehub.bookingservice.dto.BookedVenueDto;
import com.venuehub.bookingservice.model.BookedVenue;


public class Mapper {
    public static BookedVenueDto modelToDto(BookedVenue bookedVenue){
        BookedVenueDto bookedVenueDto = new BookedVenueDto();
        bookedVenueDto.setEmail(bookedVenue.getEmail());
        bookedVenueDto.setPhone(bookedVenue.getPhone());
        bookedVenueDto.setStatus(bookedVenue.getStatus());
        bookedVenueDto.setBookingDateTime(bookedVenue.getBookingDateTime());
        bookedVenueDto.setGuests(bookedVenue.getGuests());
        return bookedVenueDto;
    }
}
