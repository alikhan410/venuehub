package com.venuehub.broker.producer.image;

import com.venuehub.broker.constants.MyExchange;
import com.venuehub.broker.event.image.ImageCreatedEvent;
import com.venuehub.broker.producer.BaseProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ImageCreatedProducer extends BaseProducer<ImageCreatedEvent> {
    private final RabbitTemplate rabbitTemplate;
    private static final Logger LOGGER = LoggerFactory.getLogger(ImageCreatedProducer.class);

    @Autowired
    public ImageCreatedProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void produce(ImageCreatedEvent event, MyExchange exchange) {
        rabbitTemplate.convertAndSend(exchange.name(), "image-created", event);
        LOGGER.info("Message sent from " + getClass().getSimpleName() + " to " + exchange.name());
    }
}
