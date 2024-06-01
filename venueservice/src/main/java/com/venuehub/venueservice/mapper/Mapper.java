package com.venuehub.venueservice.mapper;

import com.venuehub.venueservice.dto.BookedVenueDto;
import com.venuehub.venueservice.dto.VenueDto;
import com.venuehub.venueservice.dto.VenueListDto;
import com.venuehub.venueservice.model.BookedVenue;
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
        return new VenueListDto(
                venue.getId(), venue.getName(), venue.getVenueType(), venue.getLocation(),venue.getImages().get(0),String.valueOf(venue.getEstimate())
        );
    }

    public static BookedVenueDto modelToVenueDto(BookedVenue bookedVenue) {
        return new BookedVenueDto(bookedVenue.getStatus(), bookedVenue.getVenue());
    }
}
