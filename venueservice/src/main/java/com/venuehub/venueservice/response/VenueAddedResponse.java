package com.venuehub.venueservice.response;


public record VenueAddedResponse(String status) {
    public VenueAddedResponse() {
        this("Venue added");
    }
}
