package net.Snow.Archaeology.Collections;

import net.Snow.Banking.Banking;
import net.botwithus.api.game.hud.inventories.Bank;
import net.botwithus.rs3.game.Item;
import net.botwithus.rs3.game.movement.Movement;
import net.botwithus.rs3.game.movement.NavPath;
import net.botwithus.rs3.game.movement.TraverseEvent;
import net.botwithus.rs3.game.queries.builders.characters.NpcQuery;
import net.botwithus.rs3.game.queries.builders.items.InventoryItemQuery;
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery;
import net.botwithus.rs3.game.queries.results.EntityResultSet;
import net.botwithus.rs3.game.queries.results.ResultSet;
import net.botwithus.rs3.game.scene.entities.characters.npc.Npc;
import net.botwithus.rs3.game.scene.entities.object.SceneObject;
import net.botwithus.rs3.script.Execution;

import java.util.*;
import java.util.stream.Collectors;

import static net.Snow.Archaeology.Collections.CollectionTypes.selectedCollectionIndex;
import static net.Snow.Archaeology.MaterialTypes.collectionQueue;
import static net.Snow.Banking.ArchaeologyBank.handleFamiliarWithdrawals;
import static net.Snow.Banking.ArchaeologyBank.withdrawSoilBox;
import static net.Snow.Banking.BankLocations.*;
import static net.Snow.Banking.Porters.quickPorterWithdraw;
import static net.Snow.Banking.Porters.usePorters;
import static net.Snow.ImGui.CustomLogger.*;
import static net.Snow.Mining.Familiar.useFamiliar;
import static net.Snow.Snow.component;
import static net.Snow.Snow.random;

public class CheckArtifacts {

    public static List<Artifact> missingArtifacts = new ArrayList<>();
    public static Map<String, Long> artifactCounts = new HashMap<>();
    public static int requiredCount = 1; // Start with 0 of each item


    public static boolean checkArtifacts() {
        // Iterate through the collectionQueue to set the selectedCollectionIndex
        for (String collectionName : collectionQueue) {
            selectedCollectionIndex = CollectionTypes.collections.stream()
                    .map(Collection::getName)
                    .toList()
                    .indexOf(collectionName);
            logBlue("Collection: " + collectionName + " - Selected for artifact farming.");
            break;
        }

        if (collectionQueue.isEmpty()) {
            logWarning("No collections in the queue.");
            return false;
        }

        goToBankToUseOrBank();

        boolean foundMissingArtifacts = checkArtifactsInBank();

        if (missingArtifacts.isEmpty()) {
            logGrey("missingArtifacts is empty.");
            setMissingArtifact();
        }

        return foundMissingArtifacts;
    }

//    private static boolean navigateToBank() {
//        logBlue("Attempting to traverse to the bank.");
//        if (Movement.traverse(NavPath.resolve(new Coordinate(3362, 3397, 0))) == TraverseEvent.State.FINISHED) {
//            logBlue("Arrived at the bank.");
//            EntityResultSet<SceneObject> results = SceneObjectQuery.newQuery().id(115427).option("Use").results();
//            SceneObject nearestBank = results.nearest();
//            if (nearestBank != null && nearestBank.interact("Use")) {
//                logGrey("Interacting with the bank.");
//                Execution.delayUntil(15000, Bank::isOpen);
//                return true;
//            } else {
//                logError("Failed to interact with the bank.");
//            }
//        }
//        return false;
//    }

    private static void goToBankToUseOrBank() {
        Banking selectedBanking = bankings.get(selectedBankIndex);
        logBlue("Navigating to " + selectedBanking.getName() + " bank.");
        TraverseEvent.State state = Movement.traverse(NavPath.resolve(selectedBanking.getCoordinate()));
        logBlue("Traversal state: " + state);

        if (state == TraverseEvent.State.FINISHED) {
            boolean success = interactWithBank(selectedBanking, "Use") || interactWithBank(selectedBanking, "Bank");
            if (success) {
                logGrey("Successfully interacted with the bank.");
            }
        } else {
            logError("Failed to traverse to " + selectedBanking.getName() + " bank.");
        }
    }


    static boolean interactWithBank(Banking banking, String interactionType) {
        // Check the type of the banking entity and call the appropriate interaction method
        if (banking.getType() == Banking.Type.SCENE_OBJECT) {
            return interactWithSceneObjectBank(banking, interactionType);
        } else if (banking.getType() == Banking.Type.NPC) {
            return interactWithNpcBank(banking, interactionType);
        }
        return false;
    }

