package com.venuehub.paymentservice.consumer;

import com.venuehub.broker.constants.MyQueue;
import com.venuehub.broker.consumer.BaseConsumer;
import com.venuehub.broker.event.booking.BookingCreatedEvent;
import com.venuehub.paymentservice.model.Booking;
import com.venuehub.paymentservice.model.BookingOrder;
import com.venuehub.paymentservice.service.BookedVenueService;
import com.venuehub.paymentservice.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookingCreatedConsumer extends BaseConsumer<BookingCreatedEvent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(BookingCreatedConsumer.class);
    private final BookedVenueService bookedVenueService;
    private final OrderService orderService;

    public BookingCreatedConsumer(BookedVenueService bookedVenueService, OrderService orderService) {
        this.bookedVenueService = bookedVenueService;
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
        bookedVenueService.save(booking);
    }
}
