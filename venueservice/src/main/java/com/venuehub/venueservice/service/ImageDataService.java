package com.venuehub.venueservice.service;

import com.venuehub.venueservice.model.ImageData;
import com.venuehub.venueservice.repository.ImageDateRepository;
import org.springframework.stereotype.Service;

@Service
public class ImageDataService {
    private final ImageDateRepository imageDateRepository;

    public ImageDataService(ImageDateRepository imageDateRepository) {
        this.imageDateRepository = imageDateRepository;
    }

    public void save(ImageData imageData) {
        imageDateRepository.save(imageData);
    }
}
