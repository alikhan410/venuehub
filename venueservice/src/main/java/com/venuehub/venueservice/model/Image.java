package com.venuehub.venueservice.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "image")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String filename;
    private String venueName;
    private String url;
    private String vendorName;

}
