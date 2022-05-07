package tourGuide.integration;

import java.util.List;
import java.util.UUID;

import gpsUtil.GpsUtil;
import gpsUtil.location.VisitedLocation;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import rewardCentral.RewardCentral;
import tourGuide.dto.NearbyAttractionsDto;
import tourGuide.dto.UserDto;
import tourGuide.service.RewardsService;
import tourGuide.service.TourGuideService;
import tripPricer.Provider;

import tourGuide.helper.InternalTestDataSet;
import tourGuide.helper.InternalTestHelper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class IntegrationTourGuideTestIT {

	@Test
	public void getUserLocation() throws NumberFormatException {
		GpsUtil gpsUtil = new GpsUtil();
		RewardCentral rewardCentral = new RewardCentral();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		InternalTestDataSet internalTestDataSet = new InternalTestDataSet();
		InternalTestHelper.setInternalUserNumber(0);
		TourGuideService tourGuideService = new TourGuideService(internalTestDataSet, gpsUtil , rewardsService, rewardCentral);
		
		UserDto userDto = new UserDto(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		VisitedLocation visitedLocation = tourGuideService.trackUserLocation(userDto);
		tourGuideService.trackerService.stopTracking();
		assertEquals(visitedLocation.userId, userDto.getUserId());
	}
	
	@Test
	public void addUser() {
		GpsUtil gpsUtil = new GpsUtil();
		RewardCentral rewardCentral = new RewardCentral();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		InternalTestDataSet internalTestDataSet = new InternalTestDataSet();
		InternalTestHelper.setInternalUserNumber(0);
		TourGuideService tourGuideService = new TourGuideService(internalTestDataSet,gpsUtil , rewardsService, rewardCentral);
		
		UserDto userDto = new UserDto(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		UserDto userDto2 = new UserDto(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");

		tourGuideService.addUser(userDto);
		tourGuideService.addUser(userDto2);
		
		UserDto retriedUserDto = tourGuideService.getUser(userDto.getUserName());
		UserDto retriedUser2Dto = tourGuideService.getUser(userDto2.getUserName());

		tourGuideService.trackerService.stopTracking();
		
		assertEquals(userDto, retriedUserDto);
		assertEquals(userDto2, retriedUser2Dto);
	}
	
	@Test
	public void getAllUsers() {
		GpsUtil gpsUtil = new GpsUtil();
		RewardCentral rewardCentral = new RewardCentral();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		InternalTestDataSet internalTestDataSet = new InternalTestDataSet();
		InternalTestHelper.setInternalUserNumber(0);
		TourGuideService tourGuideService = new TourGuideService(internalTestDataSet,gpsUtil , rewardsService, rewardCentral);
		
		UserDto userDto = new UserDto(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		UserDto userDto2 = new UserDto(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");

		tourGuideService.addUser(userDto);
		tourGuideService.addUser(userDto2);
		
		List<UserDto> allUsersDto = tourGuideService.getAllUsers();

		tourGuideService.trackerService.stopTracking();
		
		assertTrue(allUsersDto.contains(userDto));
		assertTrue(allUsersDto.contains(userDto2));
	}
	
	@Test
	public void trackUser() {
		GpsUtil gpsUtil = new GpsUtil();
		RewardCentral rewardCentral = new RewardCentral();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		InternalTestDataSet internalTestDataSet = new InternalTestDataSet();
		InternalTestHelper.setInternalUserNumber(0);
		TourGuideService tourGuideService = new TourGuideService(internalTestDataSet,gpsUtil , rewardsService, rewardCentral);
		
		UserDto userDto = new UserDto(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		VisitedLocation visitedLocation;
		visitedLocation = tourGuideService.getUserLocation(userDto);
		
		tourGuideService.trackerService.stopTracking();
		
		assertEquals(userDto.getUserId(), visitedLocation.userId);
	}

	@Test
	public void getNearbyAttractions() {
		GpsUtil gpsUtil = new GpsUtil();
		RewardCentral rewardCentral = new RewardCentral();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		InternalTestDataSet internalTestDataSet = new InternalTestDataSet();
		InternalTestHelper.setInternalUserNumber(0);
		TourGuideService tourGuideService = new TourGuideService(internalTestDataSet,gpsUtil , rewardsService, rewardCentral);
		
		UserDto userDto = new UserDto(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		VisitedLocation visitedLocation;
		visitedLocation = tourGuideService.trackUserLocation(userDto);
		
		List<NearbyAttractionsDto> attractions = tourGuideService.getNearByAttractions(visitedLocation);
		
		tourGuideService.trackerService.stopTracking();
		
		assertEquals(gpsUtil.getAttractions().size(), attractions.size());
	}
	
	public void getTripDeals() {
		GpsUtil gpsUtil = new GpsUtil();
		RewardCentral rewardCentral = new RewardCentral();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		InternalTestDataSet internalTestDataSet = new InternalTestDataSet();
		InternalTestHelper.setInternalUserNumber(0);
		TourGuideService tourGuideService = new TourGuideService(internalTestDataSet,gpsUtil , rewardsService, rewardCentral);
		
		UserDto userDto = new UserDto(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");

		List<Provider> providers = tourGuideService.getTripDeals(userDto);
		
		tourGuideService.trackerService.stopTracking();
		
		assertEquals(10, providers.size());
	}
}