package com.venuehub.venueservice.service;

import com.venuehub.commons.exception.NoSuchVenueException;
import com.venuehub.venueservice.dto.VenueDto;
import com.venuehub.venueservice.mapper.VenueServiceMapper;
import com.venuehub.venueservice.model.Booking;
import com.venuehub.venueservice.model.Image;
import com.venuehub.venueservice.model.Venue;
import com.venuehub.venueservice.repository.VenueRepository;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class VenueService {
    public static final Logger logger = LoggerFactory.getLogger(VenueService.class);

    private final VenueRepository venueRepository;
    private final CacheManager cacheManager;
    private final VenueServiceMapper venueServiceMapper = Mappers.getMapper(VenueServiceMapper.class);

    @Autowired
    public VenueService(VenueRepository venueRepository, CacheManager cacheManager) {
        this.venueRepository = venueRepository;
        this.cacheManager = cacheManager;
    }

    @Transactional
    @CacheEvict(value = {"venue:all", "venue:id", "venue:username"}, allEntries = true)
    public void save(Venue venue) {
        venueRepository.save(venue);
    }

    @Transactional
    @CacheEvict(value = {"venue:all", "venue:id", "venue:username"}, allEntries = true)
    public void saveAll(List<Venue> venues) {
        venueRepository.saveAll(venues);
    }

    public Optional<Venue> findById(long id) {
        logger.info("Fetching the venue associated with the id: {}", id);
        return venueRepository.findById(id);
    }

    @Cacheable(value = "venue:id", key = "#id")
    public VenueDto loadVenueDtoById(long id) {
        logger.info("Loading the venue associated with the id: {}", id);
        Venue venue = venueRepository.findById(id).orElseThrow(NoSuchVenueException::new);
        return venueServiceMapper.venueToDto(venue);
    }

    @Cacheable(value = "venue:username", key = "#username")
    public List<VenueDto> findByUsername(String username) {
        List<Venue> venues = venueRepository.findVenueByUsername(username);
        return venueServiceMapper.venuesToDtoList(venues);
    }

    //    @Cacheable(value = "venue:uservenuename", key = "#username")
    public VenueDto findByVenueNameandUsername(String username, String venueName) {
        Venue venue = venueRepository.findByVenueNameAndUsername(username, venueName).orElseThrow(NoSuchVenueException::new);
        return venueServiceMapper.venueToDto(venue);
    }

    @Transactional
    @CacheEvict(value = {"venue:all", "venue:id", "venue:username"}, allEntries = true)
    public void delete(Venue venue) {
        venueRepository.delete(venue);
    }

    public List<Venue> findAll() {
        return venueRepository.findAll();
    }

    @Cacheable(value = "venue:all")
    public List<VenueDto> loadAllActiveVenues() {
        return venueServiceMapper.venuesToDtoList(venueRepository.findAllActiveVenues());
    }

    @Cacheable(value = "venue:all")
    public List<VenueDto> loadAllVenues() {
        return venueServiceMapper.venuesToDtoList(venueRepository.findAll());
//        String cacheKey = "venue:all";
//        return getCachedValue(cacheKey);
    }
//
//    private List<VenueDto> getCachedValue(String cacheKey) {
//        try {
//            Cache cache = cacheManager.getCache("venue:all");
//            if (cache != null) {
//                Cache.ValueWrapper valueWrapper = cache.get(cacheKey);
//                if (valueWrapper != null && valueWrapper.get() instanceof List) {
//                    return (List<VenueDto>) valueWrapper.get();
//                }
//            }
//
//            List<Venue> venues = venueRepository.findAll();
//            List<VenueDto> venueDtos = venueServiceMapper.venuesToDtoList(venues);
//            if (cache != null) {
//                cache.put(cacheKey, venueDtos);
//            }
//            return venueDtos;
//        } catch (RedisTimeoutException e) {
//            // Log the exception
//            logger.error("RedisTimeoutException occurred while caching all venues: {}", e.getMessage());
//            // Fetch data from database as fallback
//            return venueServiceMapper.venuesToDtoList(venueRepository.findAll());
//        }
//    }

    public Venue buildVenue(VenueDto body, String username) {
        List<Booking> bookings = new ArrayList<>();
        List<Image> images = new ArrayList<>();

        return Venue.builder()
                .venueType(body.venueType())
                .username(username)
                .images(images)
                .phone(body.phone())
                .name(body.name())
                .description(body.description())
                .location(body.location())
                .estimate(Integer.parseInt(body.estimate()))
                .bookings(bookings)
                .capacity(Integer.parseInt(body.capacity()))
                .status(body.status())
                .build();

    }


}
