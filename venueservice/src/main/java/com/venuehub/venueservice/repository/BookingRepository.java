package com.venuehub.venueservice.repository;

import com.venuehub.venueservice.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query(value = "SELECT * FROM booking WHERE venue_id = :id", nativeQuery = true)
    List<Booking> findByVenue(@Param("id") Long id);
}