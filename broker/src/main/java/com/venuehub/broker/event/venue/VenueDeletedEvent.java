package com.venuehub.broker.event.venue;

import com.fasterxml.jackson.annotation.JsonProperty;

@VenueDeleted
public record VenueDeletedEvent(Long venueId, String username){}

