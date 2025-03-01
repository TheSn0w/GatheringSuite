package net.Snow.Firemaking;

import net.Snow.AbstractScript;
import net.Snow.ImGui.SnowsTemplateGraphics;
import net.Snow.Snow;
import net.Snow.Thieving.ThievingGraphics;
import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.rs3.events.impl.ChatMessageEvent;
import net.botwithus.rs3.events.impl.InventoryUpdateEvent;
import net.botwithus.rs3.game.Coordinate;
import net.botwithus.rs3.game.Item;
import net.botwithus.rs3.game.movement.Movement;
import net.botwithus.rs3.game.movement.NavPath;
import net.botwithus.rs3.game.queries.builders.items.InventoryItemQuery;
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery;
import net.botwithus.rs3.game.queries.results.EntityResultSet;
import net.botwithus.rs3.game.queries.results.ResultSet;
import net.botwithus.rs3.game.scene.entities.characters.Headbar;
import net.botwithus.rs3.game.scene.entities.object.SceneObject;
import net.botwithus.rs3.game.skills.Skills;
import net.botwithus.rs3.script.Execution;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import static net.Snow.Combat.Combat.player;
import static net.Snow.ImGui.CustomLogger.*;
import static net.Snow.ImGui.Experience.inventoryMap;
import static net.Snow.ImGui.Experience.startTracking;

public class Firemaking implements AbstractScript {

    Snow script;
    private ScriptState currentState;


    @Override
    public void initialize() {
        startingFiremakingXP.set(Skills.FIREMAKING.getSkill().getExperience());
        startingFiremakingLevel.set(Skills.FIREMAKING.getSkill().getActualLevel());
        startTracking();


    }

    public AtomicInteger startingFiremakingXP = new AtomicInteger(0);
    public AtomicInteger startingFiremakingLevel = new AtomicInteger(0);


    @Override
    public void draw(SnowsTemplateGraphics sgc) throws IOException {
        ThievingGraphics.draw(sgc, script, startingFiremakingLevel, startingFiremakingXP);
    }

    @Override
    public void onInventoryUpdate(InventoryUpdateEvent event) {
        FiremakingUpdates.onInventoryUpdate(event, inventoryMap);
    }

    @Override
    public void onChatMessageEvent(ChatMessageEvent event) {
        FiremakingUpdates.onChatMessageEvent(event, inventoryMap);
    }

    @Override
    public int getLoopDelay() {
        return 550;
    }

    @Override
    public void saveConfiguration() {
        // Implementation of saveConfiguration
        // Add your logic to save the configuration here
    }

    @Override
    public void loadConfiguration() {
        // Implementation of loadConfiguration
        // Add your logic to load the configuration here
    }



    @Override
    public void run(Snow script) {
        switch (currentState) {
            case INITIAL_STATE:
                determineInitialState();
                break;
            case NAVIGATING_TO_BARN:
                moveToBarn();
                break;
            case COLLECTING_ANIMALS:
                collectAnimals();
                break;
            case TUMMISAURUS:
                tummisaurus();
                break;
            case ROOTLESAURUS:
                rootlesaurus();
                break;
            case BEANASAURUS:
                beanasaurus();
                break;
            case BERRISAURUS:
                berrisaurus();
                break;
            case USE_FERTILISER:
                useFertiliser();
                break;
        }


        // Get the current headbar associated with heating
        Optional<Headbar> headbar = player.getHeadbars().stream().filter(bar -> bar.getId() == 5).findAny();

        SceneObject forgeObject = forge.nearest();
        if (forgeObject != null) {
            if (headbar.isPresent()) {
                logOrange("Heating the unfinished item at the forge. Current heat level: " + headbar.get().getWidth());
            } else {
                logError("No headbar found. Cannot determine the current heat level.");
                return;  // Exit if there's no headbar
            }

            if (forgeObject.interact("Heat")) {
                logSuccess("Successfully started heating the item at the forge.");

                // Wait until the headbar's width exceeds 250 or the headbar is no longer present
                boolean success = Execution.delayUntil(
                        30000,
                        () -> {
                            Optional<Headbar> updatedHeadbar = player.getHeadbars().stream().filter(bar -> bar.getId() == 5).findAny();
                            return updatedHeadbar.map(bar -> bar.getWidth() > 250).orElse(true); // Exit if width > 250 or headbar is absent
                        }
                );

                if (success) {
                    EntityResultSet<SceneObject> anvil = SceneObjectQuery.newQuery().name("Anvil").option("Smith").results();
                    if (anvil.isEmpty()) {
                        logError("Anvil not found nearby.");
                        return;
                    } else {
                        anvil.nearest().interact("Smith");
                        Execution.delayUntil(60000, () -> {
                            Optional<Headbar> updatedHeadbar = player.getHeadbars().stream().filter(bar -> bar.getId() == 5).findAny();
                            return updatedHeadbar.map(bar -> bar.getWidth() < 190).orElse(true); // Exit if width < 190 or headbar is absent
                        });
                        logSuccess("Item fully heated. Heat level is now greater than 250, or the headbar is no longer present.");
                    }
                } else {
                    logError("Heating timed out. The heat level did not reach the expected value.");
                }
            } else {
                logError("Failed to interact with the forge.");
            }
        } else {
            logError("Forge not found nearby.");
        }
    }

