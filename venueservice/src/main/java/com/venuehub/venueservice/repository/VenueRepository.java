package com.venuehub.venueservice.repository;

import com.venuehub.venueservice.model.Venue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VenueRepository extends JpaRepository<Venue, Long> {
    @Query(value = "SELECT * FROM venue WHERE username = :username", nativeQuery = true)
    List<Venue> findVenueByUsername(@Param("username")String username);
}
