package com.venuehub.broker.consumer;


public abstract class BaseConsumer<T> {

    public abstract void consume(T event);

}