    private static final EntityResultSet<SceneObject> forge = SceneObjectQuery.newQuery().name("Forge").option("Heat").results();


    ///        Quick fires in prif with logs in inventory
//        // Query for the Bank Chest SceneObject
//        logOrange("Querying for the Bank Chest SceneObject...");
//        EntityResultSet<SceneObject> bankChest = SceneObjectQuery.newQuery()
//                .id(92692)
//                .option("Use")
//                .results();
//
//        // Check if the backpack contains items that can be "Light"-ed
//        logOrange("Checking if the backpack contains items to light...");
//        if (Backpack.containsItemByCategory(22)) {
//            logOrange("Found items in the backpack to light. Querying items with the 'Light' option...");
//            // Query items in the inventory with the "Light" option
//            ResultSet<Item> results = InventoryItemQuery.newQuery()
//                    .option("Light")
//                    .results();
//
//            // Interact with each item that has the "Light" option
//            for (Item item : results) {
//                String itemName = item.getName();
//                logBlue("Attempting to interact with item: " + itemName);
//                if (Backpack.interact(itemName, "Light")) {
//                    logOrange("Checking for a 'Fire' SceneObject at the player's location...");
//
//                    // Periodically check for the "Fire" SceneObject near the player
//                    boolean fireAppeared = false;
//                    for (int i = 0; i < 20; i++) { // Check 20 times with 200ms interval
//                        // Re-query for the Fire SceneObject
//                        SceneObject fire = SceneObjectQuery.newQuery()
//                                .name("Fire")
//                                .results()
//                                .nearest();
//
//                        // Re-query for the player's current coordinate
//                        Coordinate playerCoordinate = Client.getLocalPlayer().getServerCoordinate();
//
//                        // Log player's current coordinate
//                        logError("Player's current coordinate: " + playerCoordinate);
//
//                        if (fire != null) {
//                            Coordinate fireCoordinate = fire.getCoordinate();
//                            // Log nearest Fire SceneObject and its coordinate
//                            logError("Nearest 'Fire' SceneObject at coordinate: " + fireCoordinate);
//
//                            if (fireCoordinate.equals(playerCoordinate)) {
//                                fireAppeared = true;
//                                logSuccess("Fire appeared at the player's location.");
//                                break;
//                            } else {
//                                logError("Fire is not at the player's location.");
//                            }
//                        } else {
//                            logError("No 'Fire' SceneObject found.");
//                        }
//
//                        logBlue("Retrying in 200ms...");
//                        Execution.delay(200); // Wait for 200ms before checking again
//                    }
//
//                    if (!fireAppeared) {
//                        logOrange("No Fire appeared after 20 attempts.");
//                    }
//                }
//            }
//        } else {
//            logOrange("No items to light found in the backpack. Interacting with the nearest Bank Chest...");
//            // Interact with the nearest Bank Chest if there are no items to "Light"
//            SceneObject bank = bankChest.nearest();
//            if (bank != null) {
//                logSuccess("Interacting with Bank Chest: " + bank.interact("Load Last Preset from"));
//                logOrange("Waiting for the backpack to be full...");
//                Execution.delayUntil(30000, Backpack::isFull); // Wait until the backpack is full
//
//                // Once the backpack is full, move to the specified coordinate
//                if (Backpack.isFull()) {
//                    logOrange("Backpack is full. Moving to the specified coordinate...");
//                    Coordinate targetCoordinate = new Coordinate(2230, 3311, 1);
//                    Movement.walkTo(targetCoordinate.getX(), targetCoordinate.getY(), true);
//                    logOrange("Waiting until the player reaches the target coordinate...");
//                    Execution.delayUntil(30000, () ->
//                            Client.getLocalPlayer().getCoordinate().equals(targetCoordinate)
//                    );
//                    logOrange("Player has reached the target coordinate.");
//                }
//            } else {
//                logOrange("No Bank Chest found to interact with.");
//            }
//        }
//    }

    private Coordinate randomizeLocation(Coordinate original, int maxOffset) {
        Random random = new Random();
        int xAdjustment = random.nextInt(maxOffset * 2 + 1) - maxOffset;
        int yAdjustment = random.nextInt(maxOffset * 2 + 1) - maxOffset;
        return new Coordinate(original.getX() + xAdjustment, original.getY() + yAdjustment, original.getZ());
    }

    public enum ScriptState {
        NAVIGATING_TO_BARN,
        INITIAL_STATE,
        USE_FERTILISER,
        TUMMISAURUS,
        ROOTLESAURUS,
        BEANASAURUS,
        BERRISAURUS,
        COLLECTING_ANIMALS
    }


    private void moveToBarn() {
        Coordinate barnLocation = new Coordinate(5303, 2276, 0);
        Coordinate randomisedBarnLocation = randomizeLocation(barnLocation, 1);
        if (player.getCoordinate().getRegionId() == 21027 || player.getCoordinate().getRegionId() == 21283) {
            logBlue("Already at the correct location.");
            currentState = ScriptState.INITIAL_STATE;
        } else {
            logBlue("Navigating to the correct location...");
            Movement.traverse(NavPath.resolve(randomisedBarnLocation));
            currentState = ScriptState.INITIAL_STATE;
        }
    }

    private void determineInitialState() {
        if (Backpack.contains("Potterington Blend #102 Fertiliser")) {
            logBlue("Found Potterington Blend #102 Fertiliser in backpack.");
            currentState = ScriptState.USE_FERTILISER;
        } else if (Backpack.contains("Tummisaurus Rex")) {
            logBlue("Found Tummisaurus Rex in backpack.");
            currentState = ScriptState.TUMMISAURUS;
        } else if (Backpack.contains("Rootlesaurus Rex")) {
            logBlue("Found Rootlesaurus Rex in backpack.");
            currentState = ScriptState.ROOTLESAURUS;
        } else if (Backpack.contains("Beanasaurus Rex")) {
            logBlue("Found Beanasaurus Rex in backpack.");
            currentState = ScriptState.BEANASAURUS;
        } else if (Backpack.contains("Berrisaurus Rex")) {
            logBlue("Found Berrisaurus Rex in backpack.");
            currentState = ScriptState.BERRISAURUS;
        } else {
            logBlue("No specific items found in backpack. Collecting animals...");
            currentState = ScriptState.COLLECTING_ANIMALS;
        }
    }

    private void collectAnimals() {
        EntityResultSet<SceneObject> StormBarn = SceneObjectQuery.newQuery().name("Storm barn").results();
        logBlue("Navigating to Storm barn...");
        Coordinate stormBarnLocation = new Coordinate(5305, 2280, 0);
        Coordinate randomisedStormBarnLocation = randomizeLocation(stormBarnLocation, 2);
        Movement.traverse(NavPath.resolve(randomisedStormBarnLocation));
        if (StormBarn.isEmpty()) {
            if (SceneObjectQuery.newQuery().name("Storm barn").results().nearest().interact("Collect")) {
                logSuccess("Interacted with Storm barn.");
                boolean success = Execution.delayUntil(60000, Backpack::isFull);
                if (success) {
                    logSuccess("Animals collected successfully.");
                } else {
                    logError("Failed to collect animals within the expected time.");
                }
            } else {
                logError("Failed to interact with Storm barn.");
            }
        } else {
            logBlue("Collecting animals...");
            boolean interacted = SceneObjectQuery.newQuery().name("Storm barn").results().nearest().interact("Collect");
            if (interacted) {
                logSuccess("Interacted with Storm barn.");
                boolean success = Execution.delayUntil(60000, Backpack::isFull);
                if (success) {
                    logSuccess("Animals collected successfully.");
                } else {
                    logError("Failed to collect animals within the expected time.");
                }
            } else {
                logError("Failed to interact with Storm barn.");
            }
            currentState = ScriptState.TUMMISAURUS;
        }
    }

    private void tummisaurus() {
        EntityResultSet<SceneObject> tummisaurus = SceneObjectQuery.newQuery().name("Rooty mush trough").results();
        Coordinate tummisaurusLocation = new Coordinate(5289, 2259, 0);
        Coordinate randomisedTummisaurusLocation = randomizeLocation(tummisaurusLocation, 1);
        logBlue("Navigating to Rooty mush trough...");
        Movement.traverse(NavPath.resolve(randomisedTummisaurusLocation));
        if (tummisaurus.isEmpty()) {
            if (SceneObjectQuery.newQuery().name("Rooty mush trough").results().nearest().interact("Feed")) {
                logSuccess("Interacted with Rooty mush trough.");
                boolean success = Execution.delayUntil(60000, () -> !Backpack.contains("Tummisaurus Rex"));
                if (success) {
                    logSuccess("Tummisaurus Rex fed successfully.");
                } else {
                    logError("Failed to feed Tummisaurus Rex within the expected time.");
                }
            } else {
                logError("Failed to interact with Rooty mush trough.");
            }
        } else {
            logBlue("Feeding tummisaurus...");
            boolean interacted = Objects.requireNonNull(tummisaurus.nearest()).interact("Feed");
            if (interacted) {
                logSuccess("Interacted with Rooty mush trough.");
                boolean success = Execution.delayUntil(60000, () -> !Backpack.contains("Tummisaurus Rex"));
                if (success) {
                    logSuccess("Tummisaurus Rex fed successfully.");
                } else {
                    logError("Failed to feed Tummisaurus Rex within the expected time.");
                }
            } else {
                logError("Failed to interact with Rooty mush trough.");
            }
        }
        currentState = ScriptState.ROOTLESAURUS;
    }

    private void rootlesaurus() {
        EntityResultSet<SceneObject> rootlesaurus = SceneObjectQuery.newQuery().name("Beany mush trough").results();
        Coordinate rootlesaurusLocation = new Coordinate(5311, 2293, 0);
        Coordinate randomisedrootlesaurusLocation = randomizeLocation(rootlesaurusLocation, 2);
        logBlue("Navigating to Beany mush trough...");
        Movement.traverse(NavPath.resolve(randomisedrootlesaurusLocation));
        if (rootlesaurus.isEmpty()) {
            if (SceneObjectQuery.newQuery().name("Beany mush trough").results().nearest().interact("Feed")) {
                logSuccess("Interacted with Beany mush trough.");
                boolean success = Execution.delayUntil(60000, () -> !Backpack.contains("Rootlesaurus Rex"));
                if (success) {
                    logSuccess("Rootlesaurus Rex fed successfully.");
                } else {
                    logError("Failed to feed Rootlesaurus Rex within the expected time.");
                }
            } else {
                logError("Failed to interact with Beany mush trough.");
            }
        } else {
            logBlue("Feeding rootlesaurus...");
            boolean interacted = Objects.requireNonNull(rootlesaurus.nearest()).interact("Feed");
            if (interacted) {
                logSuccess("Interacted with Beany mush trough.");
                boolean success = Execution.delayUntil(60000, () -> !Backpack.contains("Rootlesaurus Rex"));
                if (success) {
                    logSuccess("Rootlesaurus Rex fed successfully.");
                } else {
                    logError("Failed to feed Rootlesaurus Rex within the expected time.");
                }
            } else {
                logError("Failed to interact with Beany mush trough.");
            }
        }
        currentState = ScriptState.BEANASAURUS;
    }

    private void beanasaurus() {
        EntityResultSet<SceneObject> beanasaurus = SceneObjectQuery.newQuery().name("Berry mush trough").results();
        Coordinate beanasaurusLocation = new Coordinate(5329, 2289, 0);
        Coordinate randomisedbeanasaurusLocation = randomizeLocation(beanasaurusLocation, 2);
        logBlue("Navigating to Berry mush trough...");
        Movement.traverse(NavPath.resolve(randomisedbeanasaurusLocation));
        if (beanasaurus.isEmpty()) {
            if (SceneObjectQuery.newQuery().name("Berry mush trough").results().nearest().interact("Feed")) {
                logSuccess("Interacted with Berry mush trough.");
                boolean success = Execution.delayUntil(60000, () -> !Backpack.contains("Beanasaurus Rex"));
                if (success) {
                    logSuccess("Beanasaurus Rex fed successfully.");
                } else {
                    logError("Failed to feed Beanasaurus Rex within the expected time.");
                }
            } else {
                logError("Failed to interact with Berry mush trough.");
            }
        } else {
            logBlue("Feeding beanasaurus...");
            boolean interacted = Objects.requireNonNull(beanasaurus.nearest()).interact("Feed");
            if (interacted) {
                logSuccess("Interacted with Berry mush trough.");
                boolean success = Execution.delayUntil(60000, () -> !Backpack.contains("Beanasaurus Rex"));
                if (success) {
                    logSuccess("Beanasaurus Rex fed successfully.");
                } else {
                    logError("Failed to feed Beanasaurus Rex within the expected time.");
                }
            } else {
                logError("Failed to interact with Berry mush trough.");
            }
        }
        currentState = ScriptState.BERRISAURUS;
    }

    private void berrisaurus() {
        EntityResultSet<SceneObject> berrisaurus = SceneObjectQuery.newQuery().name("Cerealy mush trough").results();
        Coordinate berrisaurusLocation = new Coordinate(5329, 2269, 0);
        Coordinate randomisedberrisaurusLocation = randomizeLocation(berrisaurusLocation, 2);
        logBlue("Navigating to Cerealy mush trough...");
        Movement.traverse(NavPath.resolve(randomisedberrisaurusLocation));
        if (berrisaurus.isEmpty()) {
            if (SceneObjectQuery.newQuery().name("Cerealy mush trough").results().nearest().interact("Feed")) {
                logSuccess("Interacted with Cerealy mush trough.");
                boolean success = Execution.delayUntil(60000, () -> !Backpack.contains("Berrisaurus Rex"));
                if (success) {
                    logSuccess("Berrisaurus Rex fed successfully.");
                } else {
                    logError("Failed to feed Berrisaurus Rex within the expected time.");
                }
            } else {
                logError("Failed to interact with Cerealy mush trough.");
            }
        } else {
            logBlue("Feeding berrisaurus...");
            boolean interacted = Objects.requireNonNull(berrisaurus.nearest()).interact("Feed");
            if (interacted) {
                logSuccess("Interacted with Cerealy mush trough.");
                boolean success = Execution.delayUntil(60000, () -> !Backpack.contains("Berrisaurus Rex"));
                if (success) {
                    logSuccess("Berrisaurus Rex fed successfully.");
                } else {
                    logError("Failed to feed Berrisaurus Rex within the expected time.");
                }
            } else {
                logError("Failed to interact with Cerealy mush trough.");
            }
        }
        currentState = ScriptState.USE_FERTILISER;
    }

    private void useFertiliser() {
        ResultSet<Item> fertiliserItems = InventoryItemQuery.newQuery(93).name("Potterington Blend #102 Fertiliser").results();
        if (!fertiliserItems.isEmpty()) {
            logBlue("Using fertiliser...");
            Item firstFertiliser = fertiliserItems.first();
            if (firstFertiliser != null) {
                boolean interacted = Backpack.interact("Potterington Blend #102 Fertiliser", "Ignite");
                if (interacted) {
                    logSuccess("Successfully ignited the fertiliser.");
                    boolean success = Execution.delayUntil(60000, () -> !Backpack.contains("Potterington Blend #102 Fertiliser"));
                    if (success) {
                        logSuccess("Fertiliser used successfully.");
                        currentState = ScriptState.COLLECTING_ANIMALS;
                    } else {
                        logError("Failed to use the fertiliser within the expected time.");
                    }
                } else {
                    logError("Failed to interact with the fertiliser.");
                }
            } else {
                logError("No fertiliser found in the inventory.");
            }
        } else {
            logError("No fertiliser items found.");
        }
    }
}
