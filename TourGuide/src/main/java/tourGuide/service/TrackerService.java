package tourGuide.service;

import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import tourGuide.dto.UserDto;

/**
 * Tracker Service
 */
@Service
public class TrackerService extends Thread {

	private final Logger logger = LoggerFactory.getLogger(TrackerService.class);
	private final ExecutorService executorService = Executors.newSingleThreadExecutor();
	private final TourGuideService tourGuideService;
	private final RewardsService rewardsService;
	private boolean stop = false;

	/**
	 * Constructor
	 *
	 * @param tourGuideService TourGuideService
	 * @param rewardsService RewardService
	 */
	TrackerService(TourGuideService tourGuideService, RewardsService rewardsService) {
		this.tourGuideService = tourGuideService;
		this.rewardsService = rewardsService;
		executorService.submit(this);
	}

	/**
	 * Assures to shut down the Tracker thread
	 */
	public void stopTracking() {
		stop = true;
		executorService.shutdownNow();
	}

	/**
	 * Run
	 */
	@Override
	public void run() {
		StopWatch stopWatch = new StopWatch();
		ExecutorService trackExecutor = Executors.newSingleThreadExecutor();
		ExecutorService rewardExecutor = Executors.newSingleThreadExecutor();

		while(true) {
			if (Thread.currentThread().isInterrupted() || stop) {
				logger.debug("Tracker stopping");
				break;
			}

			List<UserDto> userDtoList = tourGuideService.getAllUsers();
			logger.debug("Begin Tracker. Tracking {} users.", userDtoList.size());

			stopWatch.start();
			List<CompletableFuture<?>> trackResult = userDtoList.stream()
							.map(userDto -> CompletableFuture.runAsync(() -> tourGuideService.trackUserLocation(userDto), trackExecutor)
									.thenRunAsync(() -> rewardsService.calculateRewards(userDto),rewardExecutor))
					.collect(Collectors.toList());
			trackResult.forEach(CompletableFuture::join);
			stopWatch.stop();

			logger.debug("Tracker  Time Elapsed: {} seconds.", TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
		}
	}
}