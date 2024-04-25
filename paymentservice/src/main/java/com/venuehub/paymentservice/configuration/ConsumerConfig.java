package com.venuehub.paymentservice.configuration;

import com.venuehub.broker.consumer.booking.BookingCreatedConsumer;
import com.venuehub.broker.consumer.booking.BookingUpdatedConsumer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConsumerConfig {
    @Bean
    public BookingCreatedConsumer bookingCreatedConsumer() {
        return new BookingCreatedConsumer();
    }
        @Bean
    public BookingUpdatedConsumer bookingUpdatedConsumer() {
        return new BookingUpdatedConsumer();
    }

}
