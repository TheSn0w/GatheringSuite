package net.Snow.AbyssRunecrafting;

import net.Snow.AbstractScript;
import net.Snow.ImGui.SnowsTemplateGraphics;
import net.Snow.Runecrafting.Variables;
import net.Snow.Runecrafting.WorldHop;
import net.Snow.Snow;
import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.api.game.hud.inventories.Equipment;
import net.botwithus.rs3.events.impl.ChatMessageEvent;
import net.botwithus.rs3.events.impl.InventoryUpdateEvent;
import net.botwithus.rs3.game.Area;
import net.botwithus.rs3.game.Client;
import net.botwithus.rs3.game.Coordinate;
import net.botwithus.rs3.game.Item;
import net.botwithus.rs3.game.actionbar.ActionBar;
import net.botwithus.rs3.game.minimenu.MiniMenu;
import net.botwithus.rs3.game.minimenu.actions.ComponentAction;
import net.botwithus.rs3.game.movement.Movement;
import net.botwithus.rs3.game.queries.builders.characters.NpcQuery;
import net.botwithus.rs3.game.queries.builders.items.InventoryItemQuery;
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery;
import net.botwithus.rs3.game.queries.results.EntityResultSet;
import net.botwithus.rs3.game.queries.results.ResultSet;
import net.botwithus.rs3.game.scene.entities.characters.npc.Npc;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.game.scene.entities.object.SceneObject;
import net.botwithus.rs3.game.skills.Skills;
import net.botwithus.rs3.game.vars.VarManager;
import net.botwithus.rs3.script.Execution;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static net.Snow.AbyssRunecrafting.AbyssRunecraftingGraphics.useFamiliar;
import static net.Snow.AbyssRunecrafting.Variables.*;
import static net.Snow.ImGui.CustomLogger.*;
import static net.Snow.ImGui.Experience.inventoryMap;
import static net.Snow.ImGui.Experience.startTracking;
import static net.Snow.LevelLogout.CheckLevel.checkLevelForSkill;
import static net.Snow.Runecrafting.Variables.loopCounter;
import static net.Snow.Snow.*;

public class AbyssRunecrafting implements AbstractScript {

    private Snow script;
    private LocalPlayer player = Client.getLocalPlayer();

    private AtomicInteger startingRunecraftingXP = new AtomicInteger(0);
    private AtomicInteger startingRunecraftingLevel = new AtomicInteger(0);

    @Override
    public void initialize() {
        startingRunecraftingXP.set(Skills.RUNECRAFTING.getSkill().getExperience());
        startingRunecraftingLevel.set(Skills.RUNECRAFTING.getSkill().getLevel());
        startTracking();
        Variables.loopCounter = 0;
        determineState();
    }

    public final Map<String, Integer> itemPrices = new ConcurrentHashMap<>();


    @Override
    public void draw(SnowsTemplateGraphics sgc) {
        AbyssRunecraftingGraphics.draw(sgc, script, startingRunecraftingLevel, startingRunecraftingXP, inventoryMap, itemPrices);
    }

    @Override
    public void onInventoryUpdate(InventoryUpdateEvent event) {
        AbyssRunecraftingUpdates.onInventoryUpdate(event, inventoryMap);
    }

    @Override
    public void onChatMessageEvent(ChatMessageEvent event) {
        AbyssRunecraftingUpdates.onChatMessageEvent(event, inventoryMap, itemPrices);
    }


    @Override
    public int getLoopDelay() {
        return 100;
    }

    private static Instant lastStateChangeTime;

    public static AbyssState botstate = null;

    public AbyssState getBotState() {
        return botstate;
    }

    public static void setBotState(AbyssState newState) {
        botstate = newState;
        lastStateChangeTime = Instant.now();
        logGrey("Bot state changed to: " + newState + " at " + lastStateChangeTime);
    }

