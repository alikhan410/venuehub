package com.venuehub.venueservice.eventProcessor;

import com.venuehub.broker.constants.BookingStatus;
import com.venuehub.broker.event.ProcessEvent;
import com.venuehub.broker.event.booking.BookingCreated;
import com.venuehub.broker.event.booking.BookingCreatedEvent;
import com.venuehub.commons.exception.NoSuchVenueException;
import com.venuehub.venueservice.model.BookedVenue;
import com.venuehub.venueservice.model.Venue;
import com.venuehub.venueservice.service.BookedVenueService;
import com.venuehub.venueservice.service.VenueService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Qualifier("BookingCreatedProcessor")
public class BookingCreatedProcessor implements ProcessEvent {

    private final Logger LOGGER = LoggerFactory.getLogger(BookingCreatedProcessor.class);
    private final BookedVenueService bookedVenueService;
    private final VenueService venueService;

    @Autowired
    public BookingCreatedProcessor(BookedVenueService bookedVenueService, VenueService venueService) {
        this.bookedVenueService = bookedVenueService;
        this.venueService = venueService;
    }

    @Override
    public void process(Object event) {
        checkAnnotation(event);
        BookingCreatedEvent bookingCreatedEvent = (BookingCreatedEvent) event;
        LOGGER.info("BookingCreatedEvent reached BookingCreatedProcessor " + bookingCreatedEvent);

        BookedVenue bookedVenue = new BookedVenue();
        bookedVenue.setId(bookingCreatedEvent.bookingId());
        bookedVenue.setStatus(bookingCreatedEvent.status());
        bookedVenue.setUsername(bookingCreatedEvent.username());

        Venue venue = venueService.findById(bookingCreatedEvent.venueId()).orElseThrow(NoSuchVenueException::new);
        bookedVenue.setVenue(venue);

        bookedVenueService.save(bookedVenue);

    }

    @Override
    public void checkAnnotation(Object event) throws IllegalArgumentException {
        if (!event.getClass().isAnnotationPresent(BookingCreated.class)) {
            throw new IllegalArgumentException("Invalid event type. Expected BookingCreatedEvent");
        }
    }
}
