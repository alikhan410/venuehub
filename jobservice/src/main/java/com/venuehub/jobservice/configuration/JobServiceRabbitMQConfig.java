package com.venuehub.jobservice.configuration;

import com.venuehub.broker.constants.MyExchange;
import com.venuehub.broker.constants.MyKeys;
import com.venuehub.broker.constants.MyQueue;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JobServiceRabbitMQConfig {
    @Bean
    public Queue jobSchedulingQueue() {
        return QueueBuilder.durable(MyQueue.Constants.JOB_SCHEDULING_QUEUE_JOB_SERVICE)
                .deadLetterExchange(MyExchange.DLX.name())
                .deadLetterRoutingKey(MyKeys.dlrq.name())
                .build();
    }

    @Bean
    public Queue jobCancellingQueue() {
        return QueueBuilder.durable(MyQueue.Constants.JOB_CANCELLING_QUEUE_JOB_SERVICE)
                .deadLetterExchange(MyExchange.DLX.name())
                .deadLetterRoutingKey(MyKeys.dlrq.name())
                .build();
    }

    @Bean
    public Queue bookingUpdatedQueue() {
        return QueueBuilder.durable(MyQueue.Constants.BOOKING_UPDATED_QUEUE_JOB_SERVICE)
                .deadLetterExchange(MyExchange.DLX.name())
                .deadLetterRoutingKey(MyKeys.dlrq.name())
                .build();
    }

    @Bean
    public TopicExchange jobExchange() {
        return new TopicExchange(MyExchange.JOB_EXCHANGE.name());
    }

    @Bean
    public Binding jobSchedulingToJobExchange() {
        return BindingBuilder
                .bind(jobSchedulingQueue())
                .to(jobExchange())
                .with("booking-job-scheduling");
    }

    @Bean
    public Binding jobCancellingToJobExchange() {
        return BindingBuilder
                .bind(jobCancellingQueue())
                .to(jobExchange())
                .with("booking-job-cancelling");
    }

    @Bean
    public Binding bookingUpdatedToJobExchange() {
        return BindingBuilder
                .bind(bookingUpdatedQueue())
                .to(jobExchange())
                .with("booking-updated");
    }

}
