package com.venuehub.paymentservice.repository;

import com.venuehub.paymentservice.model.BookedVenue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookedVenueRepository extends JpaRepository<BookedVenue, Long> {
}
