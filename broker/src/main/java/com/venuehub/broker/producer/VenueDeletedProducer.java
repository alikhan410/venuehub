package com.venuehub.broker.producer;

import com.venuehub.broker.constants.MyExchange;
import com.venuehub.broker.event.venue.VenueDeletedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VenueDeletedProducer extends BaseProducer<VenueDeletedEvent> {
    private final RabbitTemplate rabbitTemplate;
    private static final Logger LOGGER = LoggerFactory.getLogger(VenueDeletedProducer.class);

    @Autowired
    public VenueDeletedProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void produce(VenueDeletedEvent event) {
        rabbitTemplate.convertAndSend(MyExchange.VENUE_EXCHANGE.name(),"venue",event);
        LOGGER.info("Message sent from venue-deleted-producer");
    }
}
