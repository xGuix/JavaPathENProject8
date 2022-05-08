package tourGuide;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import rewardCentral.RewardCentral;
import tourGuide.helper.InternalTestDataSet;
import tourGuide.helper.InternalTestHelper;
import tourGuide.service.RewardsService;
import tourGuide.service.TourGuideService;
import tourGuide.dto.UserDto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class TestPerformanceIT {

	static final Logger logger = LogManager.getLogger("TourGuidePerformanceLog");

	/**
	 * Create ThreadPool of 1000
	 */
	Executor executor = Executors.newFixedThreadPool(2000);
	/*
	 * A note on performance improvements:
	 *     
	 *     The number of users generated for the high volume tests can be easily adjusted via this method:
	 *     
	 *     		InternalTestHelper.setInternalUserNumber(100000);
	 *     
	 *     
	 *     These tests can be modified to suit new solutions, just as long as the performance metrics
	 *     at the end of the tests remains consistent. 
	 * 
	 *     These are performance metrics that we are trying to hit:
	 *     
	 *     highVolumeTrackLocation: 100,000 users within 15 minutes:
	 *     		assertTrue(TimeUnit.MINUTES.toSeconds(15) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
     *
     *     highVolumeGetRewards: 100,000 users within 20 minutes:
	 *          assertTrue(TimeUnit.MINUTES.toSeconds(20) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
	 */

	@Test
	public void highVolumeTrackLocation() {
		GpsUtil gpsUtil = new GpsUtil();
		RewardCentral rewardCentral = new RewardCentral();
		RewardsService rewardsService = new RewardsService(gpsUtil, rewardCentral);
		// Users should be incremented up to 100,000, and test finishes within 15 minutes
		InternalTestDataSet internalTestDataSet = new InternalTestDataSet();
		InternalTestHelper.setInternalUserNumber(100000);
		TourGuideService tourGuideService = new TourGuideService(internalTestDataSet,gpsUtil, rewardsService, rewardCentral);

		List<UserDto> allUsersDto;
		ArrayList<CompletableFuture> completableFutures;
		completableFutures = new ArrayList<>();
		allUsersDto = tourGuideService.getAllUsers();
		
	    StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		for(UserDto userDto : allUsersDto) {
			CompletableFuture completable = CompletableFuture.runAsync(() -> {
				tourGuideService.trackUserLocation(userDto);
				}, executor);
			completableFutures.add(completable);
		}
		CompletableFuture.allOf(completableFutures.toArray(new CompletableFuture[completableFutures.size()])).join();

		tourGuideService.trackerService.stopTracking();
		stopWatch.stop();

		logger.info("highVolumeTrackLocation / Time Elapsed: {} seconds.", TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
		assertTrue(TimeUnit.MINUTES.toSeconds(15) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
	}

	@Test
	public void highVolumeGetRewards() {
		GpsUtil gpsUtil = new GpsUtil();
		RewardCentral rewardCentral = new RewardCentral();
		RewardsService rewardsService = new RewardsService(gpsUtil, rewardCentral);
		// Users should be incremented up to 100,000 and test finishes within 20 minutes
		InternalTestDataSet internalTestDataSet = new InternalTestDataSet();
		InternalTestHelper.setInternalUserNumber(100000);
		StopWatch stopWatch = new StopWatch();
		rewardsService.resetThreadPool();
		stopWatch.start();
		TourGuideService tourGuideService = new TourGuideService(internalTestDataSet,gpsUtil, rewardsService, rewardCentral);
		
	    Attraction attraction = gpsUtil.getAttractions().get(0);
		List<UserDto> allUsersDto;
		List<CompletableFuture> completableFutures = new ArrayList<>();
		allUsersDto = tourGuideService.getAllUsers();
		allUsersDto.forEach(u -> u.addToVisitedLocations(new VisitedLocation(u.getUserId(), attraction, new Date())));

		allUsersDto.forEach(u -> {
			CompletableFuture completable = CompletableFuture.runAsync(() -> {
				rewardsService.calculateRewards(u);
				}, executor);
			completableFutures.add(completable);
		});
		CompletableFuture.allOf(completableFutures.toArray(new CompletableFuture[completableFutures.size()])).join();

		for(UserDto userDto : allUsersDto) {
			assertTrue(userDto.getUserRewards().size() > 0);
		}

		tourGuideService.trackerService.stopTracking();
		stopWatch.stop();

		logger.info("highVolumeGetRewards / Time Elapsed: {} seconds.", TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
		assertTrue(TimeUnit.MINUTES.toSeconds(20) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
	}
}