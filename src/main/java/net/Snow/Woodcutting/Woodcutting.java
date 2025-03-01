package net.Snow.Woodcutting;

import net.Snow.AbstractScript;
import net.Snow.Banking.BankLocations;
import net.Snow.ImGui.SnowsTemplateGraphics;
import net.Snow.Navigation;
import net.Snow.Snow;
import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.rs3.events.impl.ChatMessageEvent;
import net.botwithus.rs3.events.impl.InventoryUpdateEvent;
import net.botwithus.rs3.game.*;
import net.botwithus.rs3.game.hud.interfaces.Interfaces;
import net.botwithus.rs3.game.js5.types.vars.VarDomainType;
import net.botwithus.rs3.game.queries.builders.animations.SpotAnimationQuery;
import net.botwithus.rs3.game.queries.builders.items.InventoryItemQuery;
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery;
import net.botwithus.rs3.game.queries.results.EntityResultSet;
import net.botwithus.rs3.game.queries.results.ResultSet;
import net.botwithus.rs3.game.quest.Quest;
import net.botwithus.rs3.game.scene.entities.animation.SpotAnimation;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.game.scene.entities.object.SceneObject;
import net.botwithus.rs3.game.skills.Skills;
import net.botwithus.rs3.game.vars.VarManager;
import net.botwithus.rs3.script.Execution;
import net.botwithus.rs3.util.Regex;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static net.Snow.Banking.BankLocations.goToBankToUseOrBank;
import static net.Snow.Banking.DepositBox.getNearestReachableDepositBox;
import static net.Snow.Banking.DepositBox.handleDepositBox;
import static net.Snow.Banking.Porters.checkPorters;
import static net.Snow.Banking.Porters.usePorters;
import static net.Snow.ImGui.CustomLogger.*;
import static net.Snow.ImGui.Experience.inventoryMap;
import static net.Snow.ImGui.Experience.startTracking;
import static net.Snow.Mining.Familiar.useFamiliar;
import static net.Snow.Mining.MiningVariables.doAutomaticLevelling;
import static net.Snow.Mining.MiningVariables.useJujuPotion;
import static net.Snow.Navigation.MoveTo;
import static net.Snow.Snow.*;
import static net.Snow.Spells.Lightform.applyLightForm;
import static net.Snow.Woodcutting.BirdsNests.birdsNest;
import static net.Snow.Woodcutting.Crystallise.*;
import static net.Snow.Woodcutting.DropLogs.dropAllLogs;
import static net.Snow.Woodcutting.Firemaking.doFiremaking;
import static net.Snow.Woodcutting.Firemaking.firemake;
import static net.Snow.Woodcutting.Fletching.doFletching;
import static net.Snow.Woodcutting.Fletching.fletch;
import static net.Snow.Woodcutting.IncenseSticks.burnIncenseSticks;
import static net.Snow.Woodcutting.IncenseSticks.doIncenseSticks;
import static net.Snow.Woodcutting.TreeTypes.executeAdditionalSteps;
import static net.Snow.Woodcutting.Woodbox.getAdditionalWoodboxCapacity;
import static net.Snow.Woodcutting.Woodbox.getBaseWoodboxCapacity;
import static net.Snow.Woodcutting.WoodcuttingVariables.*;

public class Woodcutting implements AbstractScript {

    Snow script;

    @Override
    public void initialize() {
        startingWoodcuttingXP.set(Skills.WOODCUTTING.getSkill().getExperience());
        startingWoodcuttingLevel.set(Skills.WOODCUTTING.getSkill().getActualLevel());
        startingFiremakingXP.set(Skills.FIREMAKING.getSkill().getExperience());
        startingFiremakingLevel.set(Skills.FIREMAKING.getSkill().getActualLevel());
        startTracking();
    }

