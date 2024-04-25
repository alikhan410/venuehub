package com.venuehub.broker.producer;

import com.venuehub.broker.constants.MyExchange;
import com.venuehub.broker.event.booking.BookingCreatedEvent;
import com.venuehub.broker.event.booking.BookingUpdatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookingUpdatedProducer extends BaseProducer<BookingUpdatedEvent>{
    private final RabbitTemplate rabbitTemplate;
    private static final Logger LOGGER = LoggerFactory.getLogger(BookingUpdatedProducer.class);

    @Autowired
    public BookingUpdatedProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void produce(BookingUpdatedEvent event) {
        rabbitTemplate.convertAndSend(MyExchange.BOOKING_EXCHANGE.name(),"booking",event);
        LOGGER.info("Message sent from booking-updated-producer");
    }
}
