//package com.venuehub.broker.config;
//
//import com.venuehub.broker.constants.MyExchange;
//import com.venuehub.broker.constants.MyQueue;
//import org.springframework.amqp.core.Binding;
//import org.springframework.amqp.core.BindingBuilder;
//import org.springframework.amqp.core.Queue;
//import org.springframework.amqp.core.TopicExchange;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class PaymentServiceRabbitMQConfig {
//    @Bean
//    public Queue bookingUpdatedQueue() {
//        return new Queue(MyQueue.Constants.BOOKING_UPDATED_QUEUE_PAYMENT_SERVICE, true);
//    }
//    @Bean
//    public Queue bookingCreatedQueue() {
//        return new Queue(MyQueue.Constants.BOOKING_CREATED_QUEUE_PAYMENT_SERVICE, true);
//    }
//
//    @Bean
//    public TopicExchange paymentExchange() {
//        return new TopicExchange(MyExchange.PAYMENT_EXCHANGE.name());
//    }
//    @Bean
//    public Binding bookingCreatedToPaymentExchange() {
//        return BindingBuilder
//                .bind(bookingCreatedQueue())
//                .to(paymentExchange())
//                .with("booking-created");
//    }
//    @Bean
//    public Binding bookingUpdatedToPaymentExchange() {
//        return BindingBuilder
//                .bind(bookingUpdatedQueue())
//                .to(paymentExchange())
//                .with("booking-updated");
//    }
//}
