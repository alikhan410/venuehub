package com.venuehub.broker.producer.venue;

import com.venuehub.broker.constants.MyExchange;
import com.venuehub.broker.event.venue.VenueUpdatedEvent;
import com.venuehub.broker.producer.BaseProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VenueUpdatedProducer extends BaseProducer<VenueUpdatedEvent> {
    private final RabbitTemplate rabbitTemplate;
    private static final Logger LOGGER = LoggerFactory.getLogger(VenueUpdatedProducer.class);

    @Autowired
    public VenueUpdatedProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void produce(VenueUpdatedEvent event, MyExchange exchange) {
        rabbitTemplate.convertAndSend(exchange.name(),"venue-updated",event);
        LOGGER.info("Message sent from "+getClass().getSimpleName()+" to " + exchange.name());
    }
}
