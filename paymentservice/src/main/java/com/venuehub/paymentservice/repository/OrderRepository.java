package com.venuehub.paymentservice.repository;

import com.venuehub.paymentservice.model.BookingOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<BookingOrder, Long> {

    @Query(value = "SELECT * FROM booking_order WHERE booking_id = :id and username = :username", nativeQuery = true)
    BookingOrder findByBooking(@Param("id") Long bookingId, @Param("username") String username);

    @Query(value = "SELECT * FROM booking_order WHERE client_secret = :clientSecret" , nativeQuery = true)
    BookingOrder findByClientSecret(@Param("clientSecret") String clientSecret);
}
