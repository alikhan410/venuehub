package com.venuehub.broker.consumer.venue;

import com.venuehub.broker.constants.MyQueue;
import com.venuehub.broker.consumer.BaseConsumer;
import com.venuehub.broker.event.ProcessEvent;
import com.venuehub.broker.event.venue.VenueUpdatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
@Lazy
public class VenueUpdatedConsumer extends BaseConsumer<VenueUpdatedEvent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(VenueUpdatedConsumer.class);
    private ProcessEvent processEvent;

    @Autowired(required = false) // Optional dependency
    public void setProcessEvent(@Qualifier("VenueUpdatedProcessor") ProcessEvent processEvent) {
        this.processEvent = processEvent;
    }
    @Override
    @RabbitListener(queues = MyQueue.Constants.VENUE_QUEUE_VALUE)
    public void consume(VenueUpdatedEvent event) {
        LOGGER.info(getClass().getName() + " Message Consumed " + event.toString());

        if (processEvent == null) {
            LOGGER.info("processEvent in " + getClass().getName() + " is null");
        } else {
            processEvent.process(event);
        }
    }
}