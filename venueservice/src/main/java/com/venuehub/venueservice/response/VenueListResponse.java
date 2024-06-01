package com.venuehub.venueservice.response;

import com.venuehub.venueservice.dto.VenueListDto;

import java.util.List;
import java.util.stream.Collectors;

public record VenueListResponse(List<VenueListDto> venueList) {

    public VenueListResponse(List<VenueListDto> venueList) {
        this.venueList = venueList.stream().map(v -> new VenueListDto(
                v.id(),
                v.name(),
                v.venueType(),
                v.location(),
                v.image(),
                v.estimate()
        )).collect(Collectors.toList());
    }
}