    public void run(Snow script) {

        checkLevelForSkill(Skills.RUNECRAFTING.getSkill());

        if (botstate == null) {
            determineState();
        }

        // Attempt world hop if needed
        WorldHop.worldHopNoTimer();

        // Pause script while world hop is in progress
        while (WorldHop.isWorldHopInProgress()) {
            Execution.delay(1000);  // Wait 1 second before checking again
        }

        switch (getBotState()) {
            case BANKING:
                banking();
                return;
            case EDGEVILLE:
                teleportToEdgeville();
                return;
            case WALL:
                interactWithWall();
                return;
            case MAGE:
                interactWithMage();
                return;
            case RIFT:
                interactWithRift();
                return;
            case ALTAR:
                interactWithAltar();
                return;
            case TELEPORTING:
                handleTeleporting();
                return;
            default:
                break;
        }
    }

    public static final Area BANK_AREA = new Area.Rectangular(new Coordinate(3859, 6825, 0), // Bottom-left coordinate of the area
            new Coordinate(3862, 6830, 0)  // Top-right coordinate of the area
    );

    public static final Area EDGEVILLE = new Area.Rectangular(new Coordinate(3074, 3496, 0), // Bottom-left coordinate of the area
            new Coordinate(3105, 3520, 0)  // Top-right coordinate of the area
    );

    public static final Area FIRST_SURGE = new Area.Rectangular(new Coordinate(3087, 3526, 0), // Bottom-left coordinate of the area
            new Coordinate(3093, 3528, 0)  // Top-right coordinate of the area
    );


    public static final Area AFTER_WALL = new Area.Rectangular(new Coordinate(3070, 3523, 0), // Bottom-left coordinate of the area
            new Coordinate(3105, 3525, 0)  // Top-right coordinate of the area
    );

    public static final Area WILDERNESS = new Area.Rectangular(new Coordinate(3070, 3523, 0), // Bottom-left coordinate of the area
            new Coordinate(3123, 3576, 0)  // Top-right coordinate of the area
    );


    public static final Area SECOND_SURGE = new Area.Rectangular(new Coordinate(3073, 3543, 0), // Bottom-left coordinate of the area
            new Coordinate(3109, 3544, 0)  // Top-right coordinate of the area
    );

    public static final Area RIFT_AREA = new Area.Rectangular(new Coordinate(3020, 4817, 0), // Bottom-left coordinate of the area
            new Coordinate(3055, 4855, 0)  // Top-right coordinate of the area
    );


    public static Area getAltarArea() {
        if (craftNatureRunes) {
            return new Area.Rectangular(new Coordinate(2404, 4847, 0), new Coordinate(2395, 4834, 0));
        } else if (craftBloodRunes) {
            return new Area.Rectangular(new Coordinate(2500, 4900, 0), new Coordinate(2490, 4890, 0)); /// not done
        } else if (craftCosmicRunes) {
            return new Area.Rectangular(new Coordinate(2600, 4950, 0), new Coordinate(2590, 4940, 0)); /// not done
        } else if (craftFireRunes) {
            return new Area.Rectangular(new Coordinate(2580, 4830, 0), new Coordinate(2591, 4843, 0));
        } else if (craftEarthRunes) {
            return new Area.Rectangular(new Coordinate(2652, 4836, 0), new Coordinate(2663, 4846, 0));
        } else if (craftMindRunes) {
            return new Area.Rectangular(new Coordinate(2780, 4837, 0), new Coordinate(2791, 4846, 0));
        } else if (craftAirRunes) {
            return new Area.Rectangular(new Coordinate(2839, 4829, 0), new Coordinate(2849, 4839, 0));
        } else if (craftWaterRunes) {
            return new Area.Rectangular(new Coordinate(3479, 4832, 0), new Coordinate(3488, 4841, 0));
        } else if (craftDeathRunes) {
            return new Area.Rectangular(new Coordinate(3200, 5250, 0), new Coordinate(3190, 5240, 0)); /// not done
        } else if (craftLawRunes) {
            return new Area.Rectangular(new Coordinate(3300, 5300, 0), new Coordinate(3290, 5290, 0)); /// not done
        } else if (craftChaosRunes) {
            return new Area.Rectangular(new Coordinate(2268, 4838, 0), new Coordinate(2275, 4846, 0));
        } else {
            return new Area.Rectangular(new Coordinate(2404, 4847, 0), new Coordinate(2395, 4834, 0)); // Default coordinates
        }
    }


