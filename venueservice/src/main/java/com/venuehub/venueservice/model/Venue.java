package com.venuehub.venueservice.model;

import com.venuehub.venueservice.validator.NotNullNotBlank;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

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
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(name = "name")
    @NotNullNotBlank(message = "Name can not be blank or empty")
    private String name;

    @Column(name = "description")
    @NotNullNotBlank(message = "description can not be blank or empty")
    private String description;

    @Column(name = "venue_type")
    @NotNullNotBlank(message = "Venue type can not be blank or empty")
    private String venueType;

    @Column(name = "location")
    @NotNullNotBlank(message = "Location can not be blank or empty")
    private String location;

    @Column(name = "capacity")
    @Min(value = 20, message = "Capacity should be more than 20")
    private int capacity;

    @Column(name = "phone")
    @NotNull(message = "Phone can not be null")
    private String phone;

    @Column(name = "estimate")
    @Min(value = 10000, message = "Minimum value for estimates is 10000")
    private int estimate;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "venue", cascade = CascadeType.ALL)
    private List<Booking> bookings;
//
//    @OneToMany(fetch = FetchType.EAGER, mappedBy = "venue", cascade = CascadeType.ALL)
//    private List<ImageData> images;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "venue_id") // This creates the foreign key column in the ImageUri table
    private List<ImageUri> imageUris = new ArrayList<>();

    @Version
    private Long version;

    private void setVersion(Long version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "Venue{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", venueType='" + venueType + '\'' +
                ", location='" + location + '\'' +
                ", capacity=" + capacity +
                ", phone='" + phone + '\'' +
                ", estimate=" + estimate +
                '}';
    }
}
