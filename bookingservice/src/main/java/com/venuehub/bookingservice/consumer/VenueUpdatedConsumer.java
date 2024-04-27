package com.venuehub.bookingservice.consumer;

import com.venuehub.bookingservice.model.Venue;
import com.venuehub.bookingservice.service.VenueService;
import com.venuehub.broker.constants.MyQueue;
import com.venuehub.broker.consumer.BaseConsumer;
import com.venuehub.broker.event.venue.VenueCreatedEvent;
import com.venuehub.broker.event.venue.VenueUpdatedEvent;
import com.venuehub.commons.exception.NoSuchVenueException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class VenueUpdatedConsumer extends BaseConsumer<VenueUpdatedEvent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(VenueUpdatedConsumer.class);
    private final VenueService venueService;

    public VenueUpdatedConsumer(VenueService venueService) {
        this.venueService = venueService;
    }
    @Override
    @RabbitListener(queues = MyQueue.Constants.VENUE_UPDATED_QUEUE_BOOKING_SERVICE)
    public void consume(VenueUpdatedEvent event) {
        LOGGER.info(event.getClass().getName() + " reached " + getClass().getName() + event);
        Venue venue = venueService.findById(event.venueId()).orElseThrow(NoSuchVenueException::new);
        // TODO venue updating logic

    }
}