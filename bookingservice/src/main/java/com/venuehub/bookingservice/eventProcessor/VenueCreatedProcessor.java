package com.venuehub.bookingservice.eventProcessor;

import com.venuehub.bookingservice.model.Venue;
import com.venuehub.bookingservice.service.VenueService;
import com.venuehub.broker.event.ProcessEvent;
import com.venuehub.broker.event.venue.VenueCreated;
import com.venuehub.broker.event.venue.VenueCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@Qualifier("VenueCreatedProcessor")
public class VenueCreatedProcessor implements ProcessEvent {
    private static final Logger LOGGER = LoggerFactory.getLogger(VenueCreatedProcessor.class);
    private final VenueService venueService;
    public VenueCreatedProcessor(VenueService venueService) {
        this.venueService = venueService;
    }

    @Override
    public void process(Object event) {
        checkAnnotation(event);
        VenueCreatedEvent venueCreatedEvent = (VenueCreatedEvent) event;
        LOGGER.info("VenueCreatedEvent reached VenueCreatedProcessor " + venueCreatedEvent);
        Venue venue = new Venue(venueCreatedEvent.venueId(), venueCreatedEvent.username());
        venueService.save(venue);
    }

    public void checkAnnotation(Object event) throws IllegalArgumentException {
        if (!event.getClass().isAnnotationPresent(VenueCreated.class)) {
            throw new IllegalArgumentException("Invalid event type. Expected VenueCreatedEvent");
        }
    }

}