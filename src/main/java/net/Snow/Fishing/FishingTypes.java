package net.Snow.Fishing;

import net.botwithus.rs3.game.Client;
import net.botwithus.rs3.game.Coordinate;
import net.botwithus.rs3.game.queries.builders.characters.NpcQuery;
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery;
import net.botwithus.rs3.game.queries.results.EntityResultSet;
import net.botwithus.rs3.game.scene.entities.characters.npc.Npc;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.game.scene.entities.object.SceneObject;
import net.botwithus.rs3.game.skills.Skills;
import net.botwithus.rs3.script.Execution;

import java.util.ArrayList;
import java.util.List;

import static net.Snow.ImGui.CustomLogger.*;
import static net.Snow.ImGui.Experience.getLevelFromXP;
import static net.Snow.Navigation.MoveTo;
import static net.Snow.Snow.random;

// Update FishingTypes class
public class FishingTypes {
    public static int selectedFishIndex = 0;
    public static List<FishingSpot> fishingSpots = new ArrayList<>();

    static {
        fishingSpots.add(new FishingSpot(List.of("Disabled"), null, null, null, 0, null, List.of(-1), List.of(), false)); // 0
        fishingSpots.add(new FishingSpot(List.of("Fishing spot"), "Net", new Coordinate(3087, 3228, 0), "Draynor Village", 1, "Shrimp/Anchovies", List.of(327), List.of(), false)); // 1
        fishingSpots.add(new FishingSpot(List.of("Fishing spot"), "Net", new Coordinate(3244, 3156, 0), "Lumbridge", 1, "Shrimp/Anchovies", List.of(4908), List.of(), false)); // 2
        fishingSpots.add(new FishingSpot(List.of("Fishing spot"), "Bait", new Coordinate(3087, 3228, 0), "Draynor Village", 5, "Herring/Sardines", List.of(327), List.of(), false)); // 3
        fishingSpots.add(new FishingSpot(List.of("Fishing spot"), "Bait", new Coordinate(3244, 3156, 0), "Lumbridge", 5, "Herring/Sardines", List.of(4908), List.of(), false)); // 4
        fishingSpots.add(new FishingSpot(List.of("Fishing spot"), "Lure", new Coordinate(3106, 3433, 0), "Barbarian Village", 20, "Trout/Salmon", List.of(328), List.of(), false)); // 5
        fishingSpots.add(new FishingSpot(List.of("Fishing spot"), "Bait", new Coordinate(3106, 3433, 0), "Barbarian Village", 25, "Pike", List.of(328), List.of(), false)); // 6
        fishingSpots.add(new FishingSpot(List.of("Fishing spot"), "Cage", new Coordinate(2845, 3430, 0), "Catherby", 40, "Lobster", List.of(321), List.of(), true)); // 7
        fishingSpots.add(new FishingSpot(List.of("Fishing spot"), "Harpoon", new Coordinate(2845, 3430, 0), "Catherby", 76, "Shark/Raw Great Shark", List.of(322), List.of(), true)); // 8
        fishingSpots.add(new FishingSpot(List.of("Fishing spot"), "Harpoon", new Coordinate(2845, 3430, 0), "Catherby", 35, "Swordfish/Tuna", List.of(321), List.of(), true)); // 9
        fishingSpots.add(new FishingSpot(List.of("Fishing spot"), "Bait", new Coordinate(3212, 2626, 0), "Menaphos (Non Vip)", 52, "Desert sole/Catfish/Beltfish", List.of(24572), List.of(), false)); // 10
        fishingSpots.add(new FishingSpot(List.of("Fishing spot"), "Bait", new Coordinate(3186, 2739, 0), "Menaphos (Vip)", 52, "Desert sole/Catfish/Beltfish", List.of(24574), List.of(FishingTypes::performMenaphosVipSteps), false)); // 11
        fishingSpots.add(new FishingSpot(List.of("Fishing spot"), "Catch", new Coordinate(2287, 3406, 2), "Prif", 93, "Small/Medium Urchins", List.of(21779), List.of(), false)); // 12
        fishingSpots.add(new FishingSpot(List.of("Fishing spot"), "Catch", new Coordinate(2282, 3417, 2), "Prif", 97, "Large Urchins", List.of(21780), List.of(), false)); // 13
        fishingSpots.add(new FishingSpot(List.of("Ghostly sole spot"), "Bait", new Coordinate(1134, 1725, 1), "Um", 66, "Ghostly sole", List.of(30285), List.of(), false)); // 14
        fishingSpots.add(new FishingSpot(List.of("Colonised Varrock guard", "Colonised Lumbridge guard"), "Catch", new Coordinate(1880, 1233, 0), "Croesus", 88, "Fungal algae", List.of(28427, 28430, 28433, 28436), List.of(), false)); // 15

        int fishingExperience = Skills.FISHING.getSkill().getExperience();
        int fishingLevel = getLevelFromXP(fishingExperience);
        addCroesusFishingSpot(fishingLevel);
    }



