package com.venuehub.paymentservice.eventProcessor;

import com.venuehub.broker.event.ProcessEvent;
import com.venuehub.broker.event.booking.BookingCreated;
import com.venuehub.broker.event.booking.BookingCreatedEvent;
import com.venuehub.paymentservice.model.BookedVenue;
import com.venuehub.paymentservice.service.BookedVenueService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@Qualifier("BookingCreatedProcessor")
public class BookingCreatedProcessor implements ProcessEvent {

    private final Logger LOGGER = LoggerFactory.getLogger(BookingCreatedProcessor.class);
    private final BookedVenueService bookedVenueService;

    public BookingCreatedProcessor(BookedVenueService bookedVenueService) {
        this.bookedVenueService = bookedVenueService;
    }

    @Override
    public void process(Object event) {
        checkAnnotation(event);
        BookingCreatedEvent bookingCreatedEvent = (BookingCreatedEvent) event;
        LOGGER.info("BookingCreatedEvent reached BookingCreatedProcessor " + bookingCreatedEvent );

        BookedVenue bookedVenue = new BookedVenue();
        bookedVenue.setId(bookingCreatedEvent.bookingId());
        bookedVenue.setStatus(bookingCreatedEvent.status());
        bookedVenue.setUsername(bookingCreatedEvent.username());
        bookedVenueService.save(bookedVenue);

    }

    @Override
    public void checkAnnotation(Object event) throws IllegalArgumentException {
        if (!event.getClass().isAnnotationPresent(BookingCreated.class)) {
            throw new IllegalArgumentException("Invalid event type. Expected BookingCreatedEvent");
        }
    }
}
