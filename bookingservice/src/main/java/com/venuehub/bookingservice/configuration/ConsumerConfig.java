package com.venuehub.bookingservice.configuration;

import com.venuehub.broker.consumer.booking.BookingUpdatedConsumer;
import com.venuehub.broker.consumer.venue.VenueCreatedConsumer;
import com.venuehub.broker.consumer.venue.VenueDeletedConsumer;
import com.venuehub.broker.consumer.venue.VenueUpdatedConsumer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConsumerConfig {
    @Bean
    public VenueCreatedConsumer venueCreatedConsumer() {
        return new VenueCreatedConsumer();
    }

    @Bean
    public VenueDeletedConsumer venueDeletedConsumer() {
        return new VenueDeletedConsumer();
    }

    @Bean
    public VenueUpdatedConsumer venueUpdatedConsumer() {
        return new VenueUpdatedConsumer();
    }

    @Bean
    public BookingUpdatedConsumer bookingUpdatedConsumer() {
        return new BookingUpdatedConsumer();
    }
}
