package com.venuehub.broker.producer.booking;

import com.venuehub.broker.constants.MyExchange;
import com.venuehub.broker.event.booking.BookingCreatedEvent;
import com.venuehub.broker.producer.BaseProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookingCreatedProducer extends BaseProducer<BookingCreatedEvent> {
    private final RabbitTemplate rabbitTemplate;
    private static final Logger LOGGER = LoggerFactory.getLogger(BookingCreatedProducer.class);

    @Autowired
    public BookingCreatedProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void produce(BookingCreatedEvent event, MyExchange exchange) {
        rabbitTemplate.convertAndSend(exchange.name(),"booking-created",event);
        LOGGER.info("Message sent from booking-created-producer");
    }
}
