package com.venuehub.broker.event.venue;

@VenueCreated
public record VenueCreatedEvent(Long venueId, String username) {
}
//public class VenueCreatedEvent implements Serializable {
//    private Long venueId;
//    public VenueCreatedEvent(Long venueId) {
//        this.venueId = venueId;
//    }
//    public VenueCreatedEvent() {
//    }
//    public Long getVenueId() {
//        return venueId;
//    }
//
//    @Override
//    public String toString() {
//        return "VenueCreatedEvent{" +
//                "venueId=" + venueId +
//                '}';
//    }
//}

