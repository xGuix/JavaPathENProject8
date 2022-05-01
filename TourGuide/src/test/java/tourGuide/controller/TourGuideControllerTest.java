package tourGuide.controller;

import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import tourGuide.dto.NearbyAttractions;
import tourGuide.service.TourGuideService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@WebMvcTest
public class TourGuideControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    TourGuideService tourGuideService;

    @Test
    public void indexTest() throws Exception {
        mockMvc.perform(get("/")).andExpect(status().isOk());
    }

    @Test
    public void getLocation() throws Exception {
        //Given
        String userName = "username";
        Location location = new Location(33.817595D,-117.922008D);
        Date date = new Date();
        VisitedLocation visitedLocation = new VisitedLocation(UUID.randomUUID(),location, date);
        //When
        Mockito.when(tourGuideService.getUserLocation(tourGuideService.getUser(userName))).thenReturn(visitedLocation);
        //Then
        mockMvc.perform(get("/getLocation").param("userName", userName)).andExpect(status().isOk());
    }

    @Test
    public void getNearbyAttractionsTest() throws Exception {
        //Given
        String userName = "username";
        Location location = new Location(33.817595D,-117.922008D);
        Date date = new Date();
        VisitedLocation visitedLocation = new VisitedLocation(UUID.randomUUID(),location, date);
        List<NearbyAttractions> nearbyAttractionsList = new ArrayList<>();
        //When
        Mockito.when(tourGuideService.getUserLocation(tourGuideService.getUser(userName))).thenReturn(visitedLocation);
        Mockito.when(tourGuideService.getNearByAttractions(visitedLocation)).thenReturn(nearbyAttractionsList);
        //Then
        mockMvc.perform(get("/getNearbyAttractions").param("userName", userName)).andExpect(status().isOk());
    }

    @Test
    public void getRewardsTest() throws Exception {
        //Given
        String userName = "username";
        //When
        Mockito.when(tourGuideService.getUserRewards(tourGuideService.getUser(userName))).thenReturn(new ArrayList<>());
        //Then
        mockMvc.perform(get("/getRewards").param("userName", userName)).andExpect(status().isOk());
    }

    @Test
    public void getAllCurrentLocationsTest() throws Exception {
        //When
        Mockito.when(tourGuideService.getAllUsers()).thenReturn(new ArrayList<>());
        //Then
        mockMvc.perform(get("/getAllCurrentLocations")).andExpect(status().isOk());
    }

    @Test
    public void getTripDealsTest() throws Exception {
        //Given
        String userName = "username";
        //When
        Mockito.when(tourGuideService.getTripDeals(tourGuideService.getUser(userName))).thenReturn(new ArrayList<>());
        //Then
        mockMvc.perform(get("/getTripDeals").param("userName", userName)).andExpect(status().isOk());
    }
}