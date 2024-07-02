package com.venuehub.paymentservice.configuration;

import com.venuehub.broker.constants.MyExchange;
import com.venuehub.broker.constants.MyKeys;
import com.venuehub.broker.constants.MyQueue;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PaymentServiceRabbitMQConfig {
    @Bean
    public Queue bookingUpdatedQueue() {
        return QueueBuilder.durable(MyQueue.Constants.BOOKING_UPDATED_QUEUE_PAYMENT_SERVICE)
                .deadLetterExchange(MyExchange.DLX.name())
                .deadLetterRoutingKey(MyKeys.dlrq.name())
                .build();
    }

    @Bean
    public Queue bookingCreatedQueue() {
        return QueueBuilder.durable(MyQueue.Constants.BOOKING_CREATED_QUEUE_PAYMENT_SERVICE)
                .deadLetterExchange(MyExchange.DLX.name())
                .deadLetterRoutingKey(MyKeys.dlrq.name())
                .build();
    }

    @Bean
    public TopicExchange paymentExchange() {
        return new TopicExchange(MyExchange.PAYMENT_EXCHANGE.name());
    }

    @Bean
    public Binding bookingCreatedToPaymentExchange() {
        return BindingBuilder
                .bind(bookingCreatedQueue())
                .to(paymentExchange())
                .with("booking-created");
    }

    @Bean
    public Binding bookingUpdatedToPaymentExchange() {
        return BindingBuilder

                .bind(bookingUpdatedQueue())
                .to(paymentExchange())
                .with("booking-updated");
    }
}
