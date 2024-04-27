package com.venuehub.bookingservice.consumer;

import com.venuehub.bookingservice.service.VenueService;
import com.venuehub.broker.constants.MyQueue;
import com.venuehub.broker.consumer.BaseConsumer;
import com.venuehub.broker.event.venue.VenueCreatedEvent;
import com.venuehub.broker.event.venue.VenueDeletedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class VenueDeletedConsumer extends BaseConsumer<VenueDeletedEvent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(VenueDeletedConsumer.class);
    private final VenueService venueService;
    public VenueDeletedConsumer(VenueService venueService) {
        this.venueService = venueService;
    }
    @Override
    @RabbitListener(queues = MyQueue.Constants.VENUE_DELETED_QUEUE_BOOKING_SERVICE)
    public void consume(VenueDeletedEvent event) {
        LOGGER.info(event.getClass().getName() + " reached " + getClass().getName() + event);
        venueService.deleteById(event.venueId());
        //TODO check if this deletes the associated bookings too

    }
}
