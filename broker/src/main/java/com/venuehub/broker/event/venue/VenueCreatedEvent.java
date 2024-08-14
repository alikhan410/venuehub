package com.venuehub.broker.event.venue;

public record VenueCreatedEvent(Long venueId, String name, int estimate,  String username) {
}


