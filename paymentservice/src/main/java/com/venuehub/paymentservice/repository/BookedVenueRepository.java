package com.venuehub.paymentservice.repository;

import com.venuehub.paymentservice.model.BookedVenue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookedVenueRepository extends JpaRepository<BookedVenue, Long> {
}
