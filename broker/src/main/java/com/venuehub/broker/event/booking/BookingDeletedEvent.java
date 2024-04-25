package com.venuehub.broker.event.booking;
@BookingDeleted
public record BookingDeletedEvent(Long id, String username) {
}
//public class BookingDeletedEvent {
//    private Long id;
//
//    public BookingDeletedEvent(Long id) {
//        this.id = id;
//    }
//
//    public BookingDeletedEvent() {
//    }
//
//    public Long getId() {
//        return id;
//    }
//
//    @Override
//    public String toString() {
//        return "BookingDeletedEvent{" +
//                "id=" + id +
//                '}';
//    }
//}
