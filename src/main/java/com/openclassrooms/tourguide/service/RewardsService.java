package com.openclassrooms.tourguide.service;

import com.openclassrooms.tourguide.user.User;
import com.openclassrooms.tourguide.user.UserReward;
import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import org.springframework.stereotype.Service;
import rewardCentral.RewardCentral;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
public class RewardsService {
    private static final double STATUTE_MILES_PER_NAUTICAL_MILE = 1.15077945;

	// proximity in miles
    private int defaultProximityBuffer = 10;
	private int proximityBuffer = defaultProximityBuffer;
	private int attractionProximityRange = 200;
	private final GpsUtil gpsUtil;
	private final RewardCentral rewardsCentral;
	private ExecutorService executorService = Executors.newFixedThreadPool(1000);


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

	/**
	 * Calculates  rewards to a user based on their visited locations and attractions
	 *
	 * @param user - user
	 */
	public void calculateRewards(User user) {
		List<Attraction> attractions = gpsUtil.getAttractions();
		List<VisitedLocation> visitedLocationList = user.getVisitedLocations().stream().toList();
		for (VisitedLocation visitedLocation : visitedLocationList) {
			for (Attraction attraction : attractions) {
				if (user.getUserRewards().stream().filter(r -> r.attraction.attractionName.equals(attraction.attractionName))
						.count() == 0) {
					calculateDistanceReward(user, visitedLocation, attraction);
				}
			}
		}
	}

	public void calculateDistanceReward(User user, VisitedLocation visitedLocation, Attraction attraction) {
		Double distance = getDistance(attraction, visitedLocation.location);
		if (distance <= proximityBuffer) {
			UserReward userReward = new UserReward(visitedLocation, attraction, distance.intValue());
			submitRewardPoints(userReward, attraction, user);
		}
	}

	/**
	 * assign rewards to a user based on their visited locations and attractions
	 *
	 * @param user - user
	 * @param userReward - UserReward
	 * @param attraction -  attractions
	 */
	private void submitRewardPoints(UserReward userReward, Attraction attraction, User user) {
		CompletableFuture.supplyAsync(() -> rewardsCentral.getAttractionRewardPoints(attraction.attractionId, user.getUserId()), executorService)
				.thenAccept(points -> {
					userReward.setRewardPoints(points);
					user.addUserReward(userReward);
				});
	}
	/**
	 * Check if a provided Location is within range of an Attraction
	 *
	 * @param attraction attraction to be checked
	 * @param location   location to be compared to
	 * @return true if location is within range, otherwise false
	 */
	public boolean isWithinAttractionProximity(Attraction attraction, Location location) {
		return (getDistance(attraction, location) > attractionProximityRange);
	}
	/**
	 * Check if a given attraction is near a visited location
	 *
	 * @param visitedLocation - visite
	 * @param attraction - attraction
	 * @return True if the attraction is near the visited location
	 */
	public boolean nearAttraction(VisitedLocation visitedLocation, Attraction attraction) {
		return getDistance(attraction, visitedLocation.location) <= proximityBuffer;
	}

	public void calculateRewardsV1(User user) {
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
	//Get reward point value of an attraction for a provided UUID
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
