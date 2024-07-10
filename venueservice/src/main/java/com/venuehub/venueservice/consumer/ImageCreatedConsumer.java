package com.venuehub.venueservice.consumer;

import com.venuehub.broker.constants.MyQueue;
import com.venuehub.broker.consumer.BaseConsumer;
import com.venuehub.broker.event.image.ImageCreatedEvent;
import com.venuehub.commons.exception.NoSuchVenueException;
import com.venuehub.venueservice.dto.VenueDto;
import com.venuehub.venueservice.model.ImageUri;
import com.venuehub.venueservice.model.Venue;
import com.venuehub.venueservice.service.ImageUriService;
import com.venuehub.venueservice.service.VenueService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class ImageCreatedConsumer extends BaseConsumer<ImageCreatedEvent> {
    public static final Logger logger = LoggerFactory.getLogger(ImageCreatedEvent.class);
    private final VenueService venueService;
    private final ImageUriService imageUriService;

    @Autowired
    public ImageCreatedConsumer(VenueService venueService, ImageUriService imageUriService) {
        this.venueService = venueService;
        this.imageUriService = imageUriService;
    }

    @Override
    @RabbitListener(queues = MyQueue.Constants.IMAGE_CREATED_QUEUE_VENUE_SERVICE)
    @Transactional
    public void consume(ImageCreatedEvent event) {
        logger.info("{} reached {} {}", event.getClass().getSimpleName(), getClass().getSimpleName(), event);

        String venueName = event.venueName().replace("-", " ");

        List<VenueDto> venueDtoList = venueService.findByUsername(event.vendorName());

        VenueDto venueDto = venueDtoList.stream()
                .filter(v -> v.name().equals(venueName))
                .toList().get(0);

        Venue venue = venueService.findById(venueDto.id()).orElseThrow(NoSuchVenueException::new);

        ImageUri imageUri = new ImageUri(event.imageId(), event.uri());
        imageUriService.save(imageUri);
        venue.getImageUris().add(imageUri);
        venueService.save(venue);
    }
}
