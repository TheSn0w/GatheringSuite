package net.Snow.SlimeCollector;

import net.Snow.AbstractScript;
import net.Snow.Banking.BankLocations;
import net.Snow.ImGui.SnowsTemplateGraphics;
import net.Snow.Runecrafting.WorldHop;
import net.Snow.Snow;
import net.Snow.Woodcutting.WoodcuttingUpdates;
import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.rs3.events.impl.ChatMessageEvent;
import net.botwithus.rs3.events.impl.InventoryUpdateEvent;
import net.botwithus.rs3.game.Area;
import net.botwithus.rs3.game.Coordinate;
import net.botwithus.rs3.game.Item;
import net.botwithus.rs3.game.minimenu.MiniMenu;
import net.botwithus.rs3.game.minimenu.actions.SelectableAction;
import net.botwithus.rs3.game.queries.builders.items.InventoryItemQuery;
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery;
import net.botwithus.rs3.game.queries.results.EntityResultSet;
import net.botwithus.rs3.game.queries.results.ResultSet;
import net.botwithus.rs3.game.scene.entities.object.SceneObject;
import net.botwithus.rs3.game.skills.Skills;
import net.botwithus.rs3.script.Execution;
import net.botwithus.rs3.util.RandomGenerator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static net.Snow.Banking.BankLocations.goToBanktoLoadPreset2;
import static net.Snow.Combat.Combat.player;
import static net.Snow.ImGui.CustomLogger.*;
import static net.Snow.ImGui.Experience.inventoryMap;
import static net.Snow.ImGui.Experience.startTracking;
import static net.Snow.Snow.shutdown;

public class SlimeCollector implements AbstractScript {

    Snow script;

    @Override
    public void initialize() {
        startTracking();
    }

    @Override
    public void draw(SnowsTemplateGraphics sgc) throws IOException {
        SlimeCollectorGraphics.draw(sgc, script);
    }

    @Override
    public void onInventoryUpdate(InventoryUpdateEvent event) {
        WoodcuttingUpdates.onInventoryUpdate(event, inventoryMap);
        SlimeCollectorUpdates.onInventoryUpdate(event, inventoryMap);
    }

    @Override
    public void onChatMessageEvent(ChatMessageEvent event) {
        WoodcuttingUpdates.onChatMessageEvent(event, inventoryMap);
        SlimeCollectorUpdates.onChatMessageEvent(event, inventoryMap);
    }

    public static final Area ECTOPHIAL = new Area.Rectangular(
            new Coordinate(3652, 3511, 0), // Bottom-left coordinate of the area
            new Coordinate(3669, 3529, 0)  // Top-right coordinate of the area
    );

    private static SlimeState slimeState;

    static SlimeState getSlimeState() {
        return slimeState;
    }

    public static void setSlimeState(SlimeState slimeState) {
        SlimeCollector.slimeState = slimeState;
    }

    public static void determineState() {
        if (ECTOPHIAL.contains(player)) {
            setSlimeState(SlimeState.COLLECTING);
        } else if (player.getCoordinate().equals(new Coordinate(3683, 9888, 0))) {
            setSlimeState(SlimeState.COLLECTING);
        } else {
            setSlimeState(SlimeState.ECTOPHIAL);
        }
    }

    private boolean stateDetermined = false;

