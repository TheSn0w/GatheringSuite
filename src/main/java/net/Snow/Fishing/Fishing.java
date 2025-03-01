package net.Snow.Fishing;

import net.Snow.AbstractScript;
import net.Snow.Banking.BankLocations;
import net.Snow.ImGui.SnowsTemplateGraphics;
import net.Snow.Snow;
import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.rs3.events.impl.ChatMessageEvent;
import net.botwithus.rs3.events.impl.InventoryUpdateEvent;
import net.botwithus.rs3.game.Area;
import net.botwithus.rs3.game.Client;
import net.botwithus.rs3.game.Coordinate;
import net.botwithus.rs3.game.queries.builders.characters.NpcQuery;
import net.botwithus.rs3.game.scene.entities.characters.npc.Npc;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.game.scene.entities.object.SceneObject;
import net.botwithus.rs3.game.skills.Skills;
import net.botwithus.rs3.script.Execution;
import net.botwithus.rs3.util.Regex;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static net.Snow.Banking.DepositBox.getNearestReachableDepositBox;
import static net.Snow.Banking.DepositBox.handleDepositBox;
import static net.Snow.Banking.Porters.checkPorters;
import static net.Snow.Banking.Porters.usePorters;
import static net.Snow.Fishing.AutoChange.selectFishingSpotBasedOnLevel;
import static net.Snow.Fishing.Crystallise.*;
import static net.Snow.Fishing.DropFish.dropAllFish;
import static net.Snow.Fishing.FishingTypes.addCroesusFishingSpot;
import static net.Snow.ImGui.CustomLogger.*;
import static net.Snow.ImGui.Experience.*;
import static net.Snow.LevelLogout.CheckLevel.checkLevelForSkill;
import static net.Snow.Mining.MiningVariables.doAutomaticLevelling;
import static net.Snow.Navigation.MoveTo;
import static net.Snow.Snow.*;
import static net.Snow.Spells.Lightform.applyLightForm;
import static net.Snow.Spells.Lightform.useLightForm;
import static net.Snow.Woodcutting.Crystallise.useCrystallise;


public class Fishing implements AbstractScript {

    Snow script;

    LocalPlayer player = Client.getLocalPlayer();


    @Override
    public void initialize() {
        startingFishingXP.set(Skills.FISHING.getSkill().getExperience());
        startingFishingLevel.set(Skills.FISHING.getSkill().getLevel());
        startTracking();

    }

    public AtomicInteger startingFishingXP = new AtomicInteger(0);
    public AtomicInteger startingFishingLevel = new AtomicInteger(0);


    @Override
    public void draw(SnowsTemplateGraphics sgc) throws IOException {
        FishingGraphics.draw(sgc, script, startingFishingLevel, startingFishingXP);
    }

    @Override
    public void onInventoryUpdate(InventoryUpdateEvent event) {
        FishingUpdates.onInventoryUpdate(event, inventoryMap);
    }

    @Override
    public void onChatMessageEvent(ChatMessageEvent event) {
        FishingUpdates.onChatMessageEvent(event, inventoryMap);
    }


    private boolean crystalliseCast = false; // Class-level variable to persist state

    public void run(Snow script) {

        checkLevelForSkill(Skills.FISHING.getSkill());

        capturestuff();


        if (usePorters) checkPorters();

        if (useLightForm) applyLightForm(player);


        if (Backpack.isFull() && !Backpack.contains(Regex.getPatternForContainsString("Sign of the Porter"))) {
            handleFullBackpack();
        } else {
            // Handle fishing if Crystallise is disabled or not needed
            if (!useCrystallise) {
                handleFishing();
            } else {
                Npc crystalliseNpc = getCrystalliseNpc();

                // Attempt to cast Crystallise if it's allowed and hasn't been cast yet
                if (crystalliseNpc == null && !crystalliseCast) {
                    Npc fishNpc = findFishForCrystallise(player);
                    if (fishNpc != null) {
                        crystalliseCast = applyCrystallise(fishNpc, player);
                    }
                }

                // If Crystallise wasn't cast or Crystallise is active but the player is not animating, handle fishing
                if (!crystalliseCast || (crystalliseNpc != null && player.getAnimationId() == -1)) {
                    handleFishing();
                }

                // Reset crystalliseCast if no Crystallise animation is active
                if (getCrystalliseNpc() == null) {
                    crystalliseCast = false;
                }
            }
        }
        // Periodically check for fishing spot availability
        Execution.delayUntil(1000, () -> {
            int fishingExperience = Skills.FISHING.getSkill().getExperience();
            int fishingLevel = getLevelFromXP(fishingExperience);
            addCroesusFishingSpot(fishingLevel);
            return false; // Continue checking
        }); // Check every 1 seconds

    }


