package tourGuide.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import rewardCentral.RewardCentral;
import tourGuide.dto.NearbyAttractions;
import tourGuide.helper.InternalTestDataSet;
import tourGuide.user.User;
import tourGuide.user.UserReward;
import tripPricer.Provider;
import tripPricer.TripPricer;

import static tourGuide.helper.InternalTestDataSet.tripPricerApiKey;

/**
 * Tour Guide Service
 */
@Service
public class TourGuideService {
	private final Logger logger = LoggerFactory.getLogger("TourGuideServiceLog");

	private final GpsUtil gpsUtil;
	private final RewardsService rewardsService;
	private final RewardCentral rewardCentral;
	private final TripPricer tripPricer = new TripPricer();
	public final TrackerService trackerService;
	public final InternalTestDataSet internalTestDataSet;

	boolean testMode = true;
	
	public TourGuideService(InternalTestDataSet internalTestDataSet, GpsUtil gpsUtil, RewardsService rewardsService, RewardCentral rewardCentral) {
		this.internalTestDataSet = internalTestDataSet;
		this.gpsUtil = gpsUtil;
		this.rewardsService = rewardsService;
		this.rewardCentral = rewardCentral;

		if(testMode) {
			logger.info("TestMode enabled");
			logger.debug("Initializing users");
			internalTestDataSet.initializeInternalUsers();
			logger.debug("Finished initializing users");
		}
		trackerService = new TrackerService(this);
		addShutDownHook();
	}
	
	public List<UserReward> getUserRewards(User user) {
		return user.getUserRewards();
	}
	
	public VisitedLocation getUserLocation(User user) {
		VisitedLocation visitedLocation;
		visitedLocation = (user.getVisitedLocations().size() > 0) ?
			user.getLastVisitedLocation() :
			trackUserLocation(user);
		return visitedLocation;
	}

	public User getUser(String userName) {
		return internalTestDataSet.internalUserMap.get(userName);
	}
	
	public List<User> getAllUsers() {
		return internalTestDataSet.internalUserMap.values().stream().collect(Collectors.toList());
	}
	
	public void addUser(User user) {
		if(!internalTestDataSet.internalUserMap.containsKey(user.getUserName())) {
			internalTestDataSet.internalUserMap.put(user.getUserName(), user);
		}
	}
	
	public List<Provider> getTripDeals(User user) {
		int cumulativeRewardPoints = 0;
		for (UserReward i : user.getUserRewards()) {
			int rewardPoints = i.getRewardPoints();
			cumulativeRewardPoints += rewardPoints;
		}
		List<Provider> providers = tripPricer.getPrice(tripPricerApiKey, user.getUserId(), user.getUserPreferences().getNumberOfAdults(), 
				user.getUserPreferences().getNumberOfChildren(), user.getUserPreferences().getTripDuration(), cumulativeRewardPoints);
		user.setTripDeals(providers);
		return providers;
	}
	
	public VisitedLocation trackUserLocation(User user) {
		VisitedLocation visitedLocation = gpsUtil.getUserLocation(user.getUserId());
		user.addToVisitedLocations(visitedLocation);
		rewardsService.calculateRewards(user);
		return visitedLocation;
	}

	public List<NearbyAttractions> getNearByAttractions(VisitedLocation visitedLocation) {
		List<NearbyAttractions> nearbyAttractions = new ArrayList<>();
		for(Attraction attraction : gpsUtil.getAttractions()) {
			if(rewardsService.isWithinAttractionProximity(attraction, visitedLocation.location)) {
				NearbyAttractions nearBy = new NearbyAttractions();
				nearBy.setAttraction(attraction);
				nearBy.setUserLocation(visitedLocation.location);
				nearBy.setDistance(rewardsService.getDistance(attraction, visitedLocation.location));
				nearBy.setRewardPoints(rewardCentral.getAttractionRewardPoints(attraction.attractionId, visitedLocation.userId));
				nearbyAttractions.add(nearBy);
			}
		}
		return nearbyAttractions;
	}
	
	private void addShutDownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
		      public void run() {
		        trackerService.stopTracking();
		      }
		    });
	}
}