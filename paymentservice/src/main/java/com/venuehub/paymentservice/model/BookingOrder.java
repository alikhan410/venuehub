package com.venuehub.paymentservice.model;

import com.venuehub.broker.constants.BookingStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.MySQLEnumJdbcType;

@Entity
@Getter
@Setter
@Table(name = "booking_order")
@NoArgsConstructor
public class BookingOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    private String username;

    private String clientSecret;

    private int amount;

    private Long bookingId;

    @JdbcType(MySQLEnumJdbcType.class)
    private OrderStatus orderStatus;


    public BookingOrder(String username, int amount, Long bookingId, String clientSecret) {
        this.orderStatus = OrderStatus.PENDING;
        this.username = username;
        this.amount = amount;
        this.bookingId = bookingId;
        this.clientSecret = clientSecret;
    }
}
