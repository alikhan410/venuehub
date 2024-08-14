package com.venuehub.imageservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String filename;
    private String venueName;
    private String uri;
    private String imagePath;
    private String vendorName;


//    public Image(String filename, String imagePath, String uri, String venueName, String vendorName) {
//        this.filename = filename;
//        this.imagePath = imagePath;
//        this.uri = uri;
//        this.venueName = venueName;
//        this.vendorName = vendorName;
//    }
}