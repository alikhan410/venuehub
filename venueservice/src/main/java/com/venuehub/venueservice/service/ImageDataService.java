package com.venuehub.venueservice.service;

import com.venuehub.venueservice.model.ImageData;
import com.venuehub.venueservice.model.Venue;
import com.venuehub.venueservice.repository.ImageDateRepository;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.file.FilesUncheck;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
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
    @CacheEvict("venue:all")
    public void saveAll(List<ImageData> images) {
        imageDateRepository.saveAll(images);
    }

    @Async
    @Transactional
    public void storeImagesAsync(Venue venue, List<Path> paths) throws IOException {
        List<ImageData> imageDataList = new ArrayList<>();
        for (Path path : paths) {
            ImageData imageData = new ImageData(Files.readAllBytes(path), venue);
            imageDataList.add(imageData);

            //Deleting the temp files that were created in the parent method
            FilesUncheck.delete(path);
        }
        imageDateRepository.saveAll(imageDataList);
    }

    @Transactional
    public void storeImagesSync(Venue venue, MultipartFile[] files) throws IOException {
        List<ImageData> imageDataList = new ArrayList<>();
        for (MultipartFile file : files) {
            ImageData imageData = new ImageData(file.getBytes(), venue);
            imageDataList.add(imageData);
        }
        imageDateRepository.saveAll(imageDataList);
    }
}
