package com.venuehub.bookingservice.repository;

import com.venuehub.bookingservice.model.BookedVenue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookedVenueRepository extends JpaRepository<BookedVenue, Long> {
    @Query(value = "SELECT * FROM booked_venue WHERE venue_id = :id AND status != 'FAILED'", nativeQuery = true)
    List<BookedVenue> findByVenue(@Param("id") long id);

    @Query(value = "SELECT * FROM booked_venue WHERE venue_id = :id", nativeQuery = true)
    List<BookedVenue> AllBookingsByVenue(@Param("id") long id);

    @Query(value = "SELECT * FROM booked_venue WHERE customer_name = :username", nativeQuery = true)
    List<BookedVenue> findByUsername(@Param("username") String username);

    @Query(value = "SELECT * FROM booked_venue WHERE id = :id AND status = 'COMPLETED'", nativeQuery = true)
    Optional<BookedVenue> findCompletedBookingById(@Param("id") long id);
}