    static void addCroesusFishingSpot(int fishingLevel) {
        List<Integer> primaryTypeIds, fallbackTypeIds;

        // Define the IDs for primary and fallback fishing spots
        if (fishingLevel < 92) {
            primaryTypeIds = List.of(28426, 28429, 28432, 28435);
            fallbackTypeIds = List.of(); // No fallback needed for <92
        } else {
            primaryTypeIds = List.of(28427, 28430, 28433, 28436);
            fallbackTypeIds = List.of(28426, 28429, 28432, 28435);
        }

        // Attempt to find and interact with the primary fishing spots
        boolean foundPrimary = findAndAddFishingSpot(primaryTypeIds);
        if (foundPrimary) {
//            logLime("Primary Type IDs found: " + primaryTypeIds);
        }

        // If primary spots are not found and we have a fallback, try the fallback spots
        if (!foundPrimary && !fallbackTypeIds.isEmpty()) {
//            logMagenta("Falling back to Type IDs: " + fallbackTypeIds);
            boolean foundFallback = findAndAddFishingSpot(fallbackTypeIds);

            // Log the result of the fallback search
            if (foundFallback) {
//                logPink("Fallback Type IDs found: " + fallbackTypeIds);
            } else {
                logMagenta("Neither primary nor fallback Type IDs found. Moving to default coordinates.");
            }
        }
    }


    /**
     * Attempts to add a fishing spot for the given type IDs.
     * Returns true if any of the spots are found, otherwise false.
     */
    private static boolean findAndAddFishingSpot(List<Integer> typeIds) {
        boolean found = false;

        // Simulate finding a spot
        for (Integer typeId : typeIds) {
            if (isFishingSpotAvailable(typeId)) {
                found = true;
                break;
            }
        }

        // If a spot is found, add it
        if (found) {
            fishingSpots.add(new FishingSpot(
                    List.of("Colonised Varrock guard", "Colonised Lumbridge guard"),
                    "Catch",
                    new Coordinate(1880, 1233, 0),
                    "Croesus",
                    88,
                    "Fungal algae",
                    typeIds,
                    List.of(),
                    false
            ));
        }

        return found;
    }

    /**
     * Function to check if a fishing spot is available.
     */
    private static boolean isFishingSpotAvailable(int typeId) {
        List<Npc> npcs = NpcQuery.newQuery()
                .byType(typeId)
                .results()
                .stream().toList();
        return !npcs.isEmpty();
    }


    public static void setSelectedFishingSpotByNameLocationAndType(String spotName, String locationName, int typeID) {
        for (int i = 0; i < fishingSpots.size(); i++) {
            FishingSpot spot = fishingSpots.get(i);
            if (spot.getSpotNames().contains(spotName) && spot.getLocationName().equals(locationName) && spot.getTypeIDs().contains(typeID)) {
                selectedFishIndex = i;
                return; // Exit the method once the fishing spot is found and selected
            }
        }
        logError("Fishing spot not found for name: " + spotName + ", location: " + locationName + ", and typeID: " + typeID);
    }

    public static FishingSpot getSelectedFishingSpot() {
        return fishingSpots.get(selectedFishIndex);
    }



    // Example additional step method for Menaphos VIP area
    public static void performMenaphosVipSteps() {
        LocalPlayer player = Client.getLocalPlayer();
        logGrey("Checking if additional steps for Menaphos VIP area are required.");

        // Ensure player is not already at the final destination
        if (!player.getCoordinate().equals(new Coordinate(3183, 2749, 0))) {
            logGrey("Performing extra steps for Menaphos VIP area.");

            EntityResultSet<SceneObject> archway = SceneObjectQuery.newQuery().id(109014).option("Go through").results();

            if (archway.isEmpty()) {
                logGrey("No archway found. Moving directly to the final destination.");
                MoveTo(new Coordinate(3183, 2749, 0));
            } else {
                logGrey("Archway found. Interacting with archway.");
                archway.first().interact("Go through");

                // Delay until the player has passed through the archway
                boolean passedThrough = Execution.delayUntil(
                        random.nextLong(2000, 3000),
                        () -> player.getCoordinate().equals(new Coordinate(3186, 2740, 0))
                );

                if (passedThrough) {
                    // Move to the final fishing spot coordinate
                    logGrey("Moving to the final fishing spot after passing through the archway.");
                    MoveTo(new Coordinate(3183, 2749, 0));
                } else {
                    logWarning("Failed to pass through the archway. Retrying...");
                    // You may want to handle retry logic here if needed
                }
            }
            logGrey("Arrived at Menaphos VIP Fishing spot.");
        } else {
            logGrey("Player already at the Menaphos VIP Fishing spot. Skipping extra steps.");
        }
    }
}
