package com.openclassrooms.tourguide.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import rewardCentral.RewardCentral;
import com.openclassrooms.tourguide.user.User;
import com.openclassrooms.tourguide.user.UserReward;

@Service
public class RewardsService {
    private static final double STATUTE_MILES_PER_NAUTICAL_MILE = 1.15077945;

	// proximity in miles
    private int defaultProximityBuffer = 10;
	private int proximityBuffer = defaultProximityBuffer;
	private int attractionProximityRange = 200;
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
	
	public void calculateRewardsV1(User user) {
		CopyOnWriteArrayList<VisitedLocation> userLocations = user.getVisitedLocations();
		CopyOnWriteArrayList<UserReward> userRewards = new CopyOnWriteArrayList<>();

		for(VisitedLocation visitedLocation : userLocations) {
			for(Attraction attraction : gpsUtil.getAttractions()) {
				if(user.isRewardExist(attraction) && nearAttraction(visitedLocation, attraction)) {
					userRewards.add(new UserReward(visitedLocation, attraction, getRewardPoints(attraction, user)));
				}
			}
		}
		user.addUserRewards(userRewards);
	}

	public void calculateRewards(User user) {
		CompletableFuture<Void> allFutures = CompletableFuture.runAsync(() -> {
			CopyOnWriteArrayList<VisitedLocation> userLocations = user.getVisitedLocations();
			CopyOnWriteArrayList<UserReward> userRewards = new CopyOnWriteArrayList<>();

			for (VisitedLocation visitedLocation : userLocations) {
				List<Attraction> attractions = gpsUtil.getAttractions();

				List<CompletableFuture<UserReward>> rewardFutures = attractions.stream()
						.filter(attraction -> user.isRewardExist(attraction) && nearAttraction(visitedLocation, attraction))
						.map(attraction -> CompletableFuture.supplyAsync(() ->
								new UserReward(visitedLocation, attraction, getRewardPoints(attraction, user))
						))
						.toList();

				List<UserReward> rewards = rewardFutures.stream()
						.map(CompletableFuture::join)
						.toList();

				userRewards.addAll(rewards);
			}
			user.addUserRewards(userRewards);
		});
		allFutures.join();
	}
	
	public boolean isWithinAttractionProximity(Attraction attraction, Location location) {
		return (getDistance(attraction, location) > attractionProximityRange);
	}

	public boolean nearAttraction(VisitedLocation visitedLocation, Attraction attraction) {
		return getDistance(attraction, visitedLocation.location) <= proximityBuffer;
	}
	
	public int getRewardPoints(Attraction attraction, User user) {
		return rewardsCentral.getAttractionRewardPoints(attraction.attractionId, user.getUserId());
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
