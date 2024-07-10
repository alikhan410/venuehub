package com.venuehub.venueservice.service;

import com.venuehub.venueservice.model.ImageUri;
import com.venuehub.venueservice.repository.ImageUriRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ImageUriService {
    private final ImageUriRepository imageUriRepository;

    public ImageUriService(ImageUriRepository imageUriRepository) {
        this.imageUriRepository = imageUriRepository;
    }

    @Transactional
    public void save(ImageUri imageUri) {
        imageUriRepository.save(imageUri);
    }
}
