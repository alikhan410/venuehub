package com.venuehub.paymentservice.consumer;

import com.venuehub.broker.constants.MyQueue;
import com.venuehub.broker.consumer.BaseConsumer;
import com.venuehub.broker.event.booking.BookingCreatedEvent;
import com.venuehub.paymentservice.model.Booking;
import com.venuehub.paymentservice.service.BookingService;
import com.venuehub.paymentservice.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class BookingCreatedConsumer extends BaseConsumer<BookingCreatedEvent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(BookingCreatedConsumer.class);
    private final BookingService bookingService;
    private final OrderService orderService;

    public BookingCreatedConsumer(BookingService bookingService, OrderService orderService) {
        this.bookingService = bookingService;
        this.orderService = orderService;
    }

    @Override
    @RabbitListener(queues = MyQueue.Constants.BOOKING_CREATED_QUEUE_PAYMENT_SERVICE)
    public void consume(BookingCreatedEvent event) {
        LOGGER.info(event.getClass().getSimpleName() + " reached " + getClass().getSimpleName()+" " + event);
        Booking booking = new Booking();
        booking.setId(event.bookingId());
        booking.setStatus(event.status());
        booking.setBookingFee(event.bookingFee());
        booking.setUsername(event.username());
        bookingService.save(booking);
    }
}