    private void banking() {
        if (BANK_AREA.contains(player)) {
            if (!player.isMoving()) {
                EntityResultSet<SceneObject> bank = SceneObjectQuery.newQuery().id(90950).option("Search").results();
                if (useFamiliar) {
                    Familiar.checkFamiliar();
                }

                if (!bank.isEmpty()) {
                    logSuccess("Interacting with Bank Counter: " + bank.nearest().interact("Load Last Preset from"));
                    Execution.delayUntil(10000, () -> Backpack.contains(7936));
                    if (Backpack.contains(7936)) {
                        logBlue("Backpack contains Pure Essence, continuing to the next state.");
                        setBotState(AbyssState.EDGEVILLE);
                    } else {
                        shutdown();
                    }
                }
            }
        }
    }

    private void teleportToEdgeville() {
        if (EDGEVILLE.contains(player)) {
            logBlue("Player is in EDGEVILLE, continuing to the next state.");
        } else {
            Item sword = InventoryItemQuery.newQuery(94).name("sword", String::contains).results().first();
            if (sword != null) {
                logBlue("Sword found in slot: " + sword.getName());

                Equipment.interact(Equipment.Slot.WEAPON, "Edgeville");
                logSuccess("Interacting with sword: " + sword.getName());
            } else {
                logError("No Wilderness sword is Equipped.");
            }

            Execution.delayUntil(15000, () -> EDGEVILLE.contains(player));
        }
        setBotState(AbyssState.WALL);
    }

    private void interactWithWall() {

        if (player.isMoving() || player.getAnimationId() != -1) {
            return;
        }
        // Query for the wall with the specific interaction option
        EntityResultSet<SceneObject> wall = SceneObjectQuery.newQuery().id(65079).option("Cross").results();

        // Check if the player is already past the wall
        if (AFTER_WALL.contains(player)) {
            logBlue("Player is past the wall, continuing to the next state.");
            setBotState(AbyssState.MAGE); // Set state to MAGE if player is past the wall
            return; // Exit to handle the new state in the next call
        }

        // Check if the player is at the starting area (EDGEVILLE)
        if (EDGEVILLE.contains(player)) {
            if (!player.isMoving()) { // Check if player is not moving
                if (!wall.isEmpty() && !player.getCoordinate().equals(new Coordinate(3092, 3520, 0))) {
                    logSuccess("Interacting with Wall: " + wall.nearest().interact("Cross"));
                    Execution.delay(random.nextLong(500, 1000));
                    return; // Exit after attempting interaction
                } else {
                    logLime("Wall is empty | Player is at the wall");
                    return; // Exit to retry finding the wall in the next call
                }
            } else {
                logLime("Player is moving");
                return; // Exit if the player is moving, to retry in the next cycle
            }
        }

        // Check if the player is in the FIRST_SURGE area
    }

