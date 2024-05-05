package com.venuehub.venueservice.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.venuehub.venueservice.validator.NotNullNotBlank;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "venue")
@AllArgsConstructor
@Data
@Builder
public class Venue {

    public Venue() {

    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "username")
    private String username;

    @Column(name = "name")
    @NotNullNotBlank(message = "Name can not be blank or empty")
    private String name;

    @Column(name = "venue_type")
    @NotNullNotBlank(message = "Venue type can not be blank or empty")
    private String venueType;

    @Column(name = "location")
    @NotNullNotBlank(message = "Location can not be blank or empty")
    private String location;

    @Column(name = "image")
    @Lob
    private byte[] image;

    @Column(name = "capacity")
    @Min(value = 0, message = "Invalid value")
    private int capacity;

    @Column(name = "phone")
    @NotNullNotBlank(message = "Phone can not be blank or empty")
    private String phone;

    @Column(name = "estimate")
    @Min(value = 0, message = "Invalid value")
    private String estimate;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "venue")
    private List<BookedVenue> bookings;


}
