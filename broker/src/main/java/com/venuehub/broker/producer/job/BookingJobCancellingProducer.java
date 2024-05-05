package com.venuehub.broker.producer.job;

import com.venuehub.broker.constants.MyExchange;
import com.venuehub.broker.event.job.BookingJobCancellingEvent;
import com.venuehub.broker.producer.BaseProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookingJobCancellingProducer extends BaseProducer<BookingJobCancellingEvent> {
    private final RabbitTemplate rabbitTemplate;
    private static final Logger LOGGER = LoggerFactory.getLogger(BookingJobCancellingProducer.class);

    @Autowired
    public BookingJobCancellingProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void produce(BookingJobCancellingEvent event, MyExchange exchange) {
        rabbitTemplate.convertAndSend(exchange.name(),"booking-job-cancelling",event);
        LOGGER.info("Message sent from "+getClass().getSimpleName()+" to " + exchange.name());
    }
}

