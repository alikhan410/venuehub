package com.venuehub.venueservice.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "images")
public class ImageData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] image;

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonBackReference
    private Venue venue;

    public ImageData(byte[] image, Venue venue) {
        this.image = image;
        this.venue = venue;
    }

    public ImageData(byte[] image) {
        this.image = image;
    }

    public ImageData() {
    }
}
