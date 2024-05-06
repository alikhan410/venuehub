package com.venuehub.venueservice.mapper;

import com.venuehub.venueservice.dto.BookedVenueDto;
import com.venuehub.venueservice.dto.VenueDto;
import com.venuehub.venueservice.model.BookedVenue;
import com.venuehub.venueservice.model.ImageData;
import com.venuehub.venueservice.model.Venue;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Mapper {
    public static VenueDto modelToDto(Venue venue) {
        return new VenueDto(
                venue.getName(),
                venue.getVenueType(),
                venue.getLocation(),
                venue.getCapacity(),
                venue.getPhone(),
                venue.getEstimate(),
                venue.getBookings()
        );
    }

    public static BookedVenueDto modelToDto(BookedVenue bookedVenue) {
        return new BookedVenueDto(bookedVenue.getStatus(), bookedVenue.getVenue());
    }
}
