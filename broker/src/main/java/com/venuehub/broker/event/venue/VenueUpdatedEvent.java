package com.venuehub.broker.event.venue;

@VenueUpdated
public record VenueUpdatedEvent(Long venueId,String name, byte[] image, int capacity, String phone, String estimate ) {
}
//public class VenueUpdatedEvent {
//    private final Long venueId;
//
//    public VenueUpdatedEvent(Long venueId) {
//        this.venueId = venueId;
//    }
//
//    public Long getVenueId() {
//        return venueId;
//    }
//
//    @Override
//    public String toString() {
//        return "VenueUpdatedEvent{" +
//                "venueId=" + venueId +
//                '}';
//    }
//}
