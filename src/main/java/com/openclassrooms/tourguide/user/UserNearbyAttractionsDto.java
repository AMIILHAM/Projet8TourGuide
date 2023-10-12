package com.openclassrooms.tourguide.user;

public class UserNearbyAttractionsDto {
    private String attractionName;
    private double attractionLocationLongitude;
    private double attractionLocationLatitude;
    private double userLocationLongitude;
    private double userLocationLatitude;
    private double distance;
    private int rewardPoints;

    public UserNearbyAttractionsDto(String attractionName, double attractionLocationLongitude, double attractionLocationLatitude, double userLocationLongitude, double userLocationLatitude, double distance, int rewardPoints) {
        this.attractionName = attractionName;
        this.attractionLocationLongitude = attractionLocationLongitude;
        this.attractionLocationLatitude = attractionLocationLatitude;
        this.userLocationLongitude = userLocationLongitude;
        this.userLocationLatitude = userLocationLatitude;
        this.distance = distance;
        this.rewardPoints = rewardPoints;
    }

    public String getAttractionName() {
        return attractionName;
    }

    public void setAttractionName(String attractionName) {
        this.attractionName = attractionName;
    }

    public double getAttractionLocationLongitude() {
        return attractionLocationLongitude;
    }

    public void setAttractionLocationLongitude(double attractionLocationLongitude) {
        this.attractionLocationLongitude = attractionLocationLongitude;
    }

    public double getAttractionLocationLatitude() {
        return attractionLocationLatitude;
    }

    public void setAttractionLocationLatitude(double attractionLocationLatitude) {
        this.attractionLocationLatitude = attractionLocationLatitude;
    }

    public double getUserLocationLongitude() {
        return userLocationLongitude;
    }

    public void setUserLocationLongitude(double userLocationLongitude) {
        this.userLocationLongitude = userLocationLongitude;
    }

    public double getUserLocationLatitude() {
        return userLocationLatitude;
    }

    public void setUserLocationLatitude(double userLocationLatitude) {
        this.userLocationLatitude = userLocationLatitude;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public int getRewardPoints() {
        return rewardPoints;
    }

    public void setRewardPoints(int rewardPoints) {
        this.rewardPoints = rewardPoints;
    }
}
