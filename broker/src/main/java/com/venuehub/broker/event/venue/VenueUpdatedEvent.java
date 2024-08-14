package com.venuehub.broker.event.venue;

public record VenueUpdatedEvent(Long venueId,String name, int capacity, String phone, String estimate ) {
}