    private static boolean interactWithSceneObjectBank(Banking banking, String interactionType) {
        // Query for scene objects with the specified banking ID
        EntityResultSet<SceneObject> results = SceneObjectQuery.newQuery().id(banking.getId()).results();
        if (!results.isEmpty()) {
            SceneObject sceneObject = results.nearest();
            if (sceneObject != null) {
                // Iterate through the interaction options and perform the specified interaction
                for (String option : banking.getInteractionOptions()) {
                    if (!option.equals(interactionType)) {
                        continue;
                    }
                    if (sceneObject.interact(option)) {
                        logBlue("Interacted with: " + sceneObject.getName() + " using option: " + option);
                        if (option.equals("Load Last Preset from")) {
                            return Execution.delayUntil(5000, () -> !net.botwithus.rs3.game.inventories.Backpack.isFull());
                        } else if (option.equals("Use") || option.equals("Bank")) {
                            waitForBank();
                            return true;
                        }
                    }
                }
            }
        } else {
            logError("No suitable bank found.");
        }
        return false;
    }

    private static boolean interactWithNpcBank(Banking banking, String interactionType) {
        // Query for NPCs with the specified banking ID
        EntityResultSet<Npc> results = NpcQuery.newQuery().id(banking.getId()).results();
        if (!results.isEmpty()) {
            Npc npc = results.nearest();
            if (npc != null) {
                // Iterate through the interaction options and perform the specified interaction
                for (String option : banking.getInteractionOptions()) {
                    if (!option.equals(interactionType)) {
                        continue;
                    }
                    if (npc.interact(option)) {
                        logBlue("Interacted with: " + npc.getName() + " using option: " + option);
                        if (option.equals("Load Last Preset from")) {
                            return Execution.delayUntil(5000, () -> !net.botwithus.rs3.game.inventories.Backpack.isFull());
                        } else if (option.equals("Use") || option.equals("Bank")) {
                            waitForBank();
                            return true;
                        }
                    }
                }
            }
        } else {
            logError("No suitable bank found.");
        }
        return false;
    }


    private static void waitForBank() {
        // Wait until the bank is open
        Execution.delayUntil(5000, Bank::isOpen);
        Execution.delay(random.nextLong(1250, 1500));
    }

    private static boolean checkArtifactsInBank() {
        if (Bank.isOpen()) {

            checkBankTabs();

            logWarning("Bank is open. Depositing items.");
            Execution.delay(random.nextLong(500, 1000));
            Bank.depositAllExcept(49538, 50096, 4614, 49976, 50431, 49947, 49949, 50753, 41092, 49429, 50161, 15707, 49948);
            Execution.delay(random.nextLong(500, 1000));

            withdrawSoilBox();

            Item soilBox = InventoryItemQuery.newQuery(93).name("Archaeological soil box").results().first();
            if (soilBox != null) {
                logGrey("Soil box found in the Backpack. Emptying it.");
                if (soilBox.getSlot() >= 0) {
                    component(9, soilBox.getSlot(), 33882127);
                    logSuccess("Emptied: " + soilBox.getName());
                    Execution.delay(random.nextLong(500, 1000));
                }
            }

            if (usePorters) {
                quickPorterWithdraw();
                Execution.delay(random.nextLong(500, 1000));
            }

            if (useFamiliar) {
                handleFamiliarWithdrawals();
                Execution.delay(random.nextLong(500, 1000));
            }

            String collectionName = CollectionTypes.collections.get(selectedCollectionIndex).getName();
            boolean collectionSelected = collectionName != null;
            logGrey("Collection selected: " + collectionSelected);

            if (collectionSelected) {
                logWarning("[Check] Rescanning the bank for artifacts.");

                // Query for artifacts (both damaged and undamaged - categories 4608 and 4609)
                ResultSet<Item> damagedResults = InventoryItemQuery.newQuery().category(4608).results();
                ResultSet<Item> nonDamagedResults = InventoryItemQuery.newQuery().category(4609).results();

                artifactCounts.clear();

                // Process damaged artifacts
                damagedResults.stream()
                        .filter(item -> ArtifactRequirements.getAllArtifactRequirements().stream()
                                .anyMatch(artifact -> artifact.getCollectionNames().contains(collectionName) &&
                                        artifact.getDamagedName().equals(item.getName())))
                        .forEach(item -> {
                            Artifact artifact = ArtifactRequirements.getAllArtifactRequirements().stream()
                                    .filter(a -> a.getDamagedName().equals(item.getName()))
                                    .findFirst()
                                    .orElse(null);
                            if (artifact != null) {
                                artifactCounts.merge(artifact.getId(), 1L, Long::sum); // Count only 1 of any version
                            }
                        });

                // Process non-damaged artifacts
                nonDamagedResults.stream()
                        .filter(item -> ArtifactRequirements.getAllArtifactRequirements().stream()
                                .anyMatch(artifact -> artifact.getCollectionNames().contains(collectionName) &&
                                        artifact.getName().equals(item.getName())))
                        .forEach(item -> {
                            Artifact artifact = ArtifactRequirements.getAllArtifactRequirements().stream()
                                    .filter(a -> a.getName().equals(item.getName()))
                                    .findFirst()
                                    .orElse(null);
                            if (artifact != null) {
                                artifactCounts.merge(artifact.getId(), 1L, Long::sum); // Count only 1 of any version
                            }
                        });

                long count = artifactCounts.values().stream().mapToLong(Long::longValue).sum();
                logGrey("Total artifact count: " + count);

                if (count > 0) {
                    logSuccess("Required artifacts found in the bank: " + artifactCounts);
                } else {
                    logBlue("No required artifacts found in the bank.");
                }

                missingArtifacts = ArtifactRequirements.getAllArtifactRequirements().stream()
                        .filter(artifact -> artifact.getCollectionNames().contains(collectionName))
                        .filter(artifact -> artifactCounts.getOrDefault(artifact.getId(), 0L) < requiredCount)
                        .collect(Collectors.toList());

                if (missingArtifacts.isEmpty()) {
                    setMissingArtifact();
                }

                if (!missingArtifacts.isEmpty()) {
                    logBlue("Missing artifacts in the selected collection: " + missingArtifacts.stream()
                            .map(Artifact::getDamagedName)
                            .collect(Collectors.joining(", ")));

                    return true;
                } else {
                    logBlue("[Check] All required artifacts are collected.");
                }
            } else {
                logError("[Check] No collection selected.");
            }

            logWarning("[Check] Closing the bank.");
            Bank.close();
        } else {
            logError("[Check] Bank is not open.");
        }
        return false;
    }






