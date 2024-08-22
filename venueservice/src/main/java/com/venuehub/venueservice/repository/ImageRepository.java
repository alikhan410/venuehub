package com.venuehub.venueservice.repository;

import com.venuehub.venueservice.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {

    Optional<Image> findByUrl(String url);

    @Query("SELECT i FROM Image i WHERE i.url LIKE %:partialUrl%")
    Optional<Image> findByPartailUrl(@Param("partialUrl") String partialUrl);
}
