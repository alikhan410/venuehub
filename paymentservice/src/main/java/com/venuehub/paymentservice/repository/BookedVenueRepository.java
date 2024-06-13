package com.venuehub.paymentservice.repository;

import com.venuehub.paymentservice.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookedVenueRepository extends JpaRepository<Booking, Long> {
}
