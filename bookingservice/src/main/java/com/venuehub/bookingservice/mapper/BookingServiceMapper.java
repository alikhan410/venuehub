package com.venuehub.bookingservice.mapper;

import com.venuehub.bookingservice.dto.BookingDateDto;
import com.venuehub.bookingservice.dto.BookingDto;
import com.venuehub.bookingservice.model.Booking;
import com.venuehub.bookingservice.response.BookingResponse;
import com.venuehub.bookingservice.response.GetBookingsResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface BookingServiceMapper {
    BookingDto bookingToBookingDto(Booking booking);

    List<BookingDto> bookingsToBookingDtoList(List<Booking> bookings);

    BookingDateDto bookingToBookingDateTimeDto(Booking booking);

    default BookingResponse bookingToBookingResponse(Booking booking) {
        return new BookingResponse(
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
