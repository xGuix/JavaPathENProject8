package tourGuide.integration;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import rewardCentral.RewardCentral;
import tourGuide.helper.InternalTestDataSet;
import tourGuide.helper.InternalTestHelper;
import tourGuide.dto.UserDto;
import tourGuide.dto.UserRewardDto;
import tourGuide.service.RewardsService;
import tourGuide.service.TourGuideService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class IntegrationRewardsTestIT {

	@Test
	public void userGetRewards() {
		GpsUtil gpsUtil = new GpsUtil();
		RewardCentral rewardCentral = new RewardCentral();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		InternalTestDataSet internalTestDataSet = new InternalTestDataSet();
		InternalTestHelper.setInternalUserNumber(1);
		TourGuideService tourGuideService = new TourGuideService(internalTestDataSet, rewardsService, rewardCentral);
		
		UserDto userDto = new UserDto(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		Attraction attraction = gpsUtil.getAttractions().get(0);
		userDto.addToVisitedLocations(new VisitedLocation(userDto.getUserId(), attraction, new Date()));
		UserRewardDto userRewardDto = new UserRewardDto(userDto.getLastVisitedLocation(),attraction, rewardCentral.getAttractionRewardPoints(attraction.attractionId, userDto.getUserId()));
		userDto.addUserReward(userRewardDto);
		List<UserRewardDto> userRewardsDto = userDto.getUserRewards();
		tourGuideService.trackerService.stopTracking();
		assertEquals(userDto.getUserRewards().size(), userRewardsDto.size());
	}
	
	@Test
	public void isWithinAttractionProximity() {
		GpsUtil gpsUtil = new GpsUtil();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		Attraction attraction = gpsUtil.getAttractions().get(0);
		assertTrue(rewardsService.isWithinAttractionProximity(attraction, attraction));
	}
	
	//TODO: Needs fixed - can throw ConcurrentModificationException
	@Test
	public void nearAllAttractions() {
		GpsUtil gpsUtil = new GpsUtil();
		RewardCentral rewardCentral = new RewardCentral();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		rewardsService.setProximityBuffer(Integer.MAX_VALUE);

		InternalTestDataSet internalTestDataSet = new InternalTestDataSet();
		InternalTestHelper.setInternalUserNumber(1);
		TourGuideService tourGuideService = new TourGuideService(internalTestDataSet, rewardsService, rewardCentral);

		rewardsService.calculateRewards(tourGuideService.getAllUsers().get(0));
		List<UserRewardDto> userRewardsDto = tourGuideService.getUserRewards(tourGuideService.getAllUsers().get(0));
		tourGuideService.trackerService.stopTracking();

		assertEquals(1, userRewardsDto.size());
	}
}