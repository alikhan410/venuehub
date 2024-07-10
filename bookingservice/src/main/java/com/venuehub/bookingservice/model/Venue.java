package com.venuehub.bookingservice.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "venue")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Venue {
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(name = "name")
    private String name;

    @Column(name = "estimate")
    @Min(value = 10000, message = "Minimum value for estimates is 10000")
    private int estimate;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "venue", cascade = CascadeType.ALL)
    @JsonBackReference
    private List<Booking> bookings;

    public Venue(Long id, String name, int estimate, String username) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.estimate = estimate;
        this.bookings = new ArrayList<>();
    }

    @Version
    private Long version;

    private void setVersion(Long version) {
        this.version = version;
    }

}