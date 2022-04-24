package tourGuide.controller;

import java.util.List;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jsoniter.output.JsonStream;

import gpsUtil.location.VisitedLocation;
import rewardCentral.RewardCentral;
import tourGuide.service.RewardsService;
import tourGuide.service.TourGuideService;
import tourGuide.user.User;
import tourGuide.user.UserReward;
import tripPricer.Provider;

/**
 *  Tour Guide rest controller
 */
@RestController
public class TourGuideController {

    private static final Logger logger = LogManager.getLogger("TourGuideControllerLog");

    /**
     *  Load TourGuideService
     */
	@Autowired
	TourGuideService tourGuideService = new TourGuideService(new GpsUtil(), new RewardsService(new GpsUtil(),new RewardCentral()));

    /**
     * Get All user:
     * Call to get All user with username
     *
     * @return userList List of all users
     */
    @RequestMapping("/allUsers")
    private List<User> getAllUsers() {
        logger.info("Search list of all users");
        return tourGuideService.getAllUsers();
    }

    /**
     * Get user:
     * Call to get user with username
     *
     * @param userName String userName
     * @return userName User userName
     */
    @RequestMapping("/getUser")
    private User getUser(String userName) {
        logger.info("Search user with username: {}", userName);
        return tourGuideService.getUser(userName);
    }

    /**
     *  Get Index Controller
     *
     * @return String Greetings from TourGuide!
     */
    @RequestMapping("/")
    public String index() {
        logger.info("Get index");
        return "Greetings from TourGuide!";
    }

    /**
     *  Get user location
     *  Call get location with username
     *
     * @param userName String user name
     * @return visitedLocation The visited location
     */
    @RequestMapping("/getLocation") 
    public VisitedLocation getLocation(@RequestParam String userName) {
    	VisitedLocation visitedLocation = tourGuideService.getUserLocation(getUser(userName));
        logger.info("Get visited locations with username: {}", userName);
		return visitedLocation;
    }
    
    //  TODO: Change this method to no longer return a List of Attractions.
 	//  Instead: Get the closest five tourist attractions to the user - no matter how far away they are.
 	//  Return a new JSON object that contains:
    	// Name of Tourist attraction, 
        // Tourist attractions lat/long, 
        // The user's location lat/long, 
        // The distance in miles between the user's location and each of the attractions.
        // The reward points for visiting each Attraction.
        //    Note: Attraction reward points can be gathered from RewardsCentral
    @RequestMapping("/getNearbyAttractions") 
    public List<Attraction> getNearbyAttractions(@RequestParam String userName) {
    	VisitedLocation visitedLocation = this.getLocation(userName);
        logger.info("Get nearby attractions with username: {}", userName);
    	return tourGuideService.getNearByAttractions(visitedLocation);
    }

    /**
     * Get user rewards:
     * Call to get user rewards with username
     *
     * @param userName String userName
     * @return providers List of providers
     */
    @RequestMapping("/getRewards") 
    public List<UserReward> getRewards(@RequestParam String userName) {
        List<UserReward> userRewardList = tourGuideService.getUserRewards(getUser(userName));
    	return userRewardList;
    }

    /**
     * Get all current lacations:
     * Call to get all current locations with username
     *
     * @param userName String userName
     * @return ?
     */
    @RequestMapping("/getAllCurrentLocations")
    public String getAllCurrentLocations(@RequestParam String userName) {
    	// TODO: Get a list of every user's most recent location as JSON
    	//- Note: does not use gpsUtil to query for their current location, 
    	//        but rather gathers the user's current location from their stored location history.
    	//
    	// Return object should be the just a JSON mapping of userId to Locations similar to:
    	//     {
    	//        "019b04a9-067a-4c76-8817-ee75088c3822": {"longitude":-48.188821,"latitude":74.84371} 
    	//        ...
    	//     }

    	return JsonStream.serialize(getUser(userName).getLatestLocationTimestamp());
    }

    /**
     * Get List of providers:
     * Call to get trip deals with username
     *
     * @param userName String userName
     * @return providers List of providers
     */
    @RequestMapping("/getTripDeals")
    public List<Provider> getTripDeals(@RequestParam String userName) {
    	List<Provider> providers = tourGuideService.getTripDeals(getUser(userName));
    	return providers;
    }
}