package com.venuehub.venueservice.repository;

import com.venuehub.venueservice.model.ImageUri;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageUriRepository extends JpaRepository<ImageUri, Long> {
}
