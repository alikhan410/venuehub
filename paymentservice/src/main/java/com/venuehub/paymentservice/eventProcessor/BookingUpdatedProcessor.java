package com.venuehub.paymentservice.eventProcessor;

import com.venuehub.broker.event.ProcessEvent;
import com.venuehub.broker.event.booking.BookingUpdated;
import com.venuehub.broker.event.booking.BookingUpdatedEvent;
import com.venuehub.commons.exception.NoSuchBookingException;
import com.venuehub.paymentservice.model.BookedVenue;
import com.venuehub.paymentservice.repository.BookedVenueRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

@Qualifier("BookingUpdated")
public class BookingUpdatedProcessor implements ProcessEvent {
    @Autowired
    private BookedVenueRepository bookedVenueRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(BookingUpdatedProcessor.class);

    @Override
    public void process(Object event) {
        checkAnnotation(event);
        BookingUpdatedEvent bookingUpdatedEvent = (BookingUpdatedEvent) event;
        LOGGER.info("BookingUpdatedEvent reached BookingUpdatedProcessor " + bookingUpdatedEvent);

        BookedVenue booking = bookedVenueRepository.findById(bookingUpdatedEvent.bookingId()).orElseThrow(() -> new NoSuchBookingException());
        booking.setStatus(bookingUpdatedEvent.status());
        bookedVenueRepository.save(booking);

    }

    @Override
    public void checkAnnotation(Object event) throws IllegalArgumentException {
        if (event.getClass().isAnnotationPresent(BookingUpdated.class)) {
            throw new IllegalArgumentException("Invalid event type. Expected BookingUpdatedEvent");
        }
    }
}
