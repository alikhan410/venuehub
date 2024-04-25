package com.venuehub.venueservice.mapper;

import com.venuehub.venueservice.dto.BookedVenueDto;
import com.venuehub.venueservice.dto.VenueDto;
import com.venuehub.venueservice.model.BookedVenue;
import com.venuehub.venueservice.model.Venue;

public class Mapper {
    public static VenueDto modelToDto(Venue venue) {

        return new VenueDto(
                venue.getName(),
                venue.getVenueType(),
                venue.getLocation(),
                venue.getCapacity(),
                venue.getImage(),
                venue.getPhone(),
                venue.getEstimate(),
                venue.getBookings()
        );
    }

    public static BookedVenueDto modelToDto(BookedVenue bookedVenue) {
        return new BookedVenueDto(bookedVenue.getStatus(), bookedVenue.getVenue());
    }
}
