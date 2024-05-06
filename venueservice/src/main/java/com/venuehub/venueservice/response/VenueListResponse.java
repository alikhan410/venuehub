package com.venuehub.venueservice.response;

import com.venuehub.venueservice.dto.VenueDto;
import com.venuehub.venueservice.model.BookedVenue;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public record VenueListResponse(List<VenueDto> venueList) {
    public VenueListResponse(List<VenueDto> venueList) {
        List<BookedVenue> emptyList = new ArrayList<>();
        this.venueList = venueList.stream().map(v -> new VenueDto(
                v.name(),
                v.venueType(),
                v.location(),
                v.capacity(),
                v.phone(),
                v.estimate(),
                emptyList
        )).collect(Collectors.toList());
    }
}
