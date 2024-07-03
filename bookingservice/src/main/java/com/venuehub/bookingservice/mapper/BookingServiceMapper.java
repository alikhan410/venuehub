package com.venuehub.bookingservice.mapper;

import com.venuehub.bookingservice.dto.BookingDateTimeDto;
import com.venuehub.bookingservice.dto.BookingDto;
import com.venuehub.bookingservice.model.Booking;
import com.venuehub.bookingservice.response.GetBookingsResponse;
import org.mapstruct.Mapper;

@Mapper
public interface BookingServiceMapper {
    BookingDto bookingToBookingDto(Booking booking);
    BookingDateTimeDto bookingToBookingDateTimeDto(Booking booking);
    default GetBookingsResponse bookingToBookingResponse(Booking booking){
        return new GetBookingsResponse(
                booking.getId(),
                booking.getStatus(),
                booking.getUsername(),
                booking.getVenue().getName(),
                booking.getVenue().getId(),
                booking.getBookingDateTime(),
                booking.getReservationExpiry()
        );
    }
}
