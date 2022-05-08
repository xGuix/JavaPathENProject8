package tourGuide.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import rewardCentral.RewardCentral;
import tourGuide.dto.NearbyAttractionsDto;
import tourGuide.helper.InternalTestDataSet;
import tourGuide.dto.UserDto;
import tourGuide.dto.UserRewardDto;
import tripPricer.Provider;
import tripPricer.TripPricer;

import static tourGuide.helper.InternalTestDataSet.tripPricerApiKey;

/**
 * Tour Guide Service
 */
@Service
public class TourGuideService {

	static final Logger logger = LoggerFactory.getLogger("TourGuideServiceLog");

	private final GpsUtil gpsUtil;
	private final RewardsService rewardsService;
	private final RewardCentral rewardCentral;
	private final TripPricer tripPricer = new TripPricer();
	public TrackerService trackerService;
	public InternalTestDataSet internalTestDataSet;

	public TourGuideService(InternalTestDataSet internalTestDataSet, GpsUtil gpsUtil , RewardsService rewardsService, RewardCentral rewardCentral) {
		this.internalTestDataSet = internalTestDataSet;
		this.gpsUtil = gpsUtil;
		this.rewardsService = rewardsService;
		this.rewardCentral = rewardCentral;

		logger.info("TestMode enabled");
		logger.debug("Initializing users");
		internalTestDataSet.initializeInternalUsers();
		logger.debug("Finished initializing users");
		trackerService = new TrackerService(this);
		addShutDownHook();
	}

	public List<UserRewardDto> getUserRewards(UserDto userDto) {
		return userDto.getUserRewards();
	}
	
	public VisitedLocation getUserLocation(UserDto userDto) {
		VisitedLocation visitedLocation;
		visitedLocation = (userDto.getVisitedLocations().size() > 0) ?
			userDto.getLastVisitedLocation() :
			trackUserLocation(userDto);
		return visitedLocation;
	}

	public UserDto getUser(String userName) {
		return internalTestDataSet.internalUserMap.get(userName);
	}
	
	public List<UserDto> getAllUsers() {
		return new ArrayList<>(internalTestDataSet.internalUserMap.values());
	}
	
	public void addUser(UserDto userDto) {
		if(!internalTestDataSet.internalUserMap.containsKey(userDto.getUserName())) {
			internalTestDataSet.internalUserMap.put(userDto.getUserName(), userDto);
		}
	}
	
	public List<Provider> getTripDeals(UserDto userDto) {
		int cumulativeRewardPoints = 0;
		for (UserRewardDto i : userDto.getUserRewards()) {
			int rewardPoints = i.getRewardPoints();
			cumulativeRewardPoints += rewardPoints;
		}
		List<Provider> providers = tripPricer.getPrice(tripPricerApiKey, userDto.getUserId(), userDto.getUserPreferences().getNumberOfAdults(),
				userDto.getUserPreferences().getNumberOfChildren(), userDto.getUserPreferences().getTripDuration(), cumulativeRewardPoints);
		userDto.setTripDeals(providers);
		return providers;
	}
	
	public VisitedLocation trackUserLocation(UserDto userDto) {
		Locale.setDefault(Locale.US);
		VisitedLocation visitedLocation = gpsUtil.getUserLocation(userDto.getUserId());
		userDto.addToVisitedLocations(visitedLocation);
		rewardsService.calculateRewards(userDto);
		return visitedLocation;
	}

	public List<NearbyAttractionsDto> getNearByAttractions(VisitedLocation visitedLocation) {
		List<NearbyAttractionsDto> nearbyAttractionsListDto = new ArrayList<>();
		for(Attraction attraction : gpsUtil.getAttractions()) {
			if(rewardsService.isWithinAttractionProximity(attraction, visitedLocation.location)) {
				NearbyAttractionsDto nearBy = new NearbyAttractionsDto();
				nearBy.setAttraction(attraction);
				nearBy.setUserLocation(visitedLocation.location);
				nearBy.setDistance(rewardsService.getDistance(attraction, visitedLocation.location));
				nearBy.setRewardPoints(rewardCentral.getAttractionRewardPoints(attraction.attractionId, visitedLocation.userId));
				nearbyAttractionsListDto.add(nearBy);
			}
		}
		return nearbyAttractionsListDto;
	}
	
	private void addShutDownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread(() -> trackerService.stopTracking()));
	}
}