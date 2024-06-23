package com.venuehub.bookingservice.configuration;

import com.venuehub.broker.constants.MyExchange;
import com.venuehub.broker.constants.MyQueue;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("dev")
public class BookingServiceRabbitMQConfig {
    @Bean
    public Queue bookingUpdatedQueue() {
        return new Queue(MyQueue.Constants.BOOKING_UPDATED_QUEUE_BOOKING_SERVICE, true);
    }
    @Bean
    public Queue venueCreatedQueue() {
        return new Queue(MyQueue.Constants.VENUE_CREATED_QUEUE_BOOKING_SERVICE, true);
    }
    @Bean
    public Queue venueUpdatedQueue() {
        return new Queue(MyQueue.Constants.VENUE_UPDATED_QUEUE_BOOKING_SERVICE, true);
    }
    @Bean
    public Queue venueDeletedQueue() {
        return new Queue(MyQueue.Constants.VENUE_DELETED_QUEUE_BOOKING_SERVICE, true);
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
