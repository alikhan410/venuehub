package com.venuehub.imageservice.repository;

import com.venuehub.imageservice.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {

    Optional<Image> findByUrl(String uri);

    @Query("SELECT i FROM Image i WHERE i.url LIKE %:partialUrl%")
    Optional<Image> findByPartailUrl(@Param("partialUrl") String partialUrl);
}
