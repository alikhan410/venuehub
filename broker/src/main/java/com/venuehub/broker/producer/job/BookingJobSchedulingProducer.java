package com.venuehub.broker.producer.job;

import com.venuehub.broker.constants.MyExchange;
import com.venuehub.broker.event.job.BookingJobSchedulingEvent;
import com.venuehub.broker.producer.BaseProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookingJobSchedulingProducer extends BaseProducer<BookingJobSchedulingEvent> {
    private final RabbitTemplate rabbitTemplate;
    private static final Logger LOGGER = LoggerFactory.getLogger(BookingJobSchedulingProducer.class);

    @Autowired
    public BookingJobSchedulingProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void produce(BookingJobSchedulingEvent event, MyExchange exchange) {
        rabbitTemplate.convertAndSend(exchange.name(),"booking-job-scheduling",event);
        LOGGER.info("Message sent from "+getClass().getSimpleName()+" to " + exchange.name());
    }
}
