package com.venuehub.broker.event.image;

public record ImageCreatedEvent(Long imageId, String uri, String venueName, String vendorName) {
}
