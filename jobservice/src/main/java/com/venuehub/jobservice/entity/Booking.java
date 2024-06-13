package com.venuehub.jobservice.entity;

import com.venuehub.broker.constants.BookingStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.MySQLEnumJdbcType;

@Entity
@Table(name = "booking")
@Data
public class Booking {
    @Id
    private long id;

    @Column(name = "username")
    private String username;

    @Column(name = "status")
    @JdbcType(MySQLEnumJdbcType.class)
    private BookingStatus status;

}
