package com.venuehub.bookingservice.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
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

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "venue", cascade = CascadeType.ALL)
    private List<BookedVenue> bookings;

    public Venue(Long id, String name, String username) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.bookings = new ArrayList<>();
    }

    @Version
    private Long version;

    private void setVersion(Long version) {
        this.version = version;
    }

}