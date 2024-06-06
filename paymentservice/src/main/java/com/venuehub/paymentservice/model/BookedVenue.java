package com.venuehub.paymentservice.model;

import com.venuehub.broker.constants.BookingStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.MySQLEnumJdbcType;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "booked_venue")
public class BookedVenue {
    @Id
    private Long id;

    private String username;

    private int bookingFee;

    @JdbcType(MySQLEnumJdbcType.class)
    private BookingStatus status;
}