    private void interactWithMage() {
        EntityResultSet<Npc> mage = NpcQuery.newQuery().name("Mage of Zamorak").option("Talk-to").results();
        if (AFTER_WALL.contains(player)) {
            logBlue("Moving to Mage");
            Movement.walkTo(3102, 3555, true);
            Execution.delayUntil(15000, () -> FIRST_SURGE.contains(player));
        }
        if (FIRST_SURGE.contains(player)) {
            if (ActionBar.getCooldown("Surge") == 0) {
                logSuccess("Surging to the mage: " + ActionBar.useAbility("Surge"));
                Movement.walkTo(3102, 3555, true);
                Execution.delayUntil(15000, () -> SECOND_SURGE.contains(player));
            }
        }
        if (SECOND_SURGE.contains(player)) {
            if (ActionBar.getCooldown("Surge") == 0) {
                logSuccess("Surging to the mage: " + ActionBar.useAbility("Surge"));
                Movement.walkTo(3102, 3555, true);
                Execution.delay(random.nextLong(600, 750));
            }
        }
        if (!mage.isEmpty()) {
            logSuccess("Interacting with Mage: " + mage.nearest().interact("Teleport"));
            Execution.delayUntil(15000, () -> RIFT_AREA.contains(player));
            setBotState(AbyssState.RIFT);
        }
    }


    private void interactWithRift() {
        if (player.isMoving()) {
            return;
        }

        String riftName = getRiftName();
        EntityResultSet<SceneObject> rift = SceneObjectQuery.newQuery().name(riftName).option("Exit-through").results();

        if (RIFT_AREA.contains(player)) {
            if (!rift.isEmpty()) {
                SceneObject nearestRift = rift.nearest();

                boolean interactionSuccessful = Execution.delayUntil(() -> {
                    if (nearestRift.interact("Exit-through")) {
                        logLime("Interacted with: " + riftName);
                        return Execution.delayUntil(() -> player.isMoving(), () -> false, random.nextLong(200, 400));
                    }
                    logOrange("Interaction failed, retrying...");
                    return false;
                }, () -> false, 300);
            }
        }

        Area altarArea = getAltarArea();
        if (altarArea.contains(player)) {
            setBotState(AbyssState.ALTAR);
        }
    }


    private static boolean hasCheckedRunePrice = false;
    private static boolean hasCheckedThread = false;


    public void interactWithAltar() {
        EntityResultSet<SceneObject> altar = SceneObjectQuery.newQuery().name(getAltarName()).option("Craft-rune").results();

        if (Backpack.contains(7936)) {
            if (!altar.isEmpty()) {
                powerburst();
                boolean interactionResult = altar.nearest().interact("Craft-rune");
                logSuccess("Interacting with Altar: " + interactionResult);
                Execution.delayUntil(15000, () -> !Backpack.contains(7936));
                loopCounter++;
            }
        } else {
            //[Original]: DoAction(COMPONENT, 10, 6, 96534533)
            String runeName = getAltarName().replace(" altar", " rune");

            if (!hasCheckedRunePrice) {
                logOrange("Checking for " + runeName + " price.");
                ResultSet<Item> runes = InventoryItemQuery.newQuery(93).name(runeName).results();
                if (!runes.isEmpty()) {
                    Item rune = runes.first();
                    int runeSlot = rune.getSlot();
                    logLime(runeName + " found in slot: " + runeSlot + ", item name: " + rune.getName());
                    component(10, runeSlot, 96534533);
                    hasCheckedRunePrice = true;
                } else {
                    logError(runeName + " not found in inventory.");
                }
            }

            if (!hasCheckedThread && Backpack.contains("Magical thread")) {
                logOrange("Checking for Magical thread price.");
                ResultSet<Item> magicalThreads = InventoryItemQuery.newQuery(93).name("Magical thread").results();
                if (!magicalThreads.isEmpty()) {
                    Item magicalThread = magicalThreads.first();
                    int magicalThreadSlot = magicalThread.getSlot();
                    logLime("Magical thread found in slot: " + magicalThreadSlot + ", item name: " + magicalThread.getName());
                    component(10, magicalThreadSlot, 96534533);
                    hasCheckedThread = true;
                } else {
                    logError("Magical thread not found in inventory.");
                }
            }
            setBotState(AbyssState.TELEPORTING);
        }
    }