    public static final Map<String, Integer> damagedArtifactCounts = new HashMap<>();
    public static final Map<String, Integer> undamagedArtifactCounts = new HashMap<>();


    public static void setMissingArtifact() {
        logBlue("[Check] Prioritizing collections to have at least one of each artifact.");
        missingArtifacts.clear(); // Clear the list before updating

        for (String collectionName : collectionQueue) {
            logGrey("Collection name: " + collectionName);
            List<Artifact> artifacts = ArtifactRequirements.getAllArtifactRequirements().stream()
                    .filter(artifact -> artifact.getCollectionNames().contains(collectionName))
                    .toList();

            for (Artifact artifact : artifacts) {
                // Query for both damaged and undamaged variants
                Item damagedItem = InventoryItemQuery.newQuery()
                        .name(artifact.getDamagedName())
                        .results()
                        .first();
                Item undamagedItem = InventoryItemQuery.newQuery()
                        .name(artifact.getName())
                        .results()
                        .first();

                // Calculate counts for damaged and undamaged variants
                int damagedCount = damagedItem != null ? damagedItem.getStackSize() : 0;
                int undamagedCount = undamagedItem != null ? undamagedItem.getStackSize() : 0;
                int totalCount = damagedCount + undamagedCount;

                // Log separate lines for damaged and undamaged counts
                logOrange("Artifact: " + artifact.getDamagedName() + " (Damaged) - Count: " + damagedCount);
                logOrange("Artifact: " + artifact.getName() + " (Non-Damaged) - Count: " + undamagedCount);

                // Update artifactCounts with the total count for this artifact ID
                artifactCounts.put(artifact.getId(), (long) totalCount);

                // Store the damaged and undamaged counts in the public maps
                damagedArtifactCounts.put(artifact.getId(), damagedCount);
                undamagedArtifactCounts.put(artifact.getId(), undamagedCount);

                // Update missing artifacts if the required count is not met
                if (totalCount < requiredCount) {
                    logOrange("Missing artifact set to: " + artifact.getDamagedName() + " with total count: " + totalCount);
                    missingArtifacts.add(artifact);
                }
            }
        }

        // Additional logic for finding the artifact with the lowest stack size (unchanged)
        if (missingArtifacts.isEmpty()) {
            logSuccess("All collections have at least one of each artifact. Determining the artifact with the lowest stack size.");
            Artifact lowestSizeArtifact = collectionQueue.stream()
                    .flatMap(collectionName -> ArtifactRequirements.getAllArtifactRequirements().stream()
                            .filter(artifact -> artifact.getCollectionNames().contains(collectionName)))
                    .min(Comparator.comparingInt(artifact -> {
                        long totalCount = artifactCounts.getOrDefault(artifact.getId(), 0L);
                        return (int) totalCount;
                    }))
                    .orElse(null);

            if (lowestSizeArtifact != null) {
                long totalCount = artifactCounts.getOrDefault(lowestSizeArtifact.getId(), 0L);
                logOrange("Missing artifact set to the one with the lowest stack size: " + lowestSizeArtifact.getDamagedName() + " with total count: " + totalCount);
                missingArtifacts.add(lowestSizeArtifact);
            } else {
                logWarning("No artifact found with the lowest stack size.");
            }
        }
    }
}
