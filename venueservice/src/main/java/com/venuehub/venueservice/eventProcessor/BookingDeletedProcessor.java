package com.venuehub.venueservice.eventProcessor;

import com.venuehub.broker.event.ProcessEvent;
import com.venuehub.broker.event.booking.BookingDeleted;
import com.venuehub.broker.event.booking.BookingDeletedEvent;
import com.venuehub.venueservice.service.BookedVenueService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@Qualifier("BookingDeletedProcessor")
public class BookingDeletedProcessor implements ProcessEvent {
    private final Logger LOGGER = LoggerFactory.getLogger(BookingDeletedProcessor.class);
    private final BookedVenueService bookedVenueService;

    public BookingDeletedProcessor(BookedVenueService bookedVenueService) {
        this.bookedVenueService = bookedVenueService;
    }

    @Override
    public void process(Object event) {
        checkAnnotation(event);
        BookingDeletedEvent bookingDeletedEvent = (BookingDeletedEvent) event;
        LOGGER.info("BookingDeletedEvent reached BookingDeletedProcessor "+ bookingDeletedEvent);
        bookedVenueService.deleteById(bookingDeletedEvent.id());
    }

    @Override
    public void checkAnnotation(Object event) throws IllegalArgumentException {
        if (!event.getClass().isAnnotationPresent(BookingDeleted.class)) {
            throw new IllegalArgumentException("Invalid event type. Expected BookingDeletedEvent");
        }
    }
}
