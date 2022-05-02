package tourGuide.dto;

import gpsUtil.location.Attraction;
import gpsUtil.location.Location;

import java.util.Objects;

public class NearbyAttractionsDto {
    private Attraction attraction;
    private Location userLocation;
    private Double distance;
    private Integer rewardPoints;

    public NearbyAttractionsDto() {
    }

    public NearbyAttractionsDto(Attraction attraction, Location userLocation, Double distance, Integer rewardPoints) {
        this.attraction = attraction;
        this.userLocation = userLocation;
        this.distance = distance;
        this.rewardPoints = rewardPoints;
    }

    public Attraction getAttraction() {
        return attraction;
    }

    public void setAttraction(Attraction attraction) {
        this.attraction = attraction;
    }

    public Location getUserLocation() {
        return userLocation;
    }

    public void setUserLocation(Location userLocation) {
        this.userLocation = userLocation;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public Integer getRewardPoints() {
        return rewardPoints;
    }

    public void setRewardPoints(Integer rewardPoints) {
        this.rewardPoints = rewardPoints;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NearbyAttractionsDto that = (NearbyAttractionsDto) o;
        return Objects.equals(attraction, that.attraction) &&
                Objects.equals(userLocation, that.userLocation) &&
                Objects.equals(distance, that.distance) &&
                Objects.equals(rewardPoints, that.rewardPoints);
    }

    @Override
    public int hashCode() {
        return Objects.hash(attraction, userLocation, distance, rewardPoints);
    }

    @Override
    public String toString() {
        return "NearbyAttractions{" +
                "attraction=" + attraction +
                ", userLocation=" + userLocation +
                ", distance=" + distance +
                ", rewardPoints=" + rewardPoints +
                '}';
    }
}