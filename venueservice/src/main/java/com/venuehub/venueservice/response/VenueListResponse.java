package com.venuehub.venueservice.response;

import com.venuehub.venueservice.dto.ImageDto;
import com.venuehub.venueservice.dto.VenueDto;


import java.util.List;
import java.util.stream.Collectors;

public record VenueListResponse(List<VenueDto> venueList) {
}
