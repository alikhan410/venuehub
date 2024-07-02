package com.venuehub.jobservice.consumer;

import com.venuehub.broker.constants.MyQueue;
import com.venuehub.broker.consumer.BaseConsumer;
import com.venuehub.broker.event.booking.BookingUpdatedEvent;
import com.venuehub.commons.exception.NoSuchBookingException;
import com.venuehub.jobservice.entity.Booking;
import com.venuehub.jobservice.service.BookingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookingUpdatedConsumer extends BaseConsumer<BookingUpdatedEvent> {
    private final Logger LOGGER = LoggerFactory.getLogger(BookingUpdatedConsumer.class);
    private final BookingService bookedVenueService;

    @Autowired
    public BookingUpdatedConsumer(BookingService bookedVenueService) {
        this.bookedVenueService = bookedVenueService;
    }

    @Override
    @RabbitListener(queues = MyQueue.Constants.BOOKING_UPDATED_QUEUE_JOB_SERVICE)
    public void consume(BookingUpdatedEvent event) {

        LOGGER.info("{} reached {} {}", event.getClass().getSimpleName(), getClass().getSimpleName(), event);

        Booking booking = bookedVenueService.findById(event.bookingId()).orElseThrow(NoSuchBookingException::new);

        booking.setStatus(event.status());

        bookedVenueService.save(booking);
    }
}
