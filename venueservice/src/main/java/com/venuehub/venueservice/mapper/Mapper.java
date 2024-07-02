package com.venuehub.venueservice.mapper;

import com.venuehub.venueservice.dto.*;
import com.venuehub.venueservice.model.Booking;
import com.venuehub.venueservice.model.ImageData;
import com.venuehub.venueservice.model.Venue;

import java.util.List;


public class Mapper {
    public static VenueDto modelToVenueDto(Venue venue) {
        List<BookingDto> bookings = modelToBookingDtoList(venue.getBookings());
        List<ImageDto> images = modelToImageDtoList(venue.getImages());
        return new VenueDto(
                venue.getId(),
                venue.getName(),
                venue.getUsername(),
                venue.getDescription(),
                venue.getVenueType(),
                venue.getLocation(),
                String.valueOf(venue.getCapacity()),
                images,
                venue.getPhone(),
                String.valueOf(venue.getEstimate()),
                bookings
        );
    }

    public static List<BookingDto> modelToBookingDtoList(List<Booking> bookings) {
        return bookings.stream().map(Mapper::modelToBookingDto).toList();

    }

    public static BookingDto modelToBookingDto(Booking booking) {
        return new BookingDto(booking.getId(), booking.getUsername(), booking.getStatus());
    }

    public static List<ImageDto> modelToImageDtoList(List<ImageData> imageDataList) {
        return imageDataList.stream().map(Mapper::modelToImageDto).toList();
    }

    public static ImageDto modelToImageDto(ImageData imageData) {
        return new ImageDto(imageData.getId(), imageData.getImage());
    }

}
