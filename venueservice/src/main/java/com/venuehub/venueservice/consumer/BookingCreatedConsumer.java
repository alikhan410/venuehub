package com.venuehub.venueservice.consumer;

import com.venuehub.broker.constants.MyQueue;
import com.venuehub.broker.consumer.BaseConsumer;
import com.venuehub.broker.event.booking.BookingCreatedEvent;
import com.venuehub.commons.exception.NoSuchVenueException;
import com.venuehub.venueservice.model.Booking;
import com.venuehub.venueservice.model.Venue;
import com.venuehub.venueservice.service.BookingService;
import com.venuehub.venueservice.service.VenueService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class BookingCreatedConsumer extends BaseConsumer<BookingCreatedEvent> {
    private final Logger LOGGER = LoggerFactory.getLogger(BookingCreatedConsumer.class);
    private final BookingService bookingService;
    private final VenueService venueService;

    public BookingCreatedConsumer(BookingService bookingService, VenueService venueService) {
        this.bookingService = bookingService;
        this.venueService = venueService;
    }

    @Override
    @RabbitListener(queues = MyQueue.Constants.BOOKING_CREATED_QUEUE_VENUE_SERVICE)
    public void consume(BookingCreatedEvent event) {
        LOGGER.info("{} reached {} {}", event.getClass().getSimpleName(), getClass().getSimpleName(), event);

        Booking booking = new Booking();
        booking.setId(event.bookingId());
        booking.setStatus(event.status());
        booking.setUsername(event.username());

        Venue venue = venueService.findById(event.venueId()).orElseThrow(NoSuchVenueException::new);
        booking.setVenue(venue);


        bookingService.save(booking);

    }
}
