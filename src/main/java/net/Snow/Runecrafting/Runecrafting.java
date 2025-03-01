package net.Snow.Runecrafting;

import net.Snow.AbstractScript;
import net.Snow.AbyssRunecrafting.Familiar;
import net.Snow.Direction;
import net.Snow.ImGui.SnowsTemplateGraphics;
import net.Snow.Snow;
import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.api.game.hud.inventories.Equipment;
import net.botwithus.rs3.events.impl.ChatMessageEvent;
import net.botwithus.rs3.events.impl.InventoryUpdateEvent;
import net.botwithus.rs3.game.Client;
import net.botwithus.rs3.game.Coordinate;
import net.botwithus.rs3.game.Item;
import net.botwithus.rs3.game.actionbar.ActionBar;
import net.botwithus.rs3.game.hud.interfaces.Interfaces;
import net.botwithus.rs3.game.minimenu.MiniMenu;
import net.botwithus.rs3.game.minimenu.actions.ComponentAction;
import net.botwithus.rs3.game.queries.builders.items.InventoryItemQuery;
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery;
import net.botwithus.rs3.game.queries.results.ResultSet;
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
import static net.Snow.ImGui.CustomLogger.*;
import static net.Snow.ImGui.Experience.inventoryMap;
import static net.Snow.ImGui.Experience.startTracking;
import static net.Snow.ImGui.SnowsTemplateGraphics.scriptStartTime;
import static net.Snow.LevelLogout.CheckLevel.checkLevelForSkill;
import static net.Snow.Runecrafting.AutoChange.*;
import static net.Snow.Runecrafting.PriceChecker.*;
import static net.Snow.Runecrafting.RunecraftingGraphics.*;
import static net.Snow.Runecrafting.SoulAltar.useSoulAltar;
import static net.Snow.Runecrafting.Variables.*;
import static net.Snow.Snow.random;
import static net.Snow.Snow.*;

public class Runecrafting implements AbstractScript {

    Snow script;

    LocalPlayer player = Client.getLocalPlayer();


    @Override
    public void initialize() {
        startingRunecraftingXP.set(Skills.RUNECRAFTING.getSkill().getExperience());
        startingRunecraftingLevel.set(Skills.RUNECRAFTING.getSkill().getLevel());
        startTracking();
        Variables.loopCounter = 0; // Initialize loop counter

    }

    public AtomicInteger startingRunecraftingXP = new AtomicInteger(0);
    public AtomicInteger startingRunecraftingLevel = new AtomicInteger(0);


    @Override
    public void draw(SnowsTemplateGraphics sgc) {
        RunecraftingGraphics.draw(sgc, script, startingRunecraftingLevel, startingRunecraftingXP, inventoryMap, itemPrices, fleshAltar, miasmaAltar, spiritAltar, boneAltar, scriptStartTime);
    }

    @Override
    public void onInventoryUpdate(InventoryUpdateEvent event) {
        RunecraftingUpdates.onInventoryUpdate(event, inventoryMap);
    }

    private final Map<String, Integer> itemPrices = new ConcurrentHashMap<>(); // The item prices map


    @Override
    public void onChatMessageEvent(ChatMessageEvent event) {
        RunecraftingUpdates.onChatMessageEvent(event, inventoryMap, itemPrices, fleshAltar, miasmaAltar, spiritAltar, boneAltar);
    }

    @Override
    public int getLoopDelay() {
        return 100;
    }



    public static Runestate getBotState() {
        return botstate;
    }

    public static void setBotState(Runestate newState) {
        botstate = newState;
        lastStateChangeTime = Instant.now();
        logGrey("Bot state changed to: " + newState + " at " + lastStateChangeTime);
    }

    private boolean hasDeterminedStart = false;

