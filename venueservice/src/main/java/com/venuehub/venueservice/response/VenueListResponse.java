package com.venuehub.venueservice.response;

import com.venuehub.venueservice.dto.VenueDto;


import java.util.List;

public record VenueListResponse(List<VenueDto> venueList) {
}
