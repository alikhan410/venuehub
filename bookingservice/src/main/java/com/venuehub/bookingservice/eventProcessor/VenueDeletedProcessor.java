package com.venuehub.bookingservice.eventProcessor;

import com.venuehub.bookingservice.service.VenueService;
import com.venuehub.broker.event.ProcessEvent;
import com.venuehub.broker.event.venue.VenueDeleted;
import com.venuehub.broker.event.venue.VenueDeletedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@Qualifier("VenueDeletedProcessor")
public class VenueDeletedProcessor implements ProcessEvent {
    private static final Logger LOGGER = LoggerFactory.getLogger(VenueDeletedProcessor.class);
    private final VenueService venueService;

    public VenueDeletedProcessor(VenueService venueService) {
        this.venueService = venueService;
    }

    @Override
    public void process(Object event) {
        checkAnnotation(event);
        VenueDeletedEvent venueDeletedEvent = (VenueDeletedEvent) event;
        LOGGER.info("VenueDeletedEvent reached VenueDeletedProcessor " + venueDeletedEvent);
        venueService.deleteById(venueDeletedEvent.venueId());
        //TODO check if this deletes the associated bookings too
    }

    @Override
    public void checkAnnotation(Object event) throws IllegalArgumentException {
        if (!event.getClass().isAnnotationPresent(VenueDeleted.class)) {
            throw new IllegalArgumentException("Invalid event type. Expected VenueDeletedEvent");
        }
    }

}
