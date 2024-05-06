package com.venuehub.venueservice.repository;

import com.venuehub.venueservice.model.ImageData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageDateRepository extends JpaRepository<ImageData, Long> {
}