    public AtomicInteger startingWoodcuttingXP = new AtomicInteger(0);
    public AtomicInteger startingWoodcuttingLevel = new AtomicInteger(0);
    public AtomicInteger startingFiremakingXP = new AtomicInteger(0);
    public AtomicInteger startingFiremakingLevel = new AtomicInteger(0);

    @Override
    public void draw(SnowsTemplateGraphics sgc) throws IOException {
        WoodcuttingGraphics.draw(sgc, script, startingWoodcuttingLevel, startingWoodcuttingXP, startingFiremakingLevel, startingFiremakingXP);
    }

    @Override
    public void onInventoryUpdate(InventoryUpdateEvent event) {
        WoodcuttingUpdates.onInventoryUpdate(event, inventoryMap);
    }

    @Override
    public void onChatMessageEvent(ChatMessageEvent event) {
        WoodcuttingUpdates.onChatMessageEvent(event, inventoryMap);
    }

    @Override
    public void run(Snow script) {

        SpotAnimation nearestSpotAnimation = SpotAnimationQuery.newQuery().ids(8447).results().nearest();
        if (nearestSpotAnimation != null && !LocalPlayer.LOCAL_PLAYER.getCoordinate().equals(nearestSpotAnimation.getCoordinate())) {
            logOrange("Moving to nearest spot animation: " + nearestSpotAnimation.getCoordinate());
            MoveTo(nearestSpotAnimation.getCoordinate());
            return;
        }

        capturestuff();


        LocalPlayer player = Client.getLocalPlayer();

        if (Interfaces.isOpen(1251)) {
            return;
        }

        if (useCrystallise) {
            SceneObject tree = findTreeForCrystallise(player);
            if (tree != null) {
                applyCrystallise(tree, player);
            }
        }

        applyLightForm(player);
        birdsNest();

        if (useJujuPotion) {
            int jujuPotionTimeRemaining = VarManager.getVarValue(VarDomainType.PLAYER, 4908);
            int jujuPotionTimeRemainingInMinutes = (jujuPotionTimeRemaining * 15) / 60;
            logBlue("Perfect Juju Mining Potion Time Remaining: " + jujuPotionTimeRemainingInMinutes + " minutes");
            if (!InventoryItemQuery.newQuery(93).name(PERFECT_JUJU_WOODCUTTING_POTION_PATTERN).results().isEmpty() && VarManager.getVarValue(VarDomainType.PLAYER, 4908) <= 1) {
                Item jujuPotion = InventoryItemQuery.newQuery(93).name(PERFECT_JUJU_WOODCUTTING_POTION_PATTERN).results().first();
                if (jujuPotion != null) {
                    logSuccess("Drinking: " + jujuPotion.getName());
                    Backpack.interact(jujuPotion.getName(), "Drink");
                    Execution.delay(random.nextLong(1000, 2000));
                }
                return;
            }
        }

        if (useFamiliar) {
            logBlue("Familiar time remaining: " + VarManager.getVarbitValue(6055) + " minutes");
            if (VarManager.getVarbitValue(6055) <= 1) {
                Familiar.summonFamiliar();
            }
        }

        if (usePorters) {
            checkPorters();
        }

        if (Backpack.isFull() && !Backpack.contains(Regex.getPatternForContainsString("Sign of the Porter"))) {
            if (Backpack.containsItemByCategory(22)) {
                if (doFiremaking) {
                    firemake();
                } else if (doIncenseSticks) {
                    burnIncenseSticks();
                } else if (doFletching) {
                    fletch();
                } else {
                    handleFullBackpack();
                }
            } else {
                handleFullBackpack();
            }
        } else {
            handleWoodcutting();
        }
    }

