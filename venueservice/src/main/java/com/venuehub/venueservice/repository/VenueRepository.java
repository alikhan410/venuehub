package com.venuehub.venueservice.repository;

import com.venuehub.venueservice.model.Venue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VenueRepository extends JpaRepository<Venue, Long> {
    @Query(value = "SELECT * FROM venue WHERE username = :username", nativeQuery = true)
    List<Venue> findVenueByUsername(@Param("username") String username);

    @Query(value = "SELECT * FROM venue WHERE username = :username AND name= :venueName", nativeQuery = true)
    Optional<Venue> findByVenueNameAndUsername(@Param("username") String username, @Param("venueName") String venueName);

    @Query(value = "SELECT * FROM venue WHERE status = 'ACTIVE'", nativeQuery = true)
    List<Venue> findAllActiveVenues();
}
