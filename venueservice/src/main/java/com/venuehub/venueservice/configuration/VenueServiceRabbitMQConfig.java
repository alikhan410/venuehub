package com.venuehub.venueservice.configuration;

import com.venuehub.broker.constants.MyExchange;
import com.venuehub.broker.constants.MyKeys;
import com.venuehub.broker.constants.MyQueue;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VenueServiceRabbitMQConfig {
    @Bean
    public Queue bookingUpdatedQueue() {
        return QueueBuilder.durable(MyQueue.Constants.BOOKING_UPDATED_QUEUE_VENUE_SERVICE)
                .deadLetterExchange(MyExchange.DLX.name())
                .deadLetterRoutingKey(MyKeys.dlrq.name())
                .build();
//        return new Queue(MyQueue.Constants.BOOKING_UPDATED_QUEUE_VENUE_SERVICE, true);
    }

    @Bean
    public Queue bookingCreatedQueue() {
        return QueueBuilder.durable(MyQueue.Constants.BOOKING_CREATED_QUEUE_VENUE_SERVICE)
                .deadLetterExchange(MyExchange.DLX.name())
                .deadLetterRoutingKey(MyKeys.dlrq.name())
                .build();
    }

    @Bean
    public TopicExchange venueExchange() {
        return new TopicExchange(MyExchange.VENUE_EXCHANGE.name());
    }

    @Bean
    public Binding bookingCreatedToVenueExchange() {
        return BindingBuilder
                .bind(bookingCreatedQueue())
                .to(venueExchange())
                .with("booking-created");
    }

    @Bean
    public Binding bookingUpdatedToVenueExchange() {
        return BindingBuilder
                .bind(bookingUpdatedQueue())
                .to(venueExchange())
                .with("booking-updated");
    }
}
