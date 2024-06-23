package com.venuehub.venueservice.mapper;

import com.venuehub.venueservice.dto.BookedVenueDto;
import com.venuehub.venueservice.dto.ImageDto;
import com.venuehub.venueservice.dto.VenueDto;
import com.venuehub.venueservice.dto.VenueListDto;
import com.venuehub.venueservice.model.Booking;
import com.venuehub.venueservice.model.Venue;


public class Mapper {
    public static VenueDto modelToVenueDto(Venue venue) {

        return new VenueDto(
                venue.getId(),
                venue.getName(),
                venue.getUsername(),
                venue.getDescription(),
                venue.getVenueType(),
                venue.getLocation(),
                String.valueOf(venue.getCapacity()),
                venue.getImages(),
                venue.getPhone(),
                String.valueOf(venue.getEstimate()),
                venue.getBookings()
        );
    }

    public static VenueListDto modelToVenueListDto(Venue venue) {
        ImageDto imageDto = new ImageDto(venue.getImages().get(0).getImage());
        return new VenueListDto(
                venue.getId(), venue.getName(), venue.getVenueType(), venue.getLocation(),imageDto,String.valueOf(venue.getEstimate())
        );
    }

    public static BookedVenueDto modelToVenueDto(Booking booking) {
        return new BookedVenueDto(booking.getStatus(), booking.getVenue());
    }
}
