package com.venuehub.broker.event;


public interface ProcessEvent {
    void process(Object event);
    void checkAnnotation(Object event) throws IllegalArgumentException;
}
