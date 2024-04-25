package com.venuehub.broker.producer;

import org.springframework.amqp.rabbit.core.RabbitTemplate;

public abstract class BaseProducer<T> {
    public abstract void produce(T event);
}
