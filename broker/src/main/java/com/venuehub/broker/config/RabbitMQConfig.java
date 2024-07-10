package com.venuehub.broker.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.venuehub.broker.constants.MyExchange;
import com.venuehub.broker.constants.MyKeys;
import com.venuehub.broker.constants.MyQueue;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.AbstractRetryOperationsInterceptorFactoryBean;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.config.StatelessRetryOperationsInterceptorFactoryBean;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
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
        return QueueBuilder.durable(MyQueue.Constants.DLQ).build();
    }

    @Bean
    public Exchange deadLetterExchange() {
        return ExchangeBuilder.directExchange(MyExchange.DLX.name()).durable(true).build();
    }
    @Bean
    public Binding dlqBinding() {
        return BindingBuilder.bind(deadLetterQueue())
                .to(deadLetterExchange())
                .with(MyKeys.dlrq.name())
                .noargs();
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

//    public RetryTemplate retryTemplate() {
//        RetryTemplate retryTemplate = new RetryTemplate();
//
//        // Configuring backoff policy
//        FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
//        backOffPolicy.setBackOffPeriod(5000); // 5 seconds delay between resending
//        retryTemplate.setBackOffPolicy(backOffPolicy);
//
//        // Configuring retry policy
//        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
//        retryPolicy.setMaxAttempts(3); // Maximum retry attempts
//        retryTemplate.setRetryPolicy(retryPolicy);
//
//
//        return retryTemplate;
//    }

//    @Bean
//    public RabbitListenerContainerFactory<SimpleMessageListenerContainer> listenerContainerFactory(ConnectionFactory connectionFactory) {
//        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
//
//        // Configuring connection factory
//        factory.setConnectionFactory(connectionFactory);
//
//        // Setting concurrency settings
//        factory.setConcurrentConsumers(1); // Number of concurrent consumers (threads) to start with
//        factory.setMaxConcurrentConsumers(5); // Maximum number of concurrent consumers
//        factory.setPrefetchCount(1); // Number of messages to prefetch in each request
//
//        // Acknowledgment mode
//        factory.setAcknowledgeMode(AcknowledgeMode.AUTO); // Automatically acknowledge messages
//
//        // Setting retry template
//        factory.setRetryTemplate(retryTemplate()); // Setting the retry template configured above
//
//        return factory;
//    }
//    @Bean
//    public RabbitListenerContainerFactory<SimpleMessageListenerContainer> rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
//        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
//        factory.setConnectionFactory(connectionFactory); // Set your connection factory here
//
//        factory.setRetryTemplate(retryTemplate());
//        return factory;
//    }

//    @Bean
//    public RetryOperationsInterceptor retryOperationsInterceptor(ConnectionFactory connectionFactory) {
//        RepublishMessageRecoverer messageRecover = new RepublishMessageRecoverer(
//                amqpTemplate(connectionFactory),
//                "DEAD_LETTER_EXCHANGE",
//                "dead-letter"
//        );
//
//        return RetryInterceptorBuilder.stateless()
//                .retryOperations(retryTemplate())
//                .recoverer(messageRecover)
//                .build();
//    }
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
