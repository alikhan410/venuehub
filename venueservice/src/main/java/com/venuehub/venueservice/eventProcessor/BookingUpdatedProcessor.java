package com.venuehub.venueservice.eventProcessor;

import com.venuehub.broker.event.ProcessEvent;
import com.venuehub.broker.event.booking.BookingUpdated;
import com.venuehub.broker.event.booking.BookingUpdatedEvent;
import com.venuehub.commons.exception.NoSuchBookingException;
import com.venuehub.venueservice.model.BookedVenue;
import com.venuehub.venueservice.service.BookedVenueService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@Qualifier("BookingUpdatedProcessor")
public class BookingUpdatedProcessor implements ProcessEvent {
    private final Logger LOGGER = LoggerFactory.getLogger(BookingUpdatedProcessor.class);
    private final BookedVenueService bookedVenueService;

    public BookingUpdatedProcessor(BookedVenueService bookedVenueService) {
        this.bookedVenueService = bookedVenueService;
    }

    @Override
    public void process(Object event) {
        checkAnnotation(event);
        BookingUpdatedEvent bookingUpdatedEvent = (BookingUpdatedEvent) event;
        LOGGER.info("BookingCreatedEvent reached BookingCreatedProcessor " + bookingUpdatedEvent);

        BookedVenue bookedVenue = bookedVenueService.findById(bookingUpdatedEvent.bookingId()).orElseThrow(NoSuchBookingException::new);
        bookedVenue.setStatus(bookingUpdatedEvent.status());
        bookedVenueService.save(bookedVenue);
    }

    @Override
    public void checkAnnotation(Object event) throws IllegalArgumentException {
        if (!event.getClass().isAnnotationPresent(BookingUpdated.class)) {
            throw new IllegalArgumentException("Invalid event type. Expected BookingUpdatedEvent");
        }
    }
}
