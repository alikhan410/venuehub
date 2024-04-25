package com.venuehub.broker.event.user;

@UserCreated
public record UserCreatedEvent(Long userId, String username) {
}
