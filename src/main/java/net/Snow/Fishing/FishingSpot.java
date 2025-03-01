package net.Snow.Fishing;

import net.botwithus.rs3.game.Coordinate;

import java.util.List;

public class FishingSpot {
    private List<String> spotNames;
    private String fishingMethod;
    private Coordinate location;
    private String locationName;
    private int requiredLevel;
    private String fishType;
    private List<Integer> typeIDs;
    private List<Runnable> additionalSteps;
    private boolean requiresAnimationCheck;

    public FishingSpot(List<String> spotNames, String fishingMethod, Coordinate location, String locationName, int requiredLevel, String fishType, List<Integer> typeIDs, List<Runnable> additionalSteps, boolean requiresAnimationCheck) {
        this.spotNames = spotNames;
        this.fishingMethod = fishingMethod;
        this.location = location;
        this.locationName = locationName;
        this.requiredLevel = requiredLevel;
        this.fishType = fishType;
        this.typeIDs = typeIDs;
        this.additionalSteps = additionalSteps;
        this.requiresAnimationCheck = requiresAnimationCheck;
    }

    public List<String> getSpotNames() {
        return spotNames;
    }

    public String getFishingMethod() {
        return fishingMethod;
    }

    public Coordinate getLocation() {
        return location;
    }

    public String getLocationName() {
        return locationName;
    }

    public int getRequiredLevel() {
        return requiredLevel;
    }

    public String getFishType() {
        return fishType;
    }

    public List<Integer> getTypeIDs() {
        return typeIDs;
    }

    public List<Runnable> getAdditionalSteps() {
        return additionalSteps;
    }

    public boolean requiresAnimationCheck() {
        return requiresAnimationCheck;
    }

    public void executeAdditionalSteps() {
        for (Runnable step : additionalSteps) {
            step.run();
        }
    }

    public String getSpotName() {
        return spotNames.isEmpty() ? null : spotNames.get(0);
    }

    
}