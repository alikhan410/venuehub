package com.venuehub.broker.producer;

import com.venuehub.broker.constants.MyExchange;
import com.venuehub.broker.event.user.UserCreatedEvent;
import com.venuehub.broker.event.venue.VenueCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserCreatedProducer extends BaseProducer<UserCreatedEvent> {
    private final RabbitTemplate rabbitTemplate;
    private static final Logger LOGGER = LoggerFactory.getLogger(UserCreatedProducer.class);

    @Autowired
    public UserCreatedProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void produce(UserCreatedEvent event) {
        rabbitTemplate.convertAndSend(MyExchange.USER_EXCHANGE.name(),"user",event);
        LOGGER.info("Message sent from user-created-producer");
    }
}