    @Override
    public void run(Snow script) {
        try {
            this.script = script;

            if (!stateDetermined) {
                determineState(); // Set the initial state based on the player's location
                stateDetermined = true;
            }

            if (getSlimeState() == SlimeState.COLLECTING){
                WorldHop.worldHopNoTimer();
            }

            // Pause script while world hop is in progress
            while (WorldHop.isWorldHopInProgress()) {
                Execution.delay(1000);  // Wait 1 second before checking again
            }

            if (player.isMoving()) {
                return;
            }

            switch (getSlimeState()) {
                case ECTOPHIAL:
                    if (ECTOPHIAL.contains(player)) {
                        setSlimeState(SlimeState.COLLECTING);
                    } else {
                        if (Backpack.contains("Ectophial")) {
                            logBlue("Backpack contains Ectophial, using it to teleport to slime location.");
                            Backpack.interact("Ectophial", "Empty");
                            Execution.delayUntil(10000, () -> ECTOPHIAL.contains(player));
                        } else {
                            logError("Ectophial not found in backpack, please add it to continue.");
                        }
                    }
                    return;
                case COLLECTING:
                    collect();
                    return;
                case NOTING:
                    notepaperOnSlime();
                    return;
                case MOVING:
                    travel();
                    return;
                default:
                    throw new IllegalStateException("Unexpected value: " + getSlimeState());
            }
        } catch (Exception e) {
            logError("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static boolean hasChecked = false;


    private void collect() {
        try {
            logGrey("Starting collect method.");

            EntityResultSet<SceneObject> slime = SceneObjectQuery.newQuery().id(17119).option("Use slime").results();
            logBlue("Querying for slime objects with ID 17119 and option 'Use slime'.");

            if (Backpack.isFull()) {
                logOrange("Backpack is full. Changing state to NOTING.");
                setSlimeState(SlimeState.NOTING);
            } else {
                if (player.getCoordinate().equals(new Coordinate(3683, 9888, 0))) {
                    logBlue("Player is at the slime collection coordinate.");

                    if (slime.isEmpty()) {
                        logError("Slime not found.");
                    } else {
                        ResultSet<Item> notepaperResults = InventoryItemQuery.newQuery(93).name("Magic notepaper", "Enchanted notepaper").results();
                        if (notepaperResults.isEmpty()) {
                            logError("Neither Magic Notepaper nor Enchanted Notepaper found in inventory.");
                            if (BankLocations.selectedBankIndex == 0) {
                                logError("Bank location not selected. Please select a bank location.");
                                shutdown();
                            } else {
                                log("Bank location selected: " + BankLocations.selectedBankIndex);
                                log("We are heading to withdraw preset 2 and head over to Necromancy.");
                                goToBanktoLoadPreset2();
                            }
                        } else {
                            logSuccess("Slime found. Attempting to interact with slime.");
                            slime.nearest().interact("Use slime");
                            logSuccess("Interacting with slime.");
                            Execution.delayUntil(30000, Backpack::isFull);
                        }
                    }
                } else {
                    logBlue("Player is not at the slime collection coordinate. Changing state to MOVING.");
                    setSlimeState(SlimeState.MOVING);
                }
            }
        } catch (Exception e) {
            logError("An error occurred in collect method: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private void travel() {
        // Check if player is moving
        if (player.isMoving()) {
            return;
        }

        // Check for trap door
        EntityResultSet<SceneObject> trapDoor = SceneObjectQuery.newQuery().id(5267).option("Open").results();
        if (!trapDoor.isEmpty()) {
            trapDoor.nearest().interact("Open");
            logOrange("Found trap door.");
            Execution.delay(1000);
        }

        // Check for open door
        EntityResultSet<SceneObject> openDoor = SceneObjectQuery.newQuery().id(5268).option("Climb-down").results();
        if (!openDoor.isEmpty()) {
            openDoor.nearest().interact("Climb-down");
            logOrange("Found open door.");
            Execution.delay(1000);
            return;
        }

        // Delay to allow any recent interactions to complete
        Execution.delay(1000);

        // Continue if the player's agility is sufficient
        if (Skills.AGILITY.getActualLevel() >= 58) {
            if (player.getCoordinate().equals(new Coordinate(3669, 9888, 3))) {
                EntityResultSet<SceneObject> weatheredWall = SceneObjectQuery.newQuery().id(9308).option("Jump-down").results();
                if (!weatheredWall.isEmpty()) {
                    weatheredWall.nearest().interact("Jump-down");
                    logOrange("Found weathered wall.");
                    return;
                }
            }

            if (player.getCoordinate().equals(new Coordinate(3671, 9888, 2))) {
                EntityResultSet<SceneObject> stair = SceneObjectQuery.newQuery().id(5263).option("Climb-down").results();
                if (!stair.isEmpty()) {
                    stair.nearest().interact("Climb-down");
                    logOrange("Found stairs.");
                    return;
                }
            }

            if (player.getCoordinate().equals(new Coordinate(3675, 9888, 1))) {
                EntityResultSet<SceneObject> stair = SceneObjectQuery.newQuery().id(5263).option("Climb-down").results();
                if (!stair.isEmpty()) {
                    stair.nearest().interact("Climb-down");
                    logOrange("Found stairs.");
                    return;
                }
            }

            if (player.getCoordinate().equals(new Coordinate(3683, 9888, 0))) {
                setSlimeState(SlimeState.COLLECTING);
                return;
            }
        }

        // Handle other coordinates for different stairs
        if (player.getCoordinate().equals(new Coordinate(3669, 9888, 3))) {
            EntityResultSet<SceneObject> stair = SceneObjectQuery.newQuery().id(5263).option("Climb-down").results();
            if (!stair.isEmpty()) {
                stair.nearest().interact("Climb-down");
                logOrange("Found first stair.");
                return;
            }
        }

        if (player.getCoordinate().equals(new Coordinate(3688, 9888, 2))) {
            EntityResultSet<SceneObject> stair = SceneObjectQuery.newQuery().id(5263).option("Climb-down").results();
            if (!stair.isEmpty()) {
                stair.nearest().interact("Climb-down");
                logOrange("Found stairs.");
                return;
            }
        }

        if (player.getCoordinate().equals(new Coordinate(3675, 9888, 1))) {
            EntityResultSet<SceneObject> stair = SceneObjectQuery.newQuery().id(5263).option("Climb-down").results();
            if (!stair.isEmpty()) {
                stair.nearest().interact("Climb-down");
                logOrange("Found stairs.");
                return;
            }
        }

        if (player.getCoordinate().equals(new Coordinate(3683, 9888, 0))) {
            setSlimeState(SlimeState.COLLECTING);
        }
    }



    public static void notepaperOnSlime() {
        try {
            List<Item> backpackItems = new ArrayList<>(Backpack.getItems());
            logOrange("Retrieved " + backpackItems.size() + " items from the backpack.");

            ResultSet<Item> results = InventoryItemQuery.newQuery().ids(4286).results();
            logOrange("Querying for items with ID 4286 and option 'Use'.");

            if (!results.isEmpty()) {
                int slotNumber = results.first().getSlot();
                logError("Bucket of slime found in slot: " + slotNumber);

                ResultSet<Item> notepaperResults = InventoryItemQuery.newQuery(93).name("Magic notepaper", "Enchanted notepaper").results();
                logOrange("Querying for Magic Notepaper or Enchanted Notepaper.");

                if (notepaperResults.isEmpty()) {
                    logError("Neither Magic Notepaper nor Enchanted Notepaper found in inventory.");
                    return;
                }

                Item notepaper = notepaperResults.first();
                logOrange("Notepaper found: " + notepaper.getName() + " in slot: " + notepaper.getSlot());

                boolean itemSelected = MiniMenu.interact(SelectableAction.SELECTABLE_COMPONENT.getType(), 0, slotNumber, 96534533);
                logOrange("Item selected: " + itemSelected);
                Execution.delay(RandomGenerator.nextInt(200, 300));

                if (itemSelected) {
                    boolean notepaperSelected = MiniMenu.interact(SelectableAction.SELECT_COMPONENT_ITEM.getType(), 0, notepaper.getSlot(), 96534533);
                    logOrange("Notepaper selected: " + notepaperSelected);

                    if (notepaperSelected) {
                        String notepaperName = notepaper.getName();
                        logSuccess("Bucket of slime successfully used on " + notepaperName + ".");
                        setSlimeState(SlimeState.COLLECTING);
                    } else {
                        String notepaperName = notepaper.getName();
                        logError("Failed to use Bucket of slime on " + notepaperName + ".");
                        logOrange("Notepaper details - Name: " + notepaper.getName() + ", ID: " + notepaper.getId());
                        setSlimeState(SlimeState.COLLECTING);
                    }
                } else {
                    logError("Failed to select Bucket of slime.");
                    logOrange("Item details - Name: Bucket of slime, ID: 4286");
                    setSlimeState(SlimeState.COLLECTING);
                }
            } else {
                logError("Bucket of slime not found in inventory.");
                setSlimeState(SlimeState.COLLECTING);
            }
        } catch (Exception e) {
            logError("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
