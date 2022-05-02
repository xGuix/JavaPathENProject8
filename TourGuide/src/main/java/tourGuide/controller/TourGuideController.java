package tourGuide.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import gpsUtil.location.Location;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import gpsUtil.location.VisitedLocation;
import tourGuide.dto.NearbyAttractionsDto;
import tourGuide.service.TourGuideService;
import tourGuide.dto.UserDto;
import tourGuide.dto.UserRewardDto;
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
	TourGuideService tourGuideService;

    /**
     * Get All user:
     * Call to get All user with username
     *
     * @return userList List of all users
     */
    @RequestMapping("/allUsers")
    private List<UserDto> getAllUsers() {
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
    private UserDto getUser(String userName) {
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

    /**
     * Get the closest attractions with distance from user :
     * Call to get attractions  with username
     *
     * @param userName String user name
     * @return userAttractionList List of the closest attraction
     */
    @RequestMapping("/getNearbyAttractions")
    public List<NearbyAttractionsDto> getNearbyAttractions(@RequestParam String userName) {
    	VisitedLocation visitedLocation = tourGuideService.getUserLocation(getUser(userName));
        List<NearbyAttractionsDto> userAttractionList = tourGuideService.getNearByAttractions(visitedLocation);
        logger.info("Get nearby attractions with username: {}", userName);
    	return userAttractionList;
    }

    /**
     * Get user rewards:
     * Call to get user rewards with username
     *
     * @param userName String userName
     * @return providers List of providers
     */
    @RequestMapping("/getRewards") 
    public List<UserRewardDto> getRewards(@RequestParam String userName) {
        List<UserRewardDto> userRewardDtoList = tourGuideService.getUserRewards(getUser(userName));
        logger.info("Get user reward with username: {}", userName);
    	return userRewardDtoList;
    }

    /**
     * Get all current locations:
     * Call to get all current locations with username
     *
     * @return allUsers List of all users locations
     */
    @RequestMapping("/getAllCurrentLocations")
    public Map<UUID, Location> getAllCurrentLocations() {
        Map<UUID, Location> userLocationMap = new HashMap<>();
        List<UserDto> usersList = tourGuideService.getAllUsers();
        for (UserDto users: usersList){
            VisitedLocation userVisitedLocation = getLocation(users.getUserName());
            userLocationMap.put(userVisitedLocation.userId, userVisitedLocation.location);
        }
        logger.info("Get all users current Location");
    	return userLocationMap;
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
        logger.info("Get user trip deal with username: {}", userName);
    	return providers;
    }
}