package com.venuehub.bookingservice.eventProcessor;

import com.venuehub.bookingservice.model.BookedVenue;
import com.venuehub.bookingservice.service.BookedVenueService;
import com.venuehub.broker.event.ProcessEvent;
import com.venuehub.broker.event.booking.BookingUpdated;
import com.venuehub.broker.event.booking.BookingUpdatedEvent;
import com.venuehub.commons.exception.NoSuchBookingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;

@Qualifier("BookingUpdatedProcessor")
public class BookingUpdatedProcessor implements ProcessEvent {

    private final BookedVenueService bookedVenueService;
    private static final Logger LOGGER = LoggerFactory.getLogger(BookingUpdatedProcessor.class);
    public BookingUpdatedProcessor(BookedVenueService bookedVenueService) {
        this.bookedVenueService = bookedVenueService;
    }

    @Override
    public void process(Object event) {
        checkAnnotation(event);
        BookingUpdatedEvent bookingUpdatedEvent = (BookingUpdatedEvent) event;
        LOGGER.info("BookingUpdatedEvent reached BookingUpdatedProcessor " + bookingUpdatedEvent);

        BookedVenue booking = bookedVenueService.findById(bookingUpdatedEvent.bookingId()).orElseThrow(NoSuchBookingException::new);
        booking.setStatus(bookingUpdatedEvent.status());
        bookedVenueService.save(booking);

    }

    @Override
    public void checkAnnotation(Object event) throws IllegalArgumentException {
        if (event.getClass().isAnnotationPresent(BookingUpdated.class)) {
            throw new IllegalArgumentException("Invalid event type. Expected BookingUpdatedEvent");
        }
    }
}
