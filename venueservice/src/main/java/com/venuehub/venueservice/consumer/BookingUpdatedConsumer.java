package com.venuehub.venueservice.consumer;

import com.venuehub.broker.constants.MyQueue;
import com.venuehub.broker.consumer.BaseConsumer;
import com.venuehub.broker.event.booking.BookingUpdatedEvent;
import com.venuehub.commons.exception.NoSuchBookingException;
import com.venuehub.venueservice.model.Booking;
import com.venuehub.venueservice.service.BookingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile({"dev", "prod"})
public class BookingUpdatedConsumer  extends BaseConsumer<BookingUpdatedEvent> {
    private final Logger LOGGER = LoggerFactory.getLogger(BookingUpdatedConsumer.class);
    private final BookingService bookingService;

    public BookingUpdatedConsumer(BookingService bookingService) {
        this.bookingService = bookingService;
    }
    @Override
    @RabbitListener(queues = MyQueue.Constants.BOOKING_UPDATED_QUEUE_VENUE_SERVICE)
    public void consume(BookingUpdatedEvent event) {
        LOGGER.info("{} reached {} {}", event.getClass().getSimpleName(), getClass().getSimpleName(), event);

        Booking booking = bookingService.findById(event.bookingId()).orElseThrow(NoSuchBookingException::new);
        booking.setStatus(event.status());
        bookingService.save(booking);

    }
}
