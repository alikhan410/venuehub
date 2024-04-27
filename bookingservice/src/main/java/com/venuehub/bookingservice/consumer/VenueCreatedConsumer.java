package com.venuehub.bookingservice.consumer;

import com.venuehub.bookingservice.model.Venue;
import com.venuehub.bookingservice.service.VenueService;
import com.venuehub.broker.constants.MyQueue;
import com.venuehub.broker.consumer.BaseConsumer;
import com.venuehub.broker.event.venue.VenueCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class VenueCreatedConsumer extends BaseConsumer<VenueCreatedEvent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(VenueCreatedConsumer.class);
    private final VenueService venueService;

    public VenueCreatedConsumer(VenueService venueService) {
        this.venueService = venueService;
    }

    @Override
    @RabbitListener(queues = MyQueue.Constants.VENUE_CREATED_QUEUE_BOOKING_SERVICE)
    public void consume(VenueCreatedEvent event) {
        LOGGER.info(event.getClass().getName() + " reached " + getClass().getName() + event);
        Venue venue = new Venue(event.venueId(), event.username());
        venueService.save(venue);

    }
}
