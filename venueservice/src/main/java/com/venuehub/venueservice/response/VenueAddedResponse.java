package com.venuehub.venueservice.response;

import org.springframework.http.HttpStatus;

public record VenueAddedResponse(String status) {
    public VenueAddedResponse() {
        this("Venue added");
    }
}
