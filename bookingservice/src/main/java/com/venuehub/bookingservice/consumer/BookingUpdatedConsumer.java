package com.venuehub.bookingservice.consumer;

import com.venuehub.bookingservice.model.BookedVenue;
import com.venuehub.bookingservice.service.BookedVenueService;
import com.venuehub.broker.constants.MyQueue;
import com.venuehub.broker.consumer.BaseConsumer;
import com.venuehub.broker.event.booking.BookingCreatedEvent;
import com.venuehub.broker.event.booking.BookingUpdatedEvent;
import com.venuehub.commons.exception.NoSuchBookingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class BookingUpdatedConsumer extends BaseConsumer<BookingUpdatedEvent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(BookingUpdatedConsumer.class);
    private final BookedVenueService bookedVenueService;

    public BookingUpdatedConsumer(BookedVenueService bookedVenueService) {
        this.bookedVenueService = bookedVenueService;
    }
    @Override
    @RabbitListener(queues = MyQueue.Constants.BOOKING_UPDATED_QUEUE_BOOKING_SERVICE)
    public void consume(BookingUpdatedEvent event) {

        LOGGER.info(event.getClass().getName() +" reached "+ getClass().getName() + event);

        BookedVenue booking = bookedVenueService.findById(event.bookingId()).orElseThrow(NoSuchBookingException::new);
        booking.setStatus(event.status());
        bookedVenueService.save(booking);

    }
}
