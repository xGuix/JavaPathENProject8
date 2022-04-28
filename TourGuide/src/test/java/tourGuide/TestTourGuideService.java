package tourGuide;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.UUID;

import org.junit.Test;

import gpsUtil.GpsUtil;
import gpsUtil.location.VisitedLocation;
import rewardCentral.RewardCentral;
import tourGuide.dto.NearbyAttractions;
import tourGuide.helper.InternalTestDataSet;
import tourGuide.helper.InternalTestHelper;
import tourGuide.service.RewardsService;
import tourGuide.service.TourGuideService;
import tourGuide.user.User;
import tripPricer.Provider;

public class TestTourGuideService {

	@Test
	public void getUserLocation() {
		GpsUtil gpsUtil = new GpsUtil();
		RewardCentral rewardCentral = new RewardCentral();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		InternalTestDataSet internalTestDataSet = new InternalTestDataSet();
		InternalTestHelper.setInternalUserNumber(0);
		TourGuideService tourGuideService = new TourGuideService(internalTestDataSet, gpsUtil, rewardsService, rewardCentral);
		
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		VisitedLocation visitedLocation = tourGuideService.trackUserLocation(user);
		tourGuideService.trackerService.stopTracking();
		assertEquals(visitedLocation.userId, user.getUserId());
	}
	
	@Test
	public void addUser() {
		GpsUtil gpsUtil = new GpsUtil();
		RewardCentral rewardCentral = new RewardCentral();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		InternalTestDataSet internalTestDataSet = new InternalTestDataSet();
		InternalTestHelper.setInternalUserNumber(0);
		TourGuideService tourGuideService = new TourGuideService(internalTestDataSet, gpsUtil, rewardsService, rewardCentral);
		
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		User user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");

		tourGuideService.addUser(user);
		tourGuideService.addUser(user2);
		
		User retriedUser = tourGuideService.getUser(user.getUserName());
		User retriedUser2 = tourGuideService.getUser(user2.getUserName());

		tourGuideService.trackerService.stopTracking();
		
		assertEquals(user, retriedUser);
		assertEquals(user2, retriedUser2);
	}
	
	@Test
	public void getAllUsers() {
		GpsUtil gpsUtil = new GpsUtil();
		RewardCentral rewardCentral = new RewardCentral();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		InternalTestDataSet internalTestDataSet = new InternalTestDataSet();
		InternalTestHelper.setInternalUserNumber(0);
		TourGuideService tourGuideService = new TourGuideService(internalTestDataSet, gpsUtil, rewardsService, rewardCentral);
		
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		User user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");

		tourGuideService.addUser(user);
		tourGuideService.addUser(user2);
		
		List<User> allUsers = tourGuideService.getAllUsers();

		tourGuideService.trackerService.stopTracking();
		
		assertTrue(allUsers.contains(user));
		assertTrue(allUsers.contains(user2));
	}
	
	@Test
	public void trackUser() {
		GpsUtil gpsUtil = new GpsUtil();
		RewardCentral rewardCentral = new RewardCentral();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		InternalTestDataSet internalTestDataSet = new InternalTestDataSet();
		InternalTestHelper.setInternalUserNumber(0);
		TourGuideService tourGuideService = new TourGuideService(internalTestDataSet, gpsUtil, rewardsService, rewardCentral);
		
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		VisitedLocation visitedLocation = tourGuideService.getUserLocation(user);
		
		tourGuideService.trackerService.stopTracking();
		
		assertEquals(user.getUserId(), visitedLocation.userId);
	}

	@Test
	public void getNearbyAttractions() {
		GpsUtil gpsUtil = new GpsUtil();
		RewardCentral rewardCentral = new RewardCentral();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		InternalTestDataSet internalTestDataSet = new InternalTestDataSet();
		InternalTestHelper.setInternalUserNumber(0);
		TourGuideService tourGuideService = new TourGuideService(internalTestDataSet, gpsUtil, rewardsService, rewardCentral);
		
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		VisitedLocation visitedLocation = tourGuideService.trackUserLocation(user);
		
		List<NearbyAttractions> attractions = tourGuideService.getNearByAttractions(visitedLocation);
		
		tourGuideService.trackerService.stopTracking();
		
		assertEquals(5, attractions.size());
	}
	
	public void getTripDeals() {
		GpsUtil gpsUtil = new GpsUtil();
		RewardCentral rewardCentral = new RewardCentral();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		InternalTestDataSet internalTestDataSet = new InternalTestDataSet();
		InternalTestHelper.setInternalUserNumber(0);
		TourGuideService tourGuideService = new TourGuideService(internalTestDataSet, gpsUtil, rewardsService, rewardCentral);
		
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");

		List<Provider> providers = tourGuideService.getTripDeals(user);
		
		tourGuideService.trackerService.stopTracking();
		
		assertEquals(10, providers.size());
	}
}