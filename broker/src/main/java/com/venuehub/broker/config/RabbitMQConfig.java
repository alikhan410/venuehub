package com.venuehub.broker.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.venuehub.broker.constants.MyExchange;
import com.venuehub.broker.constants.MyQueue;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setAddresses("localhost");
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        connectionFactory.setPort(5672);
        return connectionFactory;
    }

    @Bean
    public Queue venueQueue() {
        return new Queue(MyQueue.Constants.VENUE_QUEUE_VALUE, true);
    }
    @Bean
    public Queue userQueue() {
        return new Queue(MyQueue.Constants.USER_QUEUE_VALUE, true);
    }

    @Bean
    public Queue bookingQueue() {
        return new Queue(MyQueue.Constants.BOOKING_QUEUE_VALUE, true);
    }

    @Bean
    public TopicExchange venueExchange() {
        return new TopicExchange(MyExchange.VENUE_EXCHANGE.name());
    }
    @Bean
    public TopicExchange userExchange() {
        return new TopicExchange(MyExchange.USER_EXCHANGE.name());
    }

    @Bean
    public TopicExchange bookingExchange() {
        return new TopicExchange(MyExchange.BOOKING_EXCHANGE.name());
    }

    @Bean
    public Binding venueBinding() {
        return BindingBuilder
                .bind(venueQueue())
                .to(venueExchange())
                .with("venue");
    }
    @Bean
    public Binding userBinding() {
        return BindingBuilder
                .bind(userQueue())
                .to(userExchange())
                .with("user");
    }

    @Bean
    public Binding bookingBinding() {
        return BindingBuilder
                .bind(bookingQueue())
                .to(bookingExchange())
                .with("booking");
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        ObjectMapper mapper = new ObjectMapper();
        return new Jackson2JsonMessageConverter(mapper);
    }

    @Bean
    public RabbitTemplate amqpTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
//    @Bean
//    public RabbitListenerContainerFactory<Object> rabbitTransactionListenerFactory(ConnectionFactory connectionFactory) {
//        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
//        factory.setConnectionFactory(connectionFactory);
//        factory.setTransactionManager(rabbitTransactionManager(connectionFactory)); // Inject a RabbitTransactionManager bean
//        return factory;
//    }

//    @Bean
//    public RabbitTransactionManager rabbitTransactionManager(ConnectionFactory connectionFactory) {
//        return new RabbitTransactionManager(connectionFactory); // Inject connection factory
//    }


}
