package com.venuehub.broker.event.venue;

import com.fasterxml.jackson.annotation.JsonProperty;

@VenueDeleted
public record VenueDeletedEvent(Long venueId, String username){}
//public class VenueDeletedEvent {
//    private final Long venueId;
//
//    public VenueDeletedEvent(Long venueId) {
//        this.venueId = venueId;
//    }
//
//    public Long getVenueId() {
//        return venueId;
//    }
//
//    @Override
//    public String toString() {
//        return "VenueDeletedEvent{" +
//                "venueId=" + venueId +
//                '}';
//    }
//}
