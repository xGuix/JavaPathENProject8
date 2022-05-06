//package tourGuide.service;
//
//import org.apache.commons.lang3.time.StopWatch;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import tourGuide.dto.UserDto;
//
//import java.util.List;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.TimeUnit;
//
//public class NewTrackerServTest {
//
//    private Logger logger = LoggerFactory.getLogger(NewTrackerServTest.class);
//    /**
//     * Defines the interval in minutes between to calls for users tracking.
//     */
//    private static final long TRACKING_POLLING_INTERVAL = TimeUnit.MINUTES
//            .toSeconds(5);
//    /**
//     * Create an instance of a single thread ExecutorService.
//     */
//    private final ExecutorService executorService = Executors
//            .newSingleThreadExecutor();
//    /**
//     * TourGuideService instance declaration.
//     */
//    private TourGuideService tourGuideService;
//    /**
//     * Declares and initializes to false a boolean, that indicates if tracker is
//     * stopped (true) or run (false).
//     */
//    private boolean isTrackerStopped = false;
//
//    /**
//     * Class constructor.
//     *
//     * @param pTourGuideService
//     */
//    public NewTrackerServTest(TourGuideService pTourGuideService) {
//        this.tourGuideService = pTourGuideService;
//        executorService.submit(this);
//    }
//
//    /**
//     * Assures to shut down the Tracker thread.
//     */
//    public void stopTracking() {
//        isTrackerStopped = true;
//        executorService.shutdownNow();
//    }
//
//    /**
//     * Overridden Run super method.
//     */
//    public void run() {
//        StopWatch stopWatch = new StopWatch();
//        while (true) {
//            if (Thread.currentThread().isInterrupted() || isTrackerStopped) {
//                logger.debug("Tracker stopping");
//                break;
//            }
//
//            List<UserDto> users = tourGuideService.getAllUsers();
//            logger.debug("Begin Tracker. Tracking " + users.size() + " users.");
//            stopWatch.start();
//            users.forEach(u -> tourGuideService.trackUserLocation(u));
//            stopWatch.stop();
//            logger.debug("Tracker Time Elapsed: "
//                    + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime())
//                    + " seconds.");
//            stopWatch.reset();
//            try {
//                logger.debug("Tracker sleeping");
//                TimeUnit.SECONDS.sleep(TRACKING_POLLING_INTERVAL);
//            } catch (InterruptedException e) {
//                break;
//            }
//        }
//    }
//}
