package com.venuehub.venueservice.repository;

import com.venuehub.venueservice.model.BookedVenue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookedVenueRepository extends JpaRepository<BookedVenue, Long> {
    @Query(value = "SELECT * FROM booked_venue WHERE venue_id = :id", nativeQuery = true)
    List<BookedVenue> findByVenue(@Param("id") Long id);
}