    public void run(Snow script) {
        checkLevelForSkill(Skills.RUNECRAFTING.getSkill());

        if (soulAltar) {
            useSoulAltar();
            return;
        }

        if (!hasDeterminedStart) {
            determineStart();
            hasDeterminedStart = true;
        }
        selectRune();
        autoChange();

        // Attempt world hop if needed
        WorldHop.worldHop();

        if (WorldHop.isWorldHopInProgress()) {
            Execution.delay(1000);  // Wait 1 second before checking again
            return;
        }

        // Continue with the rest of the script after world hop is done
        switch (getBotState()) {
            case BANKING:
                banking();
                return;
            case PASSINGBRACELET:
                passingbracelet();
                return;
            case DARKPORTAL:
                darkportal();
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


    private void banking() {
        logGrey("Entering banking state.");
        if (useFamiliar) {
            Familiar.checkFamiliar();
        }
        SceneObject bank = DEAD_MANS_CHEST.results().nearest();
        if (bank != null) {
            logGrey("Bank object found. Attempting to interact.");
            if (bank.interact("Load Last Preset from")) {
                logSuccess("Interacted with bank object. Waiting for Backpack to contain Impure essence.");
                Execution.delayUntil(10000, () -> Backpack.contains(55667));
                if (Backpack.contains(55667)) {
                    logOrange("Backpack contains Impure essence. Checking for Games necklace.");
                    if (!(InventoryItemQuery.newQuery(94).name("Games", String::contains)).results().isEmpty()) {
                        logSuccess("Games necklace equipped. Setting state to PASSINGBRACELET.");
                        setBotState(Runestate.PASSINGBRACELET);
                    } else {
                        logError("Games necklace not equipped.");
                        shutdown();
                    }
                } else {
                    logError("Backpack does not contain Impure essence. Shutting down.");
                    shutdown();
                }
            } else {
                logError("Failed to interact with bank object.");
            }
        }

        logGrey("Exiting banking state.");
    }

    private void passingbracelet() {
        if (Equipment.contains("Passing bracelet")) {
            logOrange("Passing bracelet equipped. Attempting to interact with bracelet.");
            if (MiniMenu.interact(ComponentAction.COMPONENT.getType(), 4, 9, 95944719)) {
                logSuccess("Interacted with bracelet. Waiting to reach HAUNTED_HILL_AREA.");
                Execution.delayUntil(10000, () -> HAUNTED_HILL_AREA.contains(player));
                logGrey("Reached HAUNTED_HILL_AREA. Setting state to DARKPORTAL.");
                setBotState(Runestate.DARKPORTAL);
            } else {
                logError("Failed to interact with bracelet.");
            }
        } else {
            if (Backpack.contains("Passing bracelet")) {
                logOrange("Passing bracelet found in backpack. Teleporting normal.");
                //[Original]: DoAction(COMPONENT, 7, -1, 93716713)
                ActionBar.useItem("Passing bracelet", "Rub");
                Execution.delayUntil(10000, () -> Interfaces.isOpen(720));
                if (Interfaces.isOpen(720)) {
                    //[Original]: DoAction(DIALOGUE, 0, -1, 47185940)
                    dialog(0, -1, 47185940);
                    logSuccess("Interacted with bracelet. Waiting to reach HAUNTED_HILL_AREA.");
                    Execution.delayUntil(10000, () -> HAUNTED_HILL_AREA.contains(player));
                    logGrey("Reached HAUNTED_HILL_AREA. Setting state to DARKPORTAL.");
                    setBotState(Runestate.DARKPORTAL);
                }
            }
        }


        logGrey("Exiting passingbracelet state.");
    }


    private void darkportal() {
        if (!HAUNTED_HILL_AREA.contains(Client.getLocalPlayer())) {
            logError("Player is not in the Haunted Hill area.");
            return;
        }

        SceneObject portal = DARK_PORTALS.results().nearest();
        if (portal == null) {
            logError("No portal found in the area.");
            return;
        }


        if (!player.isMoving()) {
            logBlue("Attempting to interact with the portal.");
            boolean interactionSuccess = portal.interact("Enter");
            if (interactionSuccess) {
                logSuccess("Interacted with portal. Checking if player starts moving.");
                Execution.delayUntil(5000, () -> player.isMoving());
            } else {
                logError("Failed to interact with portal.");
            }
        }

        if (isFacingPortal(portal)) {
            if (ActionBar.getCooldown("Surge") == 0) {
                if (ActionBar.useAbility("Surge")) {
                    logSuccess("Used Surge ability.");
                    portal.interact("Enter");
                }
            }

            Execution.delayUntil(10000, () -> ALTAR_AREA.contains(LocalPlayer.LOCAL_PLAYER));
            setBotState(Runestate.ALTAR);
        }
    }


    private static boolean hasChecked = false;

    private void interactWithAltar() {
        if (!ALTAR_AREA.contains(Client.getLocalPlayer())) {
            logError("Player is not in the Altar area.");
            return;
        }

        SceneObjectQuery altarQuery = determineAltarQuery();
        if (altarQuery == null) {
            logError("No altar type selected.");
            return;
        }

        Execution.delayUntil(10000, () -> !altarQuery.results().isEmpty());

        SceneObject altar = altarQuery.results().nearest();
        if (altar != null) {
            logBlue("Altar found. Attempting to interact.");
            powerburst();
            if (altar.interact("Craft runes")) {
//                Execution.delayUntil(2000, () -> isFacingAltar(altar));

                if (ActionBar.getCooldown("Surge") == 0) {
                    Execution.delay(random.nextLong(600, 625));
                    if (ActionBar.useAbility("Surge")) {
                        logSuccess("Used Surge ability.");
                        altar.interact("Craft runes");
                    }
                }
                logBlue("Interacted with altar. Waiting for runes to be crafted.");
                Execution.delayUntil(10000, () -> !Backpack.contains(55667));
                if (!Backpack.contains(55667)) {
                    loopCounter++;
                    setBotState(Runestate.TELEPORTING);
                }
            } else {
                logError("Failed to interact with altar.");
            }
        } else {
            logError("Altar not found.");
        }
    }


    private void handleTeleporting() {
        checkRunePrice();

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

        if (!waitForAnimationStart(9603, 600)) {
            logError("Player did not start teleport animation.");
            return;
        }

        logBlue("Player animation ID is 9603. Waiting to reach BANK_AREA.");

        if (Execution.delayUntil(10000, () -> BANK_AREA.contains(LocalPlayer.LOCAL_PLAYER))) {
            logGrey("Reached BANK_AREA. Setting state to BANKING.");
            setBotState(Runestate.BANKING);
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
            Execution.delay(random.nextLong(300, 500));  // Small delay between interaction attempts
        }
        return interacted;
    }

    private boolean waitForAnimationStart(int animationId, long timeoutMillis) {
        return Execution.delayUntil(timeoutMillis, () -> player.getAnimationId() == animationId);
    }

    //Execution.delayUntil(() -> BANK_AREA.contains(player), () -> player.getAnimationId() == 9603, 500);


    public static SceneObjectQuery determineAltarQuery() {
        if (autoLevelling) {
            int runecraftingLevel = Skills.RUNECRAFTING.getSkill().getLevel();
            if (runecraftingLevel < 20) {
                AutoChange.currentRuneType = "Spirit";
                nextRuneType = "Bone"; // Set the next rune type after spirit
                return SPIRIT_ALTARS;
            } else if (runecraftingLevel < 40) {
                AutoChange.currentRuneType = "Bone";
                nextRuneType = "Flesh"; // Set the next rune type after bone
                return BONE_ALTARS;
            } else if (runecraftingLevel < 60) {
                AutoChange.currentRuneType = "Flesh";
                nextRuneType = "Miasma"; // Set the next rune type after flesh
                return FLESH_ALTARS;
            } else {
                AutoChange.currentRuneType = "Miasma";
                nextRuneType = "None"; // No next rune type, as Miasma is the highest
                return MIASMA_ALTARS;
            }
        } else {
            if (fleshAltar) {
                AutoChange.currentRuneType = "Flesh";
                nextRuneType = "Miasma"; // Set the next rune type after flesh
                return FLESH_ALTARS;
            } else if (miasmaAltar) {
                AutoChange.currentRuneType = "Miasma";
                nextRuneType = "None"; // No next rune type, as Miasma is the highest
                return MIASMA_ALTARS;
            } else if (spiritAltar) {
                AutoChange.currentRuneType = "Spirit";
                nextRuneType = "Bone"; // Set the next rune type after spirit
                return SPIRIT_ALTARS;
            } else if (boneAltar) {
                AutoChange.currentRuneType = "Bone";
                nextRuneType = "Flesh"; // Set the next rune type after bone
                return BONE_ALTARS;
            } else {
                return null;
            }
        }
    }

    private boolean isFacingAltar(SceneObject altar) {
        Coordinate playerPosition = Client.getLocalPlayer().getCoordinate();
        Direction playerDirection = Direction.of(Client.getLocalPlayer());
        Direction altarDirection = Direction.of(playerPosition, altar.getCoordinate());

        if (playerDirection == altarDirection) {
            logSuccess("Player is currently facing the altar.");
            return true;
        } else {
            return false;
        }
    }

    private boolean isFacingPortal(SceneObject portal) {
        Coordinate playerPosition = Client.getLocalPlayer().getCoordinate();
        Direction playerDirection = Direction.of(Client.getLocalPlayer());
        Direction portalDirection = Direction.of(playerPosition, portal.getCoordinate());

        if (playerDirection == portalDirection) {
            logSuccess("Player is currently facing the portal.");
            return true;
        } else {
            return false;
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

    private void checkRunePrice() {
        if (!hasChecked) {
            logOrange("Crafted runes. Checking rune prices.");
            Execution.delay(random.nextLong(1500, 2000));
            if (fleshAltar && Backpack.contains("Flesh rune")) {
                fleshRunePrice();
                hasChecked = true;
            }
            if (miasmaAltar && Backpack.contains("Miasma rune")) {
                miasmaRunePrice();
                hasChecked = true;
            }
            if (spiritAltar && Backpack.contains("Spirit rune")) {
                spiritRunePrice();
                hasChecked = true;
            }
            if (boneAltar && Backpack.contains("Bone rune")) {
                boneRunePrice();
                hasChecked = true;
            }
            logOrange("Checked rune prices.");
            Execution.delay(random.nextLong(1500, 2000));
        }
    }
}
