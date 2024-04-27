package com.venuehub.broker.producer.booking;

import com.venuehub.broker.constants.MyExchange;
import com.venuehub.broker.event.booking.BookingUpdatedEvent;
import com.venuehub.broker.producer.BaseProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookingUpdatedProducer extends BaseProducer<BookingUpdatedEvent> {
    private final RabbitTemplate rabbitTemplate;
    private static final Logger LOGGER = LoggerFactory.getLogger(BookingUpdatedProducer.class);

    @Autowired
    public BookingUpdatedProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    //TODO think of another way to send a message to multiple exchange
    public void produce(BookingUpdatedEvent event, MyExchange exchange) {
        rabbitTemplate.convertAndSend(exchange.name(), "booking-updated", event);
        LOGGER.info("Message sent from booking-updated-producer");
    }

}
