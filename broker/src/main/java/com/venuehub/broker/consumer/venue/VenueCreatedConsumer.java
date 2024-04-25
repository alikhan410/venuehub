package com.venuehub.broker.consumer.venue;

import com.venuehub.broker.constants.MyQueue;
import com.venuehub.broker.consumer.BaseConsumer;
import com.venuehub.broker.event.ProcessEvent;
import com.venuehub.broker.event.venue.VenueCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
@Lazy
public class VenueCreatedConsumer extends BaseConsumer<VenueCreatedEvent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(VenueCreatedConsumer.class);

    private ProcessEvent processEvent;

    @Autowired(required = false) // Optional dependency
    public void setProcessEvent(@Qualifier("VenueCreatedProcessor") ProcessEvent processEvent) {
        this.processEvent = processEvent;
    }


    @Override
    @RabbitListener(queues = MyQueue.Constants.VENUE_QUEUE_VALUE)
    public void consume(VenueCreatedEvent event) {
        LOGGER.info(getClass().getName() + " Message Consumed " + event.toString());

        if (processEvent == null) {
            LOGGER.info("processEvent in " + getClass().getName() + " is null");
        } else {
            processEvent.process(event);
        }
    }


}
