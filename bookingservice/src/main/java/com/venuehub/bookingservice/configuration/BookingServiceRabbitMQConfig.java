package com.venuehub.bookingservice.configuration;

import com.venuehub.broker.constants.MyExchange;
import com.venuehub.broker.constants.MyKeys;
import com.venuehub.broker.constants.MyQueue;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile({"dev", "prod"})
public class BookingServiceRabbitMQConfig {
    @Bean
    public Queue bookingUpdatedQueue() {
        return QueueBuilder.durable(MyQueue.Constants.BOOKING_UPDATED_QUEUE_BOOKING_SERVICE)
                .deadLetterExchange(MyExchange.DLX.name())
                .deadLetterRoutingKey(MyKeys.dlrq.name())
                .build();
    }

    @Bean
    public Queue venueCreatedQueue() {
        return QueueBuilder.durable(MyQueue.Constants.VENUE_CREATED_QUEUE_BOOKING_SERVICE)
                .deadLetterExchange(MyExchange.DLX.name())
                .deadLetterRoutingKey(MyKeys.dlrq.name())
                .build();
    }

    @Bean
    public Queue venueUpdatedQueue() {
        return QueueBuilder.durable(MyQueue.Constants.VENUE_UPDATED_QUEUE_BOOKING_SERVICE)
                .deadLetterExchange(MyExchange.DLX.name())
                .deadLetterRoutingKey(MyKeys.dlrq.name())
                .build();
    }

    @Bean
    public Queue venueDeletedQueue() {
        return QueueBuilder.durable(MyQueue.Constants.VENUE_DELETED_QUEUE_BOOKING_SERVICE)
                .deadLetterExchange(MyExchange.DLX.name())
                .deadLetterRoutingKey(MyKeys.dlrq.name())
                .build();
    }


    @Bean
    public TopicExchange bookingExchange() {
        return new TopicExchange(MyExchange.BOOKING_EXCHANGE.name());
    }

    @Bean
    public Binding venueCreatedToBookingExchange() {
        return BindingBuilder
                .bind(venueCreatedQueue())
                .to(bookingExchange())
                .with("venue-created");
    }
    @Bean
    public Binding venueUpdatedToBookingExchange() {
        return BindingBuilder
                .bind(venueUpdatedQueue())
                .to(bookingExchange())
                .with("venue-updated");
    }
    @Bean
    public Binding venueDeletedToBookingExchange() {
        return BindingBuilder
                .bind(venueDeletedQueue())
                .to(bookingExchange())
                .with("venue-deleted");
    }
    @Bean
    public Binding bookingUpdatedToBookingExchange() {
        return BindingBuilder
                .bind(bookingUpdatedQueue())
                .to(bookingExchange())
                .with("booking-updated");
    }
}
