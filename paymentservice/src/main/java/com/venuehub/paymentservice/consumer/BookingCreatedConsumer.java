package com.venuehub.paymentservice.consumer;

import com.venuehub.broker.constants.MyQueue;
import com.venuehub.broker.consumer.BaseConsumer;
import com.venuehub.broker.event.booking.BookingCreatedEvent;
import com.venuehub.paymentservice.model.BookedVenue;
import com.venuehub.paymentservice.service.BookedVenueService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class BookingCreatedConsumer extends BaseConsumer<BookingCreatedEvent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(BookingCreatedConsumer.class);
    private final BookedVenueService bookedVenueService;

    public BookingCreatedConsumer(BookedVenueService bookedVenueService) {
        this.bookedVenueService = bookedVenueService;
    }

    @Override
    @RabbitListener(queues = MyQueue.Constants.BOOKING_CREATED_QUEUE_PAYMENT_SERVICE)
    public void consume(BookingCreatedEvent event) {
        LOGGER.info(event.getClass().getSimpleName() + " reached " + getClass().getSimpleName() + event);
        BookedVenue bookedVenue = new BookedVenue();
        bookedVenue.setId(event.bookingId());
        bookedVenue.setStatus(event.status());
        bookedVenue.setUsername(event.username());
        bookedVenueService.save(bookedVenue);
    }
}
