package com.venuehub.venueservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.venuehub.broker.constants.MyExchange;
import com.venuehub.broker.event.venue.VenueCreatedEvent;
import com.venuehub.broker.event.venue.VenueDeletedEvent;
import com.venuehub.broker.event.venue.VenueUpdatedEvent;
import com.venuehub.broker.producer.venue.VenueCreatedProducer;
import com.venuehub.broker.producer.venue.VenueDeletedProducer;
import com.venuehub.broker.producer.venue.VenueUpdatedProducer;
import com.venuehub.venueservice.dto.VenueDto;
import com.venuehub.venueservice.model.BookedVenue;
import com.venuehub.venueservice.model.ImageData;
import com.venuehub.venueservice.model.Venue;
import com.venuehub.venueservice.service.BookedVenueService;
import com.venuehub.venueservice.service.ImageDataService;
import com.venuehub.venueservice.service.VenueService;
import com.venuehub.venueservice.utils.JwtTestImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(VenueController.class)
class VenueControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private JwtTestImpl jwtTestImpl;
    @MockBean
    private Jwt jwt;
    @MockBean
    private RabbitTemplate rabbitTemplate;
    @MockBean
    private BookedVenueService bookedVenueService;
    @MockBean
    private ImageDataService imageDataService;
    @MockBean
    private VenueService venueService;
    @Autowired
    private VenueCreatedProducer venueCreatedProducer;
    @Autowired
    private VenueDeletedProducer venueDeletedProducer;
    @Autowired
    private VenueUpdatedProducer venueUpdatedProducer;
    private Long venueId;
    private Venue venue;
    private VenueDto venueDto;
    private List<BookedVenue> bookings;
    private int capacity;
    private String username;
    private String venueName;
    private String venueType;
    private String location;
    private String estimate;
    private String phone;
    private String myJwt;
    private final List<ImageData> images = new ArrayList<>();
    private MockMultipartFile imageFile1;
    private MockMultipartFile imageFile2;
    @Captor
    private ArgumentCaptor<Venue> venueArgumentCaptor;

    @BeforeEach
    void BeforeEach() {
        venueId = 1L;
        estimate = "80000";
        capacity = 200;
        username = "test_user";
        myJwt = jwtTestImpl.generateJwt(username, "VENDOR");
        venueType = "C";
        phone = "03178923162";
        venueName = "Marquee Venue";
        location = "ABC Street, Karachi, Pakistan";
        bookings = new ArrayList<>();

        ImageData imageData1 = new ImageData();
        ImageData imageData2 = new ImageData();
        byte[] img1 = {};
        byte[] img2 = {};
        imageData1.setImage(img1);
        imageData2.setImage(img2);

        imageFile1 = new MockMultipartFile("images", "image1.jpg", "image/png", imageData1.getImage());
        imageFile2 = new MockMultipartFile("images", "image2.png", "image/png", imageData2.getImage());

        images.add(imageData1);
        images.add(imageData2);

        venue = Venue.builder()
                .venueType(venueType)
                .id(venueId)
                .username(username)
                .images(images)
                .phone(phone)
                .name(venueName)
                .location(location)
                .estimate(estimate)
                .bookings(bookings)
                .capacity(capacity)
                .build();
        venueDto = new VenueDto(venueName, venueType, location, capacity, phone, estimate, bookings);

    }

    public String asJsonString(Object obj) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(obj); // Convert object to JSON string

    }

    @Nested
    class getVenueById {
        @Test
        void Expect_404_When_Venue_Not_Found() throws Exception {
            mvc.perform(
                            MockMvcRequestBuilders.get("/venue/" + venueId)
                    )
                    .andExpect(status().isNotFound());
        }

        @Test
        void Should_Return_Venue() throws Exception {
            Mockito.when(venueService.findById(venueId)).thenReturn(Optional.of(venue));

            MvcResult result = mvc.perform(
                            MockMvcRequestBuilders.get("/venue/" + venueId)
                    )
                    .andExpect(status().isOk()).andReturn();

            String response = result.getResponse().getContentAsString();
            System.out.println(response);
            assertThat(response).contains(venueId.toString());
            assertThat(response).contains(String.valueOf(capacity));
            assertThat(response).contains(venueType);
            assertThat(response).contains(phone);
        }
    }

    @Nested
    class addVenue {
        @Test
        void Expect_401_When_User_UnAuthorized() throws Exception {

            mvc.perform(MockMvcRequestBuilders.multipart("/venue")
                    .file(imageFile1)
                    .file(imageFile2)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .content(asJsonString(venueDto))
            ).andExpect(status().isUnauthorized());
        }

        @Test
        void Expect_403_When_Action_Forbidden() throws Exception {
            String wrongJwt = jwtTestImpl.generateJwt(username, "USER");

            mvc.perform(MockMvcRequestBuilders.multipart("/venue")
                    .file(imageFile1)
                    .file(imageFile2)
                    .header("Authorization", "Bearer " + wrongJwt)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .param("venueName", venueDto.name())
                    .param("venueType", venueDto.venueType())
                    .param("location", venueDto.location())
                    .param("capacity", String.valueOf(venueDto.capacity())) // Convert capacity to String
                    .param("phone", venueDto.phone())
                    .param("estimate", String.valueOf(venueDto.estimate()))
            ).andExpect(status().isForbidden());
        }

        @Test
        void Should_Add_Venue() throws Exception {
            MvcResult result = mvc.perform(MockMvcRequestBuilders.multipart("/venue")
                    .file(imageFile1)
                    .file(imageFile2)
                    .header("Authorization", "Bearer " + myJwt)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .param("venueName", venueDto.name())
                    .param("venueType", venueDto.venueType())
                    .param("location", venueDto.location())
                    .param("capacity", String.valueOf(venueDto.capacity())) // Convert capacity to String
                    .param("phone", venueDto.phone())
                    .param("estimate", String.valueOf(venueDto.estimate()))
            ).andExpect(status().isCreated()).andReturn();

            String response = result.getResponse().getContentAsString();
            assertThat(response).contains(venueId.toString());
            assertThat(response).contains(String.valueOf(capacity));
            assertThat(response).contains(venueType);
            assertThat(response).contains(phone);
        }

        @Test
        void Should_Produce_Event_1_times() throws Exception {

            mvc.perform(MockMvcRequestBuilders.multipart("/venue")
                    .file(imageFile1)
                    .file(imageFile2)
                    .header("Authorization", "Bearer " + myJwt)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .param("venueName", venueDto.name())
                    .param("venueType", venueDto.venueType())
                    .param("location", venueDto.location())
                    .param("capacity", String.valueOf(venueDto.capacity())) // Convert capacity to String
                    .param("phone", venueDto.phone())
                    .param("estimate", String.valueOf(venueDto.estimate()))
            ).andExpect(status().isCreated());

            VenueCreatedEvent event = new VenueCreatedEvent(0L, username);
            Mockito.verify(rabbitTemplate, times(1)).convertAndSend(MyExchange.BOOKING_EXCHANGE.name(), "venue-created", event);
        }

    }

    @Nested
    class deleteVenue {
        @Test
        void Expect_401_When_User_UnAuthorized() throws Exception {

            mvc.perform(MockMvcRequestBuilders.delete("/venue/" + venueId))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void Expect_404_Venue_Not_Found() throws Exception {
            Mockito.when(venueService.findById(venueId)).thenReturn(Optional.of(venue));
            long wrongVenueId = 5L;

            mvc.perform(MockMvcRequestBuilders.delete("/venue/" + wrongVenueId)
                            .header("Authorization", "Bearer " + myJwt))
                    .andExpect(status().isNotFound());
        }

        @Test
        void Expect_403_When_Action_Forbidden() throws Exception {
            Mockito.when(venueService.findById(venueId)).thenReturn(Optional.of(venue));

            //USER ROLE INSTEAD OF VENDOR
            String wrongJwt = jwtTestImpl.generateJwt(username, "USER");


            mvc.perform(MockMvcRequestBuilders.delete("/venue/" + venueId)
                            .header("Authorization", "Bearer " + wrongJwt))
                    .andExpect(status().isForbidden());
        }

        @Test
        void Expect_403_When_User_is_different() throws Exception {
            Mockito.when(venueService.findById(venueId)).thenReturn(Optional.of(venue));

            //WRONG USERNAME
            String wrongJwt = jwtTestImpl.generateJwt("wrong-user", "VENDOR");


            mvc.perform(MockMvcRequestBuilders.delete("/venue/" + venueId)
                            .header("Authorization", "Bearer " + wrongJwt))
                    .andExpect(status().isForbidden());
        }

        @Test
        void Should_Delete_Venue() throws Exception {
            Mockito.when(venueService.findById(venueId)).thenReturn(Optional.of(venue));


            mvc.perform(MockMvcRequestBuilders.delete("/venue/" + venueId)
                            .header("Authorization", "Bearer " + myJwt))
                    .andExpect(status().isNoContent());

            Mockito.verify(venueService, times(1)).deleteById(venueId);
        }

        @Test
        void Should_Produce_Event_1_time() throws Exception {
            Mockito.when(venueService.findById(venueId)).thenReturn(Optional.of(venue));


            mvc.perform(MockMvcRequestBuilders.delete("/venue/" + venueId)
                            .header("Authorization", "Bearer " + myJwt))
                    .andExpect(status().isNoContent());

            VenueDeletedEvent event = new VenueDeletedEvent(venueId, username);
            Mockito.verify(rabbitTemplate, times(1)).convertAndSend(MyExchange.BOOKING_EXCHANGE.name(), "venue-deleted", event);

        }

    }

    @Nested
    class getVenue {

        @Test
        void Should_Return_Empty_List() throws Exception {

            MvcResult result = mvc.perform(MockMvcRequestBuilders.get("/venue")
                            .header("Authorization", "Bearer " + myJwt))
                    .andExpect(status().isOk()).andReturn();
            String response = result.getResponse().getContentAsString();
            System.out.println(response);
            assertThat(response).contains("[]");


        }

        @Test
        void Should_Return_Venue_List() throws Exception {
            List<Venue> venueList = new ArrayList<>();
            venueList.add(venue);
            Mockito.when(venueService.findAll()).thenReturn(venueList);

            MvcResult result = mvc.perform(MockMvcRequestBuilders.get("/venue")
                            .header("Authorization", "Bearer " + myJwt))
                    .andExpect(status().isOk()).andReturn();
            String response = result.getResponse().getContentAsString();
            assertThat(response).contains(venueId.toString());
            assertThat(response).contains(String.valueOf(capacity));
            assertThat(response).contains(venueType);
            assertThat(response).contains(phone);


        }
    }

    @Nested
    class updateVenue {
        @Test
        void Expect_401_When_User_UnAuthorized() throws Exception {

            mvc.perform(MockMvcRequestBuilders.put("/venue/" + venueId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(venueDto)))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void Expect_404_Venue_Not_Found() throws Exception {
            Mockito.when(venueService.findById(venueId)).thenReturn(Optional.of(venue));
            long wrongVenueId = 5L;

            mvc.perform(MockMvcRequestBuilders.put("/venue/" + wrongVenueId)
                            .header("Authorization", "Bearer " + myJwt)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(venueDto)))
                    .andExpect(status().isNotFound());
        }

        @Test
        void Expect_403_When_Action_Forbidden() throws Exception {
            Mockito.when(venueService.findById(venueId)).thenReturn(Optional.of(venue));

            //USER ROLE INSTEAD OF VENDOR
            String wrongJwt = jwtTestImpl.generateJwt(username, "USER");


            mvc.perform(MockMvcRequestBuilders.put("/venue/" + venueId)
                            .header("Authorization", "Bearer " + wrongJwt)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(venueDto)))
                    .andExpect(status().isForbidden());
        }

        @Test
        void Expect_403_When_User_is_different() throws Exception {
            Mockito.when(venueService.findById(venueId)).thenReturn(Optional.of(venue));

            //WRONG USERNAME
            String wrongJwt = jwtTestImpl.generateJwt("wrong-user", "VENDOR");

            mvc.perform(MockMvcRequestBuilders.put("/venue/" + venueId)
                            .header("Authorization", "Bearer " + wrongJwt)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(venueDto)))
                    .andExpect(status().isForbidden());
        }

        @Test
        void Should_Update_Venue() throws Exception {
            Mockito.when(venueService.findById(venueId)).thenReturn(Optional.of(venue));
            String updatedEstimate = "55000";
            int updatedCapacity = 50;
            VenueDto updatedVenueDto = new VenueDto(venueName, venueType, location, updatedCapacity, phone, updatedEstimate, bookings);

            MvcResult result = mvc.perform(MockMvcRequestBuilders.put("/venue/" + venueId)
                            .header("Authorization", "Bearer " + myJwt)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(updatedVenueDto)))
                    .andExpect(status().isNoContent()).andReturn();

            Mockito.verify(venueService, times(1)).save(venueArgumentCaptor.capture());
            Venue updatedVenue = venueArgumentCaptor.getValue();
            assertThat(updatedVenue.toString()).doesNotContain(estimate);
            assertThat(updatedVenue.toString()).contains(updatedEstimate);

        }

        @Test
        void Should_Produce_Event_1_time() throws Exception {
            Mockito.when(venueService.findById(venueId)).thenReturn(Optional.of(venue));

            mvc.perform(MockMvcRequestBuilders.put("/venue/" + venueId)
                            .header("Authorization", "Bearer " + myJwt)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(venueDto)))
                    .andExpect(status().isNoContent());
            VenueUpdatedEvent event = new VenueUpdatedEvent(
                    venueId,
                    venueName,
                    capacity,
                    phone,
                    estimate
            );
            Mockito.verify(rabbitTemplate, times(1)).convertAndSend(MyExchange.BOOKING_EXCHANGE.name(), "venue-updated", event);
        }


    }
}