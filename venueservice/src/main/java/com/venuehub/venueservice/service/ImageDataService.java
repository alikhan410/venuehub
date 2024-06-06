package com.venuehub.venueservice.service;

import com.venuehub.venueservice.model.ImageData;
import com.venuehub.venueservice.repository.ImageDateRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ImageDataService {
    private final ImageDateRepository imageDateRepository;

    public ImageDataService(ImageDateRepository imageDateRepository) {
        this.imageDateRepository = imageDateRepository;
    }

    @Transactional
    public void save(ImageData imageData) {
        imageDateRepository.save(imageData);
    }

    @Transactional
    public void saveAll(List<ImageData> images) {
        imageDateRepository.saveAll(images);
    }
}
