package com.venuehub.broker.event.venue;

@VenueCreated
public record VenueCreatedEvent(Long venueId, String name, String username) {
}