    private void handleFullBackpack() {
        if (BankLocations.selectedBankIndex == 0) {
            logBlue("Banking is disabled. Dropping all Fish.");
            dropAllFish();
        } else {
            SceneObject nearestBox = getNearestReachableDepositBox();
            if (nearestBox != null) {
                if (Backpack.contains("Fishing bait") || Backpack.contains("Feather")) {
                    handleDepositBox(nearestBox);
                } else {
                    nearestBox.interact("Deposit-All");
                    Execution.delayUntil(random.nextLong(30000), () -> !Backpack.isFull());
                }
            } else {
                BankLocations.goToBankToUseOrBank();
            }
        }
    }

    public void handleFishing() {
        logGrey("Starting handleFishing method.");

        try {
            FishingSpot selectedFishingSpot = getSelectedFishingSpot();
            if (selectedFishingSpot == null) return;

            Set<Npc> matchingNpcs = findFishingSpots(selectedFishingSpot);
            if (matchingNpcs.isEmpty()) {
                handleNoFishingSpotsFound(selectedFishingSpot);
                return;
            }

            if (doAutomaticLevelling && selectFishingSpotBasedOnLevel()) {
                logGrey("Fishing spot changed. Recalling handleFishing to continue with new spot.");
                return;
            }

            if (shouldExitEarly(selectedFishingSpot)) return;

            interactWithFishingSpot(matchingNpcs, selectedFishingSpot);
        } catch (Exception e) {
            logWarning("An error occurred during the handleFishing process: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Retrieves the selected fishing spot, logging a warning if it's invalid.
     */
    private FishingSpot getSelectedFishingSpot() {
        FishingSpot spot = FishingTypes.fishingSpots.get(FishingTypes.selectedFishIndex);
        if (spot == null) {
            logWarning("Selected fishing spot is invalid. Please check your configuration.");
        }
        return spot;
    }

    /**
     * Finds matching NPCs for the fishing spot based on level and fishing spot type.
     */

    private Set<Npc> findFishingSpots(FishingSpot selectedFishingSpot) {
        Set<Npc> matchingNpcs = new HashSet<>();
        Set<Coordinate> loggedCoordinates = new HashSet<>();
        StringBuilder npcLog = new StringBuilder("Matching NPCs found = ");
        boolean primaryFound = false;

        int fishingExperience = Skills.FISHING.getSkill().getExperience();
        int fishingLevel = getLevelFromXP(fishingExperience);

        List<Integer> primaryTypeIDs = selectedFishingSpot.getTypeIDs();
        List<Integer> secondaryTypeIDs = List.of(28426, 28429, 28432, 28435);

        if (isCroesusSpot(selectedFishingSpot)) {
            if (fishingLevel >= 92) {
                primaryFound = searchForFishingSpots(primaryTypeIDs, selectedFishingSpot, matchingNpcs, loggedCoordinates, npcLog);
            }
            if (!primaryFound) {
                logOrange("No primary Type IDs found. Attempting Croesus fishing with fallback Type IDs: " + secondaryTypeIDs);
                searchForFishingSpots(secondaryTypeIDs, selectedFishingSpot, matchingNpcs, loggedCoordinates, npcLog);
            }
        } else {
            logPink("Fishing level is " + fishingLevel + ", targeting Type IDs: " + primaryTypeIDs + " Player animation ID: " + player.getAnimationId());
            searchForFishingSpots(primaryTypeIDs, selectedFishingSpot, matchingNpcs, loggedCoordinates, npcLog);
        }

        if (!matchingNpcs.isEmpty()) {
            logGrey(npcLog.toString());
        }

        return matchingNpcs;
    }

    /**
     * Helper method to search for fishing spots by type ID.
     */
    private boolean searchForFishingSpots(List<Integer> typeIDs, FishingSpot selectedFishingSpot,
                                          Set<Npc> matchingNpcs, Set<Coordinate> loggedCoordinates, StringBuilder npcLog) {
        boolean found = false;
        for (int typeID : typeIDs) {
            List<Npc> npcs = NpcQuery.newQuery()
                    .name(selectedFishingSpot.getSpotNames().toArray(new String[0]))
                    .option(selectedFishingSpot.getFishingMethod())
                    .byType(typeID)
                    .results()
                    .stream().toList();

            for (Npc npc : npcs) {
                if (matchingNpcs.add(npc) && loggedCoordinates.add(npc.getCoordinate())) {
                    npcLog.append(npc.getName()).append(" (Type ID: ").append(typeID)
                            .append(") at ").append(npc.getCoordinate()).append(", ");
                    found = true;
                }
            }
        }
        return found;
    }

    /**
     * Checks if the given fishing spot is a Croesus spot.
     */
    private boolean isCroesusSpot(FishingSpot spot) {
        return spot.getSpotNames().contains("Colonised Varrock guard") &&
                spot.getSpotNames().contains("Colonised Lumbridge guard");
    }

    /**
     * Handles the scenario when no fishing spots are found.
     */
    private void handleNoFishingSpotsFound(FishingSpot selectedFishingSpot) {
        if ("Disabled".equals(selectedFishingSpot.getSpotNames().get(0))) {
            logWarning("Fishing is disabled. Please select a fishing spot.");
            return;
        }

        logBlue("Moving to fishing spot coordinates: " + selectedFishingSpot.getLocation());
        MoveTo(selectedFishingSpot.getLocation());
        selectedFishingSpot.executeAdditionalSteps();
    }

    /**
     * Determines if the method should exit early due to animation or movement.
     */
    private boolean shouldExitEarly(FishingSpot selectedFishingSpot) {
        if (selectedFishingSpot.requiresAnimationCheck()) {
            if (player.getAnimationId() != -1 || player.isMoving() || isAnimating()) {
                logGrey("Exiting handleFishing early due to animation or movement.");
                return true;
            }
        } else {
            if (player.getAnimationId() != -1 || player.isMoving()) {
                logGrey("Exiting handleFishing early due to animation or movement.");
                return true;
            }
        }
        return false;
    }

    /**
     * Interacts with the nearest available fishing NPC.
     */
    private void interactWithFishingSpot(Set<Npc> matchingNpcs, FishingSpot selectedFishingSpot) {
        Npc nearestFishingNpc = matchingNpcs.stream()
                .min(Comparator.comparingDouble(npc -> npc.getCoordinate().distanceTo(player.getCoordinate())))
                .orElse(null);

        if (nearestFishingNpc != null && isNpcReachable(nearestFishingNpc)) {
            nearestFishingNpc.interact(selectedFishingSpot.getFishingMethod());
            logSuccess("Interacted with fishing spot: " + nearestFishingNpc.getName() + " at: " +
                    nearestFishingNpc.getCoordinate());
            Execution.delayUntil(random.nextLong(4500, 6000), () -> player.getAnimationId() != -1);
        } else {
            handleNoReachableFishingSpot(selectedFishingSpot, matchingNpcs);
        }
    }

    /**
     * Handles cases where no reachable fishing spot is found.
     */
    private void handleNoReachableFishingSpot(FishingSpot selectedFishingSpot, Set<Npc> matchingNpcs) {
        if (player.getCoordinate().equals(selectedFishingSpot.getLocation())) {
            logCyan("No available fishing spots found.");
            return;
        }

        boolean isNearby = matchingNpcs.stream()
                .anyMatch(npc -> selectedFishingSpot.getTypeIDs().contains(npc.getId()));

        if (isNearby) {
            logCyan("Fishing spot type ID is nearby, no need to move.");
        } else {
            logWarning("No reachable fishing spot found within range. Moving to fishing spot coordinates: " +
                    selectedFishingSpot.getLocationName());
            MoveTo(selectedFishingSpot.getLocation());
            selectedFishingSpot.executeAdditionalSteps();
        }
    }




    private boolean isNpcReachable(Npc npc) {
        Coordinate playerCoordinate = player.getCoordinate();
        if (npc.getCoordinate().distanceTo(playerCoordinate) <= 25) {
            logGrey("Fishing spot within 25 tiles found at: " + npc.getCoordinate());
            Set<Coordinate> surroundingTiles = getSurroundingTiles(npc);
            for (Coordinate tile : surroundingTiles) {
                if (tile.isReachable()) {
                    logGrey("Reachable tile found at: " + tile);
                    return true;
                }
            }
        }
        return false;
    }

    public static Set<Coordinate> getSurroundingTiles(Npc npc) {
        logGrey("Calculating surrounding tiles for NPC: " + npc.getName());
        Area area;
        if (npc == null || (area = npc.getArea()) == null) {
            logGrey("NPC or area is null, returning empty set.");
            return Collections.emptySet();
        }

        Set<Coordinate> result = new HashSet<>();
        for (Coordinate tile : area.getCoordinates()) {
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    result.add(tile.derive(i, j, 0));
                }
            }
        }
        logGrey("Surrounding tiles calculated: " + result.size() + " tiles.");
        return result;
    }


}
