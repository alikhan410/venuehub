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

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "venue")
    private List<BookedVenue> bookings;

    public Venue(Long id, String username) {
        this.id = id;
        this.username = username;
        this.bookings = new ArrayList<>();
    }

}
//cascade = {CascadeType.PERSIST, CascadeType.REMOVE, CascadeType.MERGE},

//@Entity
//@Table(name = "venue")
//@Getter
//@Setter
//@AllArgsConstructor
//public class Venue {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "id")
//    private long id;
//
//    @Column(name = "name")
//    @NotNullNotBlank(message = "Name can not be blank or empty")
//    private String name;
//
//    @Column(name = "venue_type")
//    @NotNullNotBlank(message = "Venue type can not be blank or empty")
//    private String venueType;
//
//    @Column(name = "location")
//    @NotNullNotBlank(message = "Location can not be blank or empty")
//    private String location;
//
//    @Column(name = "capacity")
//    @Min(value = 0, message = "Invalid value")
//    private int capacity;
//
//    @Column(name = "phone")
//    @NotNullNotBlank(message = "Phone can not be blank or empty")
//    private String phone;
//
//    @Column(name = "estimate")
//    @Min(value = 0, message = "Invalid value")
//    private String estimate;
//
//    @OneToMany(fetch = FetchType.EAGER, mappedBy = "venue")
//    private List<BookedVenue> bookings;
//
//    public Venue() {
//        this.bookings = new ArrayList<>();
//    }
//
//}