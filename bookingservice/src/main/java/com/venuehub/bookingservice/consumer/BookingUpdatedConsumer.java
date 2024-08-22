package com.venuehub.bookingservice.consumer;

import com.venuehub.bookingservice.model.Booking;
import com.venuehub.bookingservice.service.BookingService;
import com.venuehub.broker.constants.BookingStatus;
import com.venuehub.broker.constants.MyQueue;
import com.venuehub.broker.consumer.BaseConsumer;
import com.venuehub.broker.event.booking.BookingUpdatedEvent;
import com.venuehub.commons.exception.NoSuchBookingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
@Profile({"dev", "prod"})
public class BookingUpdatedConsumer extends BaseConsumer<BookingUpdatedEvent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(BookingUpdatedConsumer.class);
    private final BookingService bookingService;

    public BookingUpdatedConsumer(BookingService bookingService) {
        this.bookingService = bookingService;
    }
    @Override
    @RabbitListener(queues = MyQueue.Constants.BOOKING_UPDATED_QUEUE_BOOKING_SERVICE)
    public void consume(BookingUpdatedEvent event) {

        LOGGER.info(event.getClass().getSimpleName() + " reached " + getClass().getSimpleName()+" " + event);

        Booking booking = bookingService.findById(event.bookingId()).orElseThrow(NoSuchBookingException::new);
        booking.setStatus(event.status());
        if (event.status().equals(BookingStatus.BOOKED)){
            //Will set the reservation time to NOW. which means there's no reservation time left
            booking.setReservationExpiry(LocalDateTime.now(ZoneId.of("Asia/Karachi")).toString());
        }
        bookingService.save(booking);

    }
}
