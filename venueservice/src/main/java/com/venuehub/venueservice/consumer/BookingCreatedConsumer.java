package com.venuehub.venueservice.consumer;

import com.venuehub.broker.constants.MyQueue;
import com.venuehub.broker.consumer.BaseConsumer;
import com.venuehub.broker.event.booking.BookingCreatedEvent;
import com.venuehub.commons.exception.NoSuchVenueException;
import com.venuehub.venueservice.model.BookedVenue;
import com.venuehub.venueservice.model.Venue;
import com.venuehub.venueservice.service.BookedVenueService;
import com.venuehub.venueservice.service.VenueService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class BookingCreatedConsumer extends BaseConsumer<BookingCreatedEvent> {
    private final Logger LOGGER = LoggerFactory.getLogger(BookingCreatedConsumer.class);
    private final BookedVenueService bookedVenueService;
    private final VenueService venueService;

    public BookingCreatedConsumer(BookedVenueService bookedVenueService, VenueService venueService) {
        this.bookedVenueService = bookedVenueService;
        this.venueService = venueService;
    }

    @Override
    @RabbitListener(queues = MyQueue.Constants.BOOKING_CREATED_QUEUE_VENUE_SERVICE)
    public void consume(BookingCreatedEvent event) {
        LOGGER.info(event.getClass().getSimpleName() + " reached " + getClass().getSimpleName() + event);
        BookedVenue bookedVenue = new BookedVenue();
        bookedVenue.setId(event.bookingId());
        bookedVenue.setStatus(event.status());
        bookedVenue.setUsername(event.username());

        Venue venue = venueService.findById(event.venueId()).orElseThrow(NoSuchVenueException::new);
        bookedVenue.setVenue(venue);

        bookedVenueService.save(bookedVenue);

    }
}
