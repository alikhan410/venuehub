package com.venuehub.paymentservice.consumer;

import com.venuehub.broker.constants.MyQueue;
import com.venuehub.broker.consumer.BaseConsumer;
import com.venuehub.broker.event.booking.BookingUpdatedEvent;
import com.venuehub.commons.exception.NoSuchBookingException;
import com.venuehub.paymentservice.model.Booking;
import com.venuehub.paymentservice.service.BookedVenueService;
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
    @RabbitListener(queues = MyQueue.Constants.BOOKING_UPDATED_QUEUE_PAYMENT_SERVICE)
    public void consume(BookingUpdatedEvent event) {
        LOGGER.info(event.getClass().getSimpleName() + " reached " + getClass().getSimpleName()+" " + event);
        Booking booking = bookedVenueService.findById(event.bookingId()).orElseThrow(NoSuchBookingException::new);
        booking.setStatus(event.status());
        bookedVenueService.save(booking);
    }
}
