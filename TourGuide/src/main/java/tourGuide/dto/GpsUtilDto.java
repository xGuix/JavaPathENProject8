package tourGuide.dto;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;

import java.util.Objects;

public class GpsUtilDto {

    GpsUtil gpsUtil;
    VisitedLocation visitedLocation;
    Attraction attraction;
    Location location;

    public GpsUtilDto() {
    }

    public GpsUtilDto(VisitedLocation visitedLocation, Attraction attraction, Location location) {
        this.gpsUtil = new GpsUtil();
        this.visitedLocation = visitedLocation;
        this.attraction = attraction;
        this.location = location;
    }

    public GpsUtil getGpsUtil() {
        return gpsUtil;
    }

    public void setGpsUtil(GpsUtil gpsUtil) {
        this.gpsUtil = gpsUtil;
    }

    public VisitedLocation getVisitedLocation() {
        return visitedLocation;
    }

    public void setVisitedLocation(VisitedLocation visitedLocation) {
        this.visitedLocation = visitedLocation;;
    }

    public Attraction getAttraction() {
        return attraction;
    }

    public void setAttraction(Attraction attraction) {
        this.attraction = attraction;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GpsUtilDto that = (GpsUtilDto) o;
        return Objects.equals(gpsUtil, that.gpsUtil) && Objects.equals(visitedLocation,
                that.visitedLocation) && Objects.equals(attraction,
                that.attraction) && Objects.equals(location, that.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gpsUtil, visitedLocation, attraction, location);
    }
}