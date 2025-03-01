package net.Snow.Archaeology;

import net.Snow.AbstractScript;
import net.Snow.Archaeology.ArtifactQueue.ArtifactQueue;
import net.Snow.Archaeology.Collections.Artifact;
import net.Snow.Archaeology.Collections.ArtifactRequirements;
import net.Snow.Banking.BankLocations;
import net.Snow.ImGui.SnowsTemplateGraphics;
import net.Snow.Navigation;
import net.Snow.Snow;
import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.rs3.events.impl.ChatMessageEvent;
import net.botwithus.rs3.events.impl.InventoryUpdateEvent;
import net.botwithus.rs3.game.Client;
import net.botwithus.rs3.game.Coordinate;
import net.botwithus.rs3.game.Item;
import net.botwithus.rs3.game.js5.types.vars.VarDomainType;
import net.botwithus.rs3.game.queries.builders.animations.SpotAnimationQuery;
import net.botwithus.rs3.game.queries.builders.items.InventoryItemQuery;
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery;
import net.botwithus.rs3.game.queries.results.EntityResultSet;
import net.botwithus.rs3.game.scene.entities.animation.SpotAnimation;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.game.scene.entities.object.SceneObject;
import net.botwithus.rs3.game.skills.Skills;
import net.botwithus.rs3.game.vars.VarManager;
import net.botwithus.rs3.script.Execution;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static net.Snow.Archaeology.Archaeology.ArchaeologyState.*;
import static net.Snow.Archaeology.ArchaeologyGraphics.*;
import static net.Snow.Archaeology.Buffs.useArchaeologyBuffs;
import static net.Snow.Archaeology.Collections.CheckArtifacts.*;
import static net.Snow.Archaeology.Collections.CollectionTypes.selectedCollectionIndex;
import static net.Snow.Archaeology.DropSoil.*;
import static net.Snow.Archaeology.Familiar.handleFamiliar;
import static net.Snow.Archaeology.ItemPatterns.MATERIAL_PATTERN;
import static net.Snow.Archaeology.MaterialCarts.getNearestReachableMaterialCart;
import static net.Snow.Archaeology.MaterialTypes.*;
import static net.Snow.Archaeology.SelectedQueue.handleTask;
import static net.Snow.Archaeology.SoilBox.handleSoilBox;
import static net.Snow.Banking.ArchaeologyBank.handleCompleteTome;
import static net.Snow.Banking.BankLocations.selectedBankIndex;
import static net.Snow.Banking.Porters.checkPorters;
import static net.Snow.Banking.Porters.usePorters;
import static net.Snow.ImGui.CustomLogger.*;
import static net.Snow.ImGui.Experience.inventoryMap;
import static net.Snow.ImGui.Experience.startTracking;
import static net.Snow.Mining.Familiar.useFamiliar;
import static net.Snow.Navigation.MoveTo;
import static net.Snow.Runecrafting.WorldHop.worldHopforArch;
import static net.Snow.Snow.capturestuff;
import static net.Snow.Snow.random;

public class Archaeology implements AbstractScript {
    Snow script;

    @Override
    public void initialize() {
        startingArchaeologyXP.set(Skills.ARCHAEOLOGY.getSkill().getExperience());
        startingArchaeologyLevel.set(Skills.ARCHAEOLOGY.getSkill().getLevel());
        startTracking();
    }

    public AtomicInteger startingArchaeologyXP = new AtomicInteger(0);
    public AtomicInteger startingArchaeologyLevel = new AtomicInteger(0);

    @Override
    public void draw(SnowsTemplateGraphics sgc) throws IOException {
        ArchaeologyGraphics.draw(sgc, script, startingArchaeologyLevel, startingArchaeologyXP);
    }

    @Override
    public void onInventoryUpdate(InventoryUpdateEvent event) {
        ArchaeologyUpdates.onInventoryUpdate(event, inventoryMap);
    }

    @Override
    public void onChatMessageEvent(ChatMessageEvent event) {
        ArchaeologyUpdates.onChatMessageEvent(event, inventoryMap);
    }

    public enum ArchaeologyState {
        BANKING,
        FULLBACKPACK,
        MOVETO,
        EXCAVATING,
        COLLECTIONS,
        MYSTERIES,
    }

    private static ArchaeologyState botstate = EXCAVATING;

    public static ArchaeologyState getBotState() {
        return botstate;
    }

    public static void setBotState(ArchaeologyState newState) {
        botstate = newState;
    }

    public void run(Snow script) {
        LocalPlayer player = Client.getLocalPlayer();
        Scanner.updateSceneObjectTableData(player);
        capturestuff();


        if (player.isMoving()) {
            return;
        }

        if (useMaterialBuffs) {
            useArchaeologyBuffs();
        }

        switch (botstate) {
            case FULLBACKPACK:
                handleFullBackpack();
                break;
            case BANKING:
                logWarning("Entering BANKING state.");
                if (BankLocations.selectedBankIndex != 0) {
                    if (Backpack.contains("Complete tome")) {
                        handleCompleteTome();
                    }
                    logWarning("Going to bank to use or bank.");
                    BankLocations.goToBankToUseOrBank();
                } else {
                    logError("Banking is not selected.");
                }
                break;
            case EXCAVATING:
                if (useQueue) {
                    List<ArtifactQueue.ArtifactTask> allTasks = ArtifactQueue.getAllArtifactTasks();

                    ArtifactQueue.ArtifactTask currentTask = allTasks.isEmpty() ? null : allTasks.get(0);
                    if (currentTask != null) {
                        handleTask(currentTask, allTasks);
                    } else {
                        log("No current task to process or the task is already completed.");
                    }
                }

                if (doCollections) {
                    // Update counts for all relevant artifacts
                    updateArtifactCounts();

                    // Determine the current artifact and hotspot
                    Artifact currentArtifact = !missingArtifacts.isEmpty() ? missingArtifacts.get(0) : null;
                    String currentHotspot = currentArtifact != null ? currentArtifact.getHotspot() : null;

                    boolean allArtifactsCollected = true;

                    // Iterate over the missing artifacts to update status
                    Iterator<Artifact> iterator = missingArtifacts.iterator();
                    while (iterator.hasNext()) {
                        Artifact artifact = iterator.next();
                        boolean found = Backpack.contains(artifact.getDamagedName());
                        logGrey("Searching for artifact: " + artifact.getDamagedName() + " - Found: " + found);

                        if (found) {
                            // Artifact obtained, remove it from the missing list
                            logSuccess("Obtained missing artifact: " + artifact.getDamagedName() + ". Removing from missing list.");
                            iterator.remove();
                        } else {
                            // Check if this artifact is at the current hotspot
                            if (artifact.getHotspot().equals(currentHotspot)) {
                                allArtifactsCollected = false; // There is still an artifact to collect at the current hotspot
                                break;
                            }
                        }
                    }

                    if (allArtifactsCollected) {
                        logSuccess("All artifacts at the current hotspot have been collected. Switching to COLLECTIONS state.");
                        setBotState(COLLECTIONS);
                        return;
                    }
                }
                if (Backpack.containsItemByCategory(4617)) {
                    interactWithPages();
                }
                if (usePorters) {
                    checkPorters();
                }
                if (useFamiliar) {
                    handleFamiliar(player);
                }
                if (Backpack.isFull()) {
                    log("Backpack is full. Setting bot state to FULLBACKPACK.");
                    setBotState(FULLBACKPACK);
                } else {
                    handleArchaeology();
                }
                break;
            case COLLECTIONS:
                if (selectedBankIndex == 0) {
                    logError("Banking is not selected, we have set your banking location to the Archaeological Guild, you can change this in the settings.");
                    BankLocations.setBank("Archaeological Guild");
                }
                handleCollections();
                break;
            default:
                break;
        }
    }

    private static void updateArtifactCounts() {
        // Get all artifacts relevant to the current collection
        List<Artifact> relevantArtifacts = ArtifactRequirements.getAllArtifactRequirements().stream()
                .filter(artifact -> missingArtifacts.stream()
                        .anyMatch(missingArtifact -> new HashSet<>(missingArtifact.getCollectionNames()).containsAll(artifact.getCollectionNames())))
                .toList();

        for (Artifact artifact : relevantArtifacts) {
            // Check both the damaged and undamaged versions
            Item damagedItem = InventoryItemQuery.newQuery().name(artifact.getDamagedName()).results().first();
            int damagedCount = damagedItem != null ? damagedItem.getStackSize() : 0;

            Item undamagedItem = InventoryItemQuery.newQuery().name(artifact.getName()).results().first();
            int undamagedCount = undamagedItem != null ? undamagedItem.getStackSize() : 0;

            // Calculate the total count currently in the backpack
            long countInBackpack = damagedCount + undamagedCount;

            // Fetch the existing count from the map (which includes counts from banking or previous updates)
            long existingCount = artifactCounts.getOrDefault(artifact.getId(), 0L);

            // Calculate the actual number of new artifacts found since the last update
            long newArtifactsFound = countInBackpack > existingCount ? countInBackpack - existingCount : 0;

            // Increment the total count by the number of new artifacts found
            long newCount = existingCount + newArtifactsFound;

            // Store the updated count back in the map
            artifactCounts.put(artifact.getId(), newCount);

        }
    }




    private static void passThroughAndSetBankingState() {
        EntityResultSet<SceneObject> results = SceneObjectQuery.newQuery().name("Forge doors").option("Pass through").results();
        if (!results.isEmpty()) {
            SceneObject passThrough = results.nearest();
            if (passThrough != null) {
                passThrough.interact("Pass through");
                Execution.delayUntil(30000, () -> {
                    Coordinate playerCoord = Client.getLocalPlayer().getCoordinate();
                    return playerCoord.getX() == 2298 && playerCoord.getY() == 7373 && playerCoord.getZ() == 0;
                });
                setBotState(BANKING);
            }
        }
    }

    public static void handleFullBackpack() {
        // Check if the backpack is full
        if (!Backpack.isFull()) {
            return;
        }

        // Check for soil items (category 4603) in the backpack
        if (Backpack.containsItemByCategory(4603)) {
            logWarning("Backpack contains Soil.");

            // If drop soil is enabled, drop all soil
            if (dropSoil) {
                logBlue("Drop soil is enabled, dropping all soil.");
                dropAllSoil();
                logBlue("Finished Dropping Soil.");
                setBotState(EXCAVATING);
                return;
            }

            // If drop soil is not active, handle the soil box
            if (Backpack.contains("Archaeological soil box")) {
                logWarning("Handling soil box.");
                if (handleSoilBox()) {
                    logSuccess("Soil box handled.");
                    setBotState(EXCAVATING);
                    return;
                } else {
                    logError("Soil box is full or could not be handled.");
                }
            } else {
                logError("Soil box not found in backpack.");
            }
        }

        // Check if the current excavation type is "Big High War God shrine"
        if (selectedExcavationIndex != 55) {
            log("selected excavation index: " + selectedExcavationIndex);
            // Check if the backpack contains at least 5 materials
            if (Backpack.getCount(MATERIAL_PATTERN) >= 5) {
                logWarning("Backpack contains at least 5 materials.");
                SceneObject nearestCart = getNearestReachableMaterialCart();

                if (nearestCart != null) {
                    log("Nearest material cart found. Depositing materials.");
                    logMaterialCounts();
                    nearestCart.interact("Deposit materials");
                    Execution.delayUntil(30000, () -> !Backpack.contains(MATERIAL_PATTERN));
                    logSuccess("Materials deposited.");
                    setBotState(EXCAVATING);
                    return;
                } else {
                    logWarning("No reachable material cart found.");
                }
            }
        } else {
            logBlue("Excavating at 'Big High War God shrine'. Skipping material cart deposit.");
        }

        // If no soil box was found or it is full, and no cart was found to deposit materials, set bot state to banking
        logWarning("No soil box found or full, and no cart available to deposit materials. Setting bot state to BANKING.");
        handleBankingState();
    }

    private static void handleBankingState() {
        if (selectedExcavationIndex == 49 || selectedExcavationIndex == 50 || selectedExcavationIndex == 52 || selectedExcavationIndex == 54) {
            passThroughAndSetBankingState();
        } else {
            setBotState(BANKING);
        }
    }

    private static void logMaterialCounts() {
        Backpack.getItems().stream()
                .filter(item -> item.getName().matches(MATERIAL_PATTERN.pattern()))
                .collect(Collectors.groupingBy(Item::getName, Collectors.counting()))
                .forEach((name, count) -> logBlue("Material: " + name + ", Count: " + count));
    }


    public static void handleCollections() {
        if (selectedCollectionIndex != 0) {
            logWarning("Selected collection index is not 0.");
            if (checkArtifacts()) {
                logWarning("Starting to excavate required artifact.");
                logBlue("Missing artifacts: " + missingArtifacts.stream().map(Artifact::getDamagedName).collect(Collectors.joining(", ")));
                moveToNextHotspotBasedOnMissingArtifacts(missingArtifacts);
            } else {
                logBlue("No missing artifacts found.");
            }
        } else {
            logBlue("Selected collection index is 0.");
        }
    }


    private static Coordinate lastInteractedSpotAnimationCoordinate = null;

    public static void handleArchaeology() {
        int sprite = VarManager.getVarValue(VarDomainType.PLAYER, 9307);
        if (sprite > 0) {
            logGrey("Sprite Focus Percent: " + sprite);
        }

        LocalPlayer player = Client.getLocalPlayer();
        Navigation selectedDigsite = ExcavationTypes.get(selectedExcavationIndex);
        String selectedDigsiteName = selectedDigsite.getName();
        boolean isMaterialCache = selectedDigsiteName.contains("Material cache");

        // Uncover nearby object if not a Material cache
        if (!isMaterialCache) {
            uncoverNearbyObject(player);
        }

        // Query and filter digsites
        List<SceneObject> digsiteList = queryNearbyDigsites(player, 25);

        SceneObject selectedDigsiteObject = findDigsiteByName(digsiteList, selectedDigsiteName);
        if (selectedDigsiteObject == null || player.getCoordinate().distanceTo(selectedDigsiteObject.getCoordinate()) > 25.0) {
            logBlue("Selected digsite is more than 10 tiles away or not found nearby. Traversing to: " + selectedDigsiteName);
            MoveTo(selectedDigsite.getCoordinate());
            selectedDigsite.executeAdditionalSteps();
            digsiteList = queryNearbyDigsites(player, 25); // Re-query digsites after movement
        }

        interactWithDigsitesAndAnimations(player, selectedDigsiteName, digsiteList, isMaterialCache);
    }

    private static final Set<SceneObject> blacklistedObjects = new HashSet<>();

    private static void uncoverNearbyObject(LocalPlayer player) {
        SceneObject uncoverObject = SceneObjectQuery.newQuery()
                .option("Uncover")
                .results()
                .nearestTo(player.getCoordinate());

        if (uncoverObject != null && !blacklistedObjects.contains(uncoverObject) && uncoverObject.getCoordinate().distanceTo(player.getCoordinate()) <= 3) {
            logBlue("Uncovering: " + uncoverObject.getName());
            uncoverObject.interact("Uncover");
            boolean success = Execution.delayUntil(10000, () -> !uncoverObject.getOptions().contains("Uncover"));
            if (success) {
                logError("Uncover action completed.");
            } else {
                logError("Failed to uncover: " + uncoverObject.getName() + ". Blacklisting it.");
                blacklistedObjects.add(uncoverObject);
            }
        }
    }

    private static List<SceneObject> queryNearbyDigsites(LocalPlayer player, int distance) {
        return SceneObjectQuery.newQuery()
                .option("Excavate", "Uncover")
                .results()
                .stream()
                .filter(digsite -> digsite.getCoordinate().distanceTo(player.getCoordinate()) <= distance)
                .collect(Collectors.toList());
    }

    private static SceneObject findDigsiteByName(List<SceneObject> digsites, String name) {
        return digsites.stream()
                .filter(digsite -> digsite.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    private static void interactWithDigsitesAndAnimations(LocalPlayer player, String selectedDigsiteName, List<SceneObject> digsites, boolean isMaterialCache) {
        // If the selected index is "Material cache", disregard spot animations
        if (isMaterialCache) {
            // Only interact with material cache if the player is not currently animating
            if (player.getAnimationId() == -1 && !digsites.isEmpty()) {
                logGrey("Player is not animating. Interacting with nearest Material cache digsite.");
                interactWithNearestDigsite(digsites, selectedDigsiteName, " - Player is not animating");
            }
            return; // Exit to ensure no further processing of spot animations
        }

        // Handle spot animations if not a Material cache
        EntityResultSet<SpotAnimation> spotAnimations = SpotAnimationQuery.newQuery().ids(7307).results();
        logGrey("Queried spot animations. Found: " + spotAnimations.size());

        if (!spotAnimations.isEmpty()) {
            interactWithSpotAnimations(spotAnimations, digsites, selectedDigsiteName);
        } else if (player.getAnimationId() == -1 && !digsites.isEmpty()) {
            logGrey("Player is not animating. Interacting with nearest digsite.");
            interactWithNearestDigsite(digsites, selectedDigsiteName, " - Player is not animating");
        }
    }

    private static void interactWithNearestDigsite(List<SceneObject> digsites, String selectedName, String message) {
        LocalPlayer player = Client.getLocalPlayer();
        EntityResultSet<SceneObject> visibleDigsites = SceneObjectQuery.newQuery()
                .name(selectedName)
                .hidden(false)
                .option("Excavate")
                .results();

        EntityResultSet<SceneObject> hiddenDigsites = SceneObjectQuery.newQuery()
                .name(selectedName)
                .hidden(true)
                .option("Excavate")
                .results();

        SceneObject nearestVisibleDigsite = visibleDigsites.stream()
                .min(Comparator.comparingDouble(digsite -> digsite.getCoordinate().distanceTo(Client.getLocalPlayer().getCoordinate())))
                .orElse(null);

        hiddenDigsites.forEach(digsite ->
                logOrange("Hidden digsite: " + digsite.getName() + " at " + digsite.getCoordinate())
        );

        if (nearestVisibleDigsite != null) {
            logBlue("Interacted with: " + nearestVisibleDigsite.getName() + message + " - Excavate: " + nearestVisibleDigsite.interact("Excavate"));
            Execution.delayUntil(10000, () -> player.getAnimationId() != -1);
        } else if (!hiddenDigsites.isEmpty()) {
            worldHopforArch(); // Hop worlds if no visible digsites but hidden ones exist
        }
    }

    private static void interactWithSpotAnimations(EntityResultSet<SpotAnimation> animations, List<SceneObject> digsites, String selectedName) {
        SpotAnimation currentAnimation = animations.first();
        Coordinate currentCoordinate = currentAnimation.getCoordinate();
        logGrey("Current spot animation coordinate: " + currentCoordinate);

        LocalPlayer player = Client.getLocalPlayer();

        SceneObject matchingDigsite = digsites.stream()
                .filter(digsite -> digsite.getCoordinate().equals(currentCoordinate) && digsite.getName().equals(selectedName))
                .findFirst()
                .orElse(null);

        if (matchingDigsite != null) {
            if (lastInteractedSpotAnimationCoordinate == null || !lastInteractedSpotAnimationCoordinate.equals(currentCoordinate) || player.getAnimationId() == -1) {
                Execution.delay(random.nextLong(500, 2500));
                logSuccess("Interacted with: " + matchingDigsite.getName() + " - Sprite moved: " + matchingDigsite.interact("Excavate"));
                lastInteractedSpotAnimationCoordinate = currentCoordinate;
            }
        } else {
            logGrey("Spot animation does not match the selected digsite.");
            interactWithNearestDigsite(digsites, selectedName, " - Player is not animating");
        }
    }
}
