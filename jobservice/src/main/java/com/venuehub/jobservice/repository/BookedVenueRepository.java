package com.venuehub.jobservice.repository;

import com.venuehub.jobservice.entity.BookedVenue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookedVenueRepository extends JpaRepository<BookedVenue, Long> {

}