    public void listAllLogsInWoodbox() {
        // Query the woodbox inventory for all items
        ResultSet<Item> woodboxItems = InventoryItemQuery.newQuery(937).results();

        // Use a HashMap to store log names and their counts
        Map<String, Integer> logCounts = new HashMap<>();

        // Iterate through the items in the woodbox
        for (Item item : woodboxItems) {
            if (item.getName() != null && item.getName().toLowerCase().contains("log")) {
                // If the item is a log, add its count to the HashMap
                logCounts.put(item.getName(), item.getStackSize());
            }
        }

        // Display the results
        if (logCounts.isEmpty()) {
            logBlue("No logs stored in the woodbox.");
        } else {
            logBlue("Logs currently stored in the woodbox:");
            for (Map.Entry<String, Integer> entry : logCounts.entrySet()) {
                logBlue("- " + entry.getKey() + ": " + entry.getValue());
            }
        }
    }

    private void handleFullBackpack() {
        Item woodbox = InventoryItemQuery.newQuery(93).name(woodboxPattern).results().first();
        if (woodbox == null || woodbox.getId() == -1) {
            logWarning("We did not find a woodbox");
            if (BankLocations.selectedBankIndex == 0) {
                logBlue("Banking is disabled. Dropping all logs.");
                dropAllLogs();
            } else {
                SceneObject nearestBox = getNearestReachableDepositBox();
                if (nearestBox != null) {
                    nearestBox.interact("Deposit-All");
                    logBlue("Interacted with Deposit Box: " + nearestBox.getName());
                    Execution.delayUntil(random.nextLong(30000), () -> !Backpack.isFull());
                } else {
                    goToBankToUseOrBank();
                }
            }
        } else {
            logSuccess("We found a woodbox: " + woodbox.getName());
            int capacity = getBaseWoodboxCapacity(woodbox.getName()) + getAdditionalWoodboxCapacity();
            logBlue("Our expected capacity is: " + capacity);

            // Query the main inventory for logs
            Item logs = InventoryItemQuery.newQuery(93).name(logPattern).results().first();

            if (logs == null || logs.getId() == -1 || logs.getName() == null) {
                logWarning("No logs found in inventory.");
                return;
            }

            // Query the woodbox inventory for all items
            ResultSet<Item> woodboxItems = InventoryItemQuery.newQuery(937).results();
            int totalLogsStored = 0;

            // Display all types of logs and their amounts in the woodbox
            logBlue("Logs currently stored in the woodbox:");
            for (Item item : woodboxItems) {
                if (item.getName() != null && item.getName().toLowerCase().contains("log")) {
                    logBlue("- " + item.getName() + ": " + item.getStackSize());
                    totalLogsStored += item.getStackSize();
                }
            }

            logBlue("Total logs stored: " + totalLogsStored + " / " + capacity);

            // If logs stored in the woodbox are less than its capacity
            if (totalLogsStored < capacity) {
                ResultSet<Item> woodboxInventoryItems = InventoryItemQuery.newQuery(93)
                        .name(woodboxPattern)
                        .option("Fill")
                        .results();

                if (woodboxInventoryItems != null && !woodboxInventoryItems.isEmpty()) {
                    Item woodboxItem = woodboxInventoryItems.first();
                    boolean interactionSuccess = Backpack.interact(woodboxItem.getName(), "Fill");

                    // Wait for the interaction to complete and check if the backpack is not full
                    boolean filledSuccessfully = interactionSuccess && Execution.delayUntil(
                            random.nextLong(3000, 5000),
                            () -> !Backpack.isFull()
                    );

                    if (filledSuccessfully) {
                        logSuccess("Successfully filled woodbox: " + woodboxItem.getName());

                        // Re-query the woodbox inventory to get the updated amount of logs
                        woodboxItems = InventoryItemQuery.newQuery(937).results();
                        totalLogsStored = 0;

                        logBlue("Logs after filling stored in the woodbox:");
                        for (Item item : woodboxItems) {
                            if (item.getName() != null && item.getName().toLowerCase().contains("log")) {
                                logBlue("- " + item.getName() + ": " + item.getStackSize());
                                totalLogsStored += item.getStackSize();
                            }
                        }

                        logBlue("Total logs stored after filling: " + totalLogsStored + " / " + capacity);
                    } else {
                        logError("Failed to fill woodbox: " + woodboxItem.getName());
                    }
                } else {
                    logError("Woodbox item not found in inventory with the pattern: " + woodboxPattern);
                }
            }
            // If logs stored in the woodbox are equal to or more than its capacity
            else {
                logBlue("Woodbox is full or at capacity: " + totalLogsStored + " / " + capacity);
                if (BankLocations.selectedBankIndex == 0) {
                    logBlue("Banking is disabled. Dropping all logs.");
                    dropAllLogs();
                } else {
                    // Check if the backpack contains an item matching the woodbox pattern
                    if (Backpack.contains(woodboxPattern)) {
                        logBlue("Woodbox is in the backpack. Going straight to the bank.");
                        goToBankToUseOrBank();
                    } else {
                        SceneObject nearestBox = getNearestReachableDepositBox();
                        if (nearestBox != null) {
                            nearestBox.interact("Deposit");
                            handleDepositBox(nearestBox);
                        } else {
                            goToBankToUseOrBank();
                        }
                    }
                }
            }
        }
    }

