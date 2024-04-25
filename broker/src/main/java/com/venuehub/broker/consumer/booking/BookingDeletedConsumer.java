package com.venuehub.broker.consumer.booking;

import com.venuehub.broker.constants.MyQueue;
import com.venuehub.broker.consumer.BaseConsumer;
import com.venuehub.broker.event.ProcessEvent;
import com.venuehub.broker.event.booking.BookingDeletedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
@Lazy
public class BookingDeletedConsumer extends BaseConsumer<BookingDeletedEvent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(BookingDeletedConsumer.class);
    private ProcessEvent processEvent;

    @Autowired(required = false) // Optional dependency
    public void setProcessEvent(@Qualifier("BookingDeletedProcessor") ProcessEvent processEvent) {
        this.processEvent = processEvent;
    }
    @Override
    @RabbitListener(queues = MyQueue.Constants.BOOKING_QUEUE_VALUE)
    public void consume(BookingDeletedEvent event) {
        LOGGER.info(getClass().getName() + " Message Consumed " + event.toString());

        if (processEvent == null) {
            LOGGER.info("processEvent in " + getClass().getName() + " is null");
        } else {
            processEvent.process(event);
            LOGGER.info("Event is processed in " + getClass().getName());
        }
    }
}
