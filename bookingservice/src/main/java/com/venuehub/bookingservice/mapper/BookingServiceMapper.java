package com.venuehub.bookingservice.mapper;

import com.venuehub.bookingservice.dto.BookingDateTimeDto;
import com.venuehub.bookingservice.dto.BookingDto;
import com.venuehub.bookingservice.model.Booking;
import com.venuehub.bookingservice.response.GetBookingsResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface BookingServiceMapper {
    BookingDto bookingToBookingDto(Booking booking);

    List<BookingDto> bookingsToBookingDtoList(List<Booking> bookings);

    BookingDateTimeDto bookingToBookingDateTimeDto(Booking booking);

    default GetBookingsResponse bookingToBookingResponse(Booking booking) {
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