            public static final Area MENAPHOSVIP = new Area.Rectangular(
            new Coordinate(3179, 2740, 0), // Bottom-left coordinate of the area
            new Coordinate(3192, 2757, 0)  // Top-right coordinate of the area
    );


    private void handleWoodcutting() {
        LocalPlayer player = Client.getLocalPlayer();
        int playerWoodcuttingLevel = Skills.WOODCUTTING.getSkill().getActualLevel();

        // Automatic leveling logic
        if (doAutomaticLevelling) {
            if (playerWoodcuttingLevel >= 1 && playerWoodcuttingLevel < 10) {
                TreeTypes.setSelectedTreeByNameAndLocation("Tree", "Varrock");
            } else if (playerWoodcuttingLevel >= 10 && playerWoodcuttingLevel < 20) {
                TreeTypes.setSelectedTreeByNameAndLocation("Oak", "Varrock");
            } else if (playerWoodcuttingLevel >= 20 && playerWoodcuttingLevel < 50) {
                TreeTypes.setSelectedTreeByNameAndLocation("Willow", "Draynor Village");
            } else if (playerWoodcuttingLevel >= 50) {
                Optional<Quest> quest = Quest.byId(390);
                if (quest.isPresent() && quest.get().isComplete()) {
                    if (VarManager.getVarValue(VarDomainType.PLAYER, 7002) > 1290000) {
                        logGrey("Player has completed the quest: " + quest.get().name() + " and has enough reputation to enter VIP area.");
                        TreeTypes.setSelectedTreeByNameAndLocation("Acadia tree", "Menophos (VIP)");
                    } else {
                        logGrey("Player has completed the quest: " + quest.get().name() + " but does not have enough reputation to enter VIP area.");
                        TreeTypes.setSelectedTreeByNameAndLocation("Acadia tree", "Menophos (Not VIP)");
                    }
                } else {
                    logGrey("Quest: " + quest.get().name() + " is not complete.");
                    if (playerWoodcuttingLevel >= 68) {
                        TreeTypes.setSelectedTreeByNameAndLocation("Ivy", "Grand Exchange");
                    } else {
                        TreeTypes.setSelectedTreeByNameAndLocation("Maple Tree", "Seers' Village");
                    }
                }
            }
        }

        // Check if the player is currently performing an animation or moving
        if (player.getAnimationId() != -1 || player.isMoving()) {
            return;
        }

        Navigation selectedTree = TreeTypes.treeTypes.get(TreeTypes.selectedTreeIndex);
        if (selectedTree.getName().equals("Disabled")) {
            logError("No tree type selected.");
            return;
        }

        if (selectedTree.getName().equals("Ivy") && isAnimating()) {
            logGrey("Player is currently animating. Waiting for the animation to finish.");
            return;
        }

        // Determine the interaction option based on the selected tree
        String interactionOption = selectedTree.getName().equals("Ivy") ? "Chop" : "Chop down";
        if (selectedTree.getName().equals("Acadia tree")) {
            interactionOption = "Cut down";
        }

        // Check if the player is in the MENAPHOSVIP area if it is a VIP tree
        if (selectedTree.getLocationName().equals("Menophos (VIP)") && !MENAPHOSVIP.contains(player)) {
            logGrey("Player is not in the Menophos VIP area. Moving to the area.");
            MoveTo(selectedTree.getCoordinate());
            executeAdditionalSteps();
            return;
        }

        EntityResultSet<SceneObject> nearestTree = SceneObjectQuery.newQuery().name(selectedTree.getName()).option(interactionOption).hidden(false).results();

        if (nearestTree.isEmpty()) {
            logError("No valid trees found nearby.");
            MoveTo(selectedTree.getCoordinate());
            executeAdditionalSteps();
            return;
        }

        if (selectedTree.getName() == null) {
            logError("No tree type selected.");
            return;
        }

        // Initialize allowedCoordinates as final
        final Set<Coordinate> allowedCoordinates;
        if (selectedTree.getName().equals("Acadia tree")) {
            allowedCoordinates = getAllowedCoordinatesForMenophos(selectedTree);
        } else {
            allowedCoordinates = Set.of(); // Use an empty set for non-Acadia trees
        }


        SceneObject treeToChop = null;

        if (selectedTree.getName().equals("Acadia tree")) {
            // Filter trees to ensure they are within the allowed coordinates for Acadia trees
            treeToChop = nearestTree.stream()
                    .filter(tree -> allowedCoordinates.contains(tree.getCoordinate()))
                    .min(Comparator.comparingDouble(tree -> Distance.between(player.getCoordinate(), tree.getCoordinate())))
                    .orElse(null);

            if (treeToChop == null) {
                logError("No valid Acadia trees found within the allowed coordinates.");
                MoveTo(selectedTree.getCoordinate());
                return;
            }
        } else {
            // For other tree types, just find the nearest tree without coordinate restrictions
            treeToChop = nearestTree.nearestTo(player);
            if (treeToChop == null) {
                logError("No valid trees found nearby.");
                return;
            }
        }

        // Interact with the nearest valid tree within the allowed coordinates
        boolean success = treeToChop.interact(interactionOption);
        if (success) {
            logSuccess("Chopping down: " + treeToChop.getName());
            Execution.delayUntil(random.nextLong(4500, 6000), () -> player.getAnimationId() != -1);
        } else {
            logError("Failed to interact with the tree.");
        }
    }

    // Method to get allowed coordinates based on the selected tree's location
    private Set<Coordinate> getAllowedCoordinatesForMenophos(Navigation selectedTree) {
        Set<Coordinate> allowedCoordinates = new HashSet<>();

        if (selectedTree.getLocationName().equals("Menophos (VIP)")) {
            allowedCoordinates.addAll(List.of(
                    new Coordinate(3180, 2747, 0),
                    new Coordinate(3180, 2753, 0),
                    new Coordinate(3192, 2747, 0),
                    new Coordinate(3192, 2753, 0)
            ));
        } else if (selectedTree.getLocationName().equals("Menophos (Not VIP)")) {
            allowedCoordinates.addAll(List.of(
                    new Coordinate(3194, 2736, 0),
                    new Coordinate(3179, 2736, 0),
                    new Coordinate(3183, 2722, 0),
                    new Coordinate(3189, 2722, 0),
                    new Coordinate(3195, 2716, 0),
                    new Coordinate(3183, 2716, 0),
                    new Coordinate(3180, 2704, 0),
                    new Coordinate(3196, 2708, 0)
            ));
        }

        return allowedCoordinates;
    }



}