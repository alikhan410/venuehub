package com.venuehub.broker.producer;

import com.venuehub.broker.constants.MyExchange;
import com.venuehub.broker.event.venue.VenueCreatedEvent;
import com.venuehub.broker.event.venue.VenueDeletedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VenueCreatedProducer extends BaseProducer<VenueCreatedEvent> {
    private final RabbitTemplate rabbitTemplate;
    private static final Logger LOGGER = LoggerFactory.getLogger(VenueCreatedProducer.class);

    @Autowired
    public VenueCreatedProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void produce(VenueCreatedEvent event) {
        rabbitTemplate.convertAndSend(MyExchange.VENUE_EXCHANGE.name(),"venue",event);
        LOGGER.info("Message sent from venue-created-producer");
    }
}

