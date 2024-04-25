package com.venuehub.broker.event.booking;

import com.venuehub.broker.constants.BookingStatus;

@BookingUpdated
public record BookingUpdatedEvent(Long bookingId, BookingStatus status){}
//public class BookingUpdatedEvent {
//    private Long bookingId;
//
//    private BookingStatus status;
//    public BookingUpdatedEvent() {
//    }
//    public BookingUpdatedEvent(Long bookingId, BookingStatus status) {
//        this.bookingId = bookingId;
//
//        this.status = status;
//
//    }
//    public Long getBookingId() {
//        return bookingId;
//    }
//    public BookingStatus getStatus() {
//        return status;
//    }
//    @Override
//    public String toString() {
//        return "BookingUpdatedEvent{" +
//                "bookingId=" + bookingId +
//                ", status=" + status +
//                '}';
//    }
//}
