package com.venuehub.venueservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class ImageUri {

    @Id
    Long id;
    String uri;

    public ImageUri(Long id, String uri) {
        this.id = id;
        this.uri = uri;
    }
}
