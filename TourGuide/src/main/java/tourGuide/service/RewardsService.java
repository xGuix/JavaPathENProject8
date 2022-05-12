package tourGuide.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.stereotype.Service;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import rewardCentral.RewardCentral;
import tourGuide.dto.UserDto;
import tourGuide.dto.UserRewardDto;

/**
 * Reward Service
 */
@Service
public class RewardsService {

    private static final double STATUTE_MILES_PER_NAUTICAL_MILE = 1.15077945;

	// proximity in miles
    private final int defaultProximityBuffer = 100;
	private int proximityBuffer = defaultProximityBuffer;
	public final int attractionProximityRange = 10000;
	private final GpsUtil gpsUtil;
	private final RewardCentral rewardsCentral;
	
	public RewardsService(GpsUtil gpsUtil, RewardCentral rewardCentral) {
		this.gpsUtil = gpsUtil;
		this.rewardsCentral = rewardCentral;
	}
	
	public void setProximityBuffer(int proximityBuffer) {
		this.proximityBuffer = proximityBuffer;
	}
	
	public void setDefaultProximityBuffer() {
		proximityBuffer = defaultProximityBuffer;
	}
	
	public List<UserRewardDto> calculateRewards(UserDto userDto) {
		List<VisitedLocation> userLocations = userDto.getVisitedLocations();
		List<Attraction> attractions = gpsUtil.getAttractions();
		//List<VisitedLocation> userLocationsConverter = new CopyOnWriteArrayList<>(userLocations);

		userDto.getVisitedLocations().forEach(visitedLocation -> {
			attractions.forEach(a -> {
				if (userDto.getUserRewards().stream().noneMatch(r -> r.getAttraction().attractionName.equals(a.attractionName))) {
					if (nearAttraction(visitedLocation, a)) {
						userDto.getUserRewards().add(new UserRewardDto(visitedLocation, a, getRewardPoints(a, userDto)));
					}
				}
			});
		});
		return userDto.getUserRewards();
	}

	public boolean isWithinAttractionProximity(Attraction attraction, Location location) {
		return !(getDistance(attraction, location) > attractionProximityRange);
	}
	
	private boolean nearAttraction(VisitedLocation visitedLocation, Attraction attraction) {
		return !(getDistance(attraction, visitedLocation.location) > proximityBuffer);
	}
	
	private int getRewardPoints(Attraction attraction, UserDto userDto) {
		return rewardsCentral.getAttractionRewardPoints(attraction.attractionId, userDto.getUserId());
	}
	
	public double getDistance(Location loc1, Location loc2) {
        double lat1 = Math.toRadians(loc1.latitude);
        double lon1 = Math.toRadians(loc1.longitude);
        double lat2 = Math.toRadians(loc2.latitude);
        double lon2 = Math.toRadians(loc2.longitude);

        double angle = Math.acos(Math.sin(lat1) * Math.sin(lat2)
                               + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2));

        double nauticalMiles = 60 * Math.toDegrees(angle);
		return STATUTE_MILES_PER_NAUTICAL_MILE * nauticalMiles;
	}
}