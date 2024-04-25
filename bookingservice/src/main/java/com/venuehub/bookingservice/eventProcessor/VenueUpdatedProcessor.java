package com.venuehub.bookingservice.eventProcessor;

import com.venuehub.broker.event.ProcessEvent;
import com.venuehub.broker.event.venue.VenueUpdated;
import com.venuehub.broker.event.venue.VenueUpdatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@Qualifier("VenueUpdatedProcessor")
public class VenueUpdatedProcessor implements ProcessEvent {
    private static final Logger LOGGER = LoggerFactory.getLogger(VenueUpdatedProcessor.class);

    @Override
    public void process(Object event) {
        checkAnnotation(event);
        VenueUpdatedEvent venueUpdatedEvent = (VenueUpdatedEvent) event;
        LOGGER.info("VenueUpdatedEvent reached VenueUpdatedProcessor " + venueUpdatedEvent);
        // TODO Add an Venue Updated Processor
    }

    @Override
    public void checkAnnotation(Object event) throws IllegalArgumentException {
        if (!event.getClass().isAnnotationPresent(VenueUpdated.class)) {
            throw new IllegalArgumentException("Invalid event type. Expected VenueUpdatedEvent");
        }
    }
}