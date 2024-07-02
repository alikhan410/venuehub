package com.venuehub.paymentservice.service;

import com.venuehub.paymentservice.model.BookingOrder;
import com.venuehub.paymentservice.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class OrderService {
    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional
    public void save(BookingOrder bookingOrder) {
        orderRepository.save(bookingOrder);
    }

    public Optional<BookingOrder> findById(Long orderId) {
        return orderRepository.findById(orderId);
    }

    public BookingOrder findByBooking(Long bookingId, String username) {
        return orderRepository.findByBooking(bookingId, username);
    }

    public BookingOrder findByClientSecret(String clientSecret) {
        return orderRepository.findByClientSecret(clientSecret);
    }

    public List<BookingOrder> findByUsername(String username) {
        return orderRepository.findByUsername(username);
    }

    public List<BookingOrder> findByVendor(String vendor) {
        return orderRepository.findByVendor(vendor);
    }
}