    private void handleTeleporting() {

        if (!isGamesNecklaceEquipped()) {
            logError("Games necklace not equipped. Shutting down.");
            shutdown();
            return;
        }

        logSuccess("Games necklace equipped. Attempting to interact with teleport.");

        if (!attemptTeleportInteraction()) {
            logError("Failed to interact with teleport.");
            return;
        }

        if (!waitForAnimationStart(9603, 1000)) {
            logPink("Player did not start teleport animation.");
            return;
        }

        logBlue("Player animation ID is 9603. Waiting to reach BANK_AREA.");

        if (Execution.delayUntil(10000, () -> BANK_AREA.contains(LocalPlayer.LOCAL_PLAYER))) {
            logGrey("Reached BANK_AREA. Setting state to BANKING.");
            setBotState(AbyssState.BANKING);
        } else {
            logOrange("Failed to reach BANK_AREA.");
        }
    }

    private boolean isGamesNecklaceEquipped() {
        return !InventoryItemQuery.newQuery(94).name("Games", String::contains).results().isEmpty();
    }

    private boolean attemptTeleportInteraction() {
        boolean interacted = MiniMenu.interact(ComponentAction.COMPONENT.getType(), 5, 2, 95944719);
        if (interacted) {
            logGrey("Interacted with teleport. Waiting for player animation.");
            Execution.delay(random.nextLong(500, 1000));  // Small delay between interaction attempts
        }
        return interacted;
    }

    private boolean waitForAnimationStart(int animationId, long timeoutMillis) {
        return Execution.delayUntil(timeoutMillis, () -> player.getAnimationId() == animationId);
    }

    private void determineState() {
        logGrey("Determining initial state based on player location...");

        // Get the player's current coordinate
        Coordinate playerPosition = player.getCoordinate();
        Area altarArea = getAltarArea();

        // Determine the initial state based on the player's location
        if (BANK_AREA.contains(playerPosition) && !Backpack.contains(7936)) {
            logBlue("Player is in BANK_AREA. Setting state to BANKING.");
            setBotState(AbyssState.BANKING);
        } else if (BANK_AREA.contains(playerPosition) && Backpack.contains(7936)) {
            logBlue("Player is in BANK_AREA with Pure Essence. Setting state to WALL.");
            setBotState(AbyssState.EDGEVILLE);
        } else if (EDGEVILLE.contains(playerPosition) && Backpack.contains(7936)) {
            logBlue("Player is in EDGEVILLE. Setting state to WALL.");
            setBotState(AbyssState.WALL);
        } else if (WILDERNESS.contains(playerPosition) && Backpack.contains(7936)) {
            logBlue("Player is past the wall. Setting state to MAGE.");
            setBotState(AbyssState.MAGE);
        } else if (RIFT_AREA.contains(playerPosition) && Backpack.contains(7936)) {
            logBlue("Player is in RIFT_AREA. Setting state to RIFT.");
            setBotState(AbyssState.RIFT);
        } else if (altarArea.contains(playerPosition) && Backpack.contains(7936)) {
            logBlue("Player is in ALTAR_AREA. Setting state to ALTAR.");
            setBotState(AbyssState.ALTAR);
        } else {
            logOrange("Player is in an unknown location. Setting state to TELEPORTING.");
            setBotState(AbyssState.TELEPORTING);
        }
    }

    private void powerburst() {
        VarManager.getVarbitValue(45519);

        if (VarManager.getVarbitValue(45519) == 0) {
            ResultSet<Item> potionItems = InventoryItemQuery.newQuery(93).option("Drink").results();
            Item potion = potionItems.isEmpty() ? null : potionItems.first();

            if (potion != null) {
                boolean drinkSuccess = Backpack.interact(potion.getName(), "Drink");

                if (drinkSuccess) {
                    logSuccess("Successfully drank " + potion.getName());
                    Execution.delay(random.nextLong(200, 400));
                }
            }
        }
    }
}