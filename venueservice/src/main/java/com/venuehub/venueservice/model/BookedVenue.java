package com.venuehub.venueservice.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.venuehub.broker.constants.BookingStatus;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.MySQLEnumJdbcType;

@Entity
@Table(name = "booked_venue")
@Data
public class BookedVenue {
    @Id
    @Column(name = "id")
    private long id;

    @Column(name = "username")
    private String username;

    @Column(name = "status")
    @JdbcType(MySQLEnumJdbcType.class)
    private BookingStatus status;

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonBackReference
    private Venue venue;

    @Version
    private Long version;

    private void setVersion(Long version) {
        this.version = version;
    }

}
//@Entity
//@Table(name = "booked_venue")
//@Data
//public class BookedVenue {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "id")
//    private long id;
//
//    @Column(name = "booking_date_time")
//    @NotNullNotBlank(message = "Date can not be null or empty")
//    private String bookingDateTime;
//
//    @Column(name = "customer_name")
//    @NotNullNotBlank(message = "Customer name can not be null or empty")
//    private String customerName;
//
//    @Column(name = "customer_email")
//    @Email(message = "Email can not be null or empty")
//    private String customerEmail;
//
//    @Column(name = "customer_phone")
//    @NotNullNotBlank(message = "Phone can not be null or blank")
//    private String customerPhone;
//
//    @Column(name = "guests")
//    @Min(value = 0, message = "Invalid value")
//    private int guests;
//
//    @ManyToOne( fetch = FetchType.EAGER)
//    @JsonBackReference
//    private Venue venue;
//}
