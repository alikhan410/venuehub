package com.venuehub.venueservice.service;

import com.venuehub.venueservice.model.ImageUri;
import com.venuehub.venueservice.repository.ImageUriRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    @Transactional
    public void saveAll(List<ImageUri> imageUriList) {
        imageUriRepository.saveAll(imageUriList);
    }
}
