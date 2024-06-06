package com.venuehub.bookingservice.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.venuehub.bookingservice.validator.NotNullNotBlank;
import com.venuehub.broker.constants.BookingStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import lombok.*;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.MySQLEnumJdbcType;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Entity
@Table(name = "booked_venue")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class BookedVenue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "status")
    @JdbcType(MySQLEnumJdbcType.class)
    private BookingStatus status;

    @Column(name = "booking_date_time")
    @NotNullNotBlank(message = "Date can not be null or empty")
    private String bookingDateTime;

    @Column(name = "booking_fee")
    @Min(value = 10000, message = "Minimum value for estimates is 10000")
    private int bookingFee;


    @Builder.Default
    @Column(name = "reservation_expiry")
    private String reservationExpiry = LocalDateTime.now(ZoneId.of("Asia/Karachi")).plusMinutes(50).toString();

    @Column(name = "customer_name")
    @NotNullNotBlank(message = "Customer name can not be null or empty")
    private String username;

    @Column(name = "customer_email")
    @Email(message = "Email can not be null or empty")
    private String email;

    @Column(name = "customer_phone")
    @NotNullNotBlank(message = "Phone can not be null or blank")
    private String phone;

    @Column(name = "guests")
    @Min(value = 0, message = "Invalid value")
    private int guests;

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonBackReference
    private Venue venue;

    @Version
    private Long version;

    private void setVersion(Long version) {
        this.version = version;
    }


}
//cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.DETACH}