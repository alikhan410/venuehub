package com.venuehub.broker.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.venuehub.broker.constants.MyExchange;
import com.venuehub.broker.constants.MyQueue;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.AbstractRetryOperationsInterceptorFactoryBean;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.config.StatelessRetryOperationsInterceptorFactoryBean;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.RepublishMessageRecoverer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.interceptor.MethodInvocationRecoverer;
import org.springframework.retry.interceptor.RetryOperationsInterceptor;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

@Configuration
public class RabbitMQConfig {
    //    @Value("${spring.rabbitmq.host}")
//    private static String HOST;
//    @Value("${spring.rabbitmq.port}")
//    private static int PORT;
//
//    @Bean
//    public ConnectionFactory connectionFactory() {
//        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
//        connectionFactory.setAddresses(HOST);
//        connectionFactory.setUsername("guest");
//        connectionFactory.setPassword("guest");
//        connectionFactory.setPort(PORT);
//        return connectionFactory;
//    }
//    @Bean
//    public Queue retryQueue() {
//        return new Queue("RETRY_QUEUE", true);
//    }

    @Bean
    public Queue deadLetterQueue() {
        return new Queue("DEAD_LETTER_QUEUE", true);
    }

    @Bean
    public TopicExchange deadLetterExchange() {
        return new TopicExchange("DEAD_LETTER_EXCHANGE");
    }

    @Bean
    Binding deadQueueBinding() {
        return BindingBuilder
                .bind(deadLetterQueue())
                .to(deadLetterExchange())
                .with("dead-letter");
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

    @Bean
    public RetryTemplate retryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();

        FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
        backOffPolicy.setBackOffPeriod(5000); //5 seconds delay between resending
        retryTemplate.setBackOffPolicy(backOffPolicy);

        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(3);
        retryTemplate.setRetryPolicy(retryPolicy);

        return retryTemplate;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory listenerContainerFactory(ConnectionFactory connectionFactory) {

        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setConcurrentConsumers(1);
        factory.setMaxConcurrentConsumers(5);
        factory.setPrefetchCount(1);
        factory.setAcknowledgeMode(AcknowledgeMode.AUTO);
        factory.setRetryTemplate(retryTemplate());

        return factory;
    }

    @Bean
    public RetryOperationsInterceptor retryOperationsInterceptor(ConnectionFactory connectionFactory) {
        RepublishMessageRecoverer messageRecover = new RepublishMessageRecoverer(
                amqpTemplate(connectionFactory),
                "DEAD_LETTER_EXCHANGE",
                "dead-letter"
        );

        return RetryInterceptorBuilder.stateless()
                .retryOperations(retryTemplate())
                .recoverer(messageRecover)
                .build();
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
