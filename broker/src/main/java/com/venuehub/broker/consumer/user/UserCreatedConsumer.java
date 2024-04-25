package com.venuehub.broker.consumer.user;

import com.venuehub.broker.constants.MyQueue;
import com.venuehub.broker.consumer.BaseConsumer;
import com.venuehub.broker.event.ProcessEvent;
import com.venuehub.broker.event.user.UserCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class UserCreatedConsumer extends BaseConsumer<UserCreatedEvent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserCreatedConsumer.class);
    private ProcessEvent processEvent;

    @Autowired(required = false) // Optional dependency
    public void setProcessEvent(@Qualifier("UserCreatedProcessor") ProcessEvent processEvent) {
        this.processEvent = processEvent;
    }

    @Override
    @RabbitListener(queues = MyQueue.Constants.USER_QUEUE_VALUE)
    public void consume(UserCreatedEvent event) {
        LOGGER.info(getClass().getName() + " Message Consumed " + event.toString());

        if (processEvent == null) {
            LOGGER.info("processEvent in " + getClass().getName() + " is null");
        } else {
            processEvent.process(event);
            LOGGER.info("Event is processed in " + getClass().getName());
        }
    }
}
