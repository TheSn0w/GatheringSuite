package net.Snow.Crafting;

import net.Snow.AbstractScript;
import net.Snow.ImGui.SnowsTemplateGraphics;
import net.Snow.Snow;
import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.rs3.events.impl.ChatMessageEvent;
import net.botwithus.rs3.events.impl.InventoryUpdateEvent;
import net.botwithus.rs3.game.Item;
import net.botwithus.rs3.game.actionbar.ActionBar;
import net.botwithus.rs3.game.hud.interfaces.Interfaces;
import net.botwithus.rs3.game.queries.builders.characters.NpcQuery;
import net.botwithus.rs3.game.queries.builders.items.InventoryItemQuery;
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery;
import net.botwithus.rs3.game.queries.results.EntityResultSet;
import net.botwithus.rs3.game.queries.results.ResultSet;
import net.botwithus.rs3.game.scene.entities.characters.npc.Npc;
import net.botwithus.rs3.game.scene.entities.object.SceneObject;
import net.botwithus.rs3.game.skills.Skills;
import net.botwithus.rs3.script.Execution;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

import static net.Snow.Combat.Combat.player;
import static net.Snow.ImGui.CustomLogger.*;
import static net.Snow.ImGui.Experience.inventoryMap;
import static net.Snow.ImGui.Experience.startTracking;
import static net.Snow.Snow.*;

public class Crafting implements AbstractScript {

    static Snow script;

    @Override
    public void initialize() {
        startingCraftingXP.set(Skills.CRAFTING.getSkill().getExperience());
        startingCraftingLevel.set(Skills.CRAFTING.getSkill().getActualLevel());
        startTracking();

    }

    public AtomicInteger startingCraftingXP = new AtomicInteger(0);
    public AtomicInteger startingCraftingLevel = new AtomicInteger(0);


    @Override
    public void draw(SnowsTemplateGraphics sgc) throws IOException {
        CraftingGraphics.draw(sgc, script, startingCraftingLevel, startingCraftingXP);
    }

    @Override
    public void onInventoryUpdate(InventoryUpdateEvent event) {
        CraftingUpdates.onInventoryUpdate(event, inventoryMap);
    }

    @Override
    public void onChatMessageEvent(ChatMessageEvent event) {
        CraftingUpdates.onChatMessageEvent(event, inventoryMap);
    }

    @Override
    public int getLoopDelay() {
        return random.nextInt(100, 300);
    }

    public static boolean formClay = false;
    public static boolean fireClay = false;
    public static boolean fireClayLunar = false;
    public static boolean addRunes = false;
    public static boolean craftGems = false;

    @Override
    public void run(Snow script) {

        // Capture initial setup or state
        capturestuff();



        // Check if neither formClay nor fireClay is selected, log a warning and exit
        if (!formClay && !fireClay && !fireClayLunar && !addRunes && !craftGems) {
            logWarning("No Crafting option selected. Exiting run method.");
            return;
        }

        // Check if interface 1251 is open, log a message and exit
        if (Interfaces.isOpen(1251)) {
            logGrey("Interface 1251 is open. Exiting run method.");
            return;
        }

        // Check if interface 1370 is open, log a message and select the interface
        if (Interfaces.isOpen(1370)) {
            selectInterface1370();
            return;
        }




        // Check if formClay is selected and interface 1188 is open, log a message, interact with the dialog, and wait until interface 1370 is open
        if (formClay && Interfaces.isOpen(1188)) {
            dialog(0, -1, 77856776);
            Execution.delayUntil(30000, () -> Interfaces.isOpen(1370));
            return;
        }
        // Check if fireClay is selected and interface 1188 is open, log a message, interact with the dialog, and wait until interface 1370 is open
        else if (fireClay && Interfaces.isOpen(1188)) {
            dialog(0, -1, 77856781);
            Execution.delayUntil(30000, () -> Interfaces.isOpen(1370));
            return;
        }

        // Check if formClay is selected and there are at least 6 Soft clay in the backpack, log a message and handle portables
        if (formClay && Backpack.getCount("Soft clay") >= 6) {
            logBlue("Soft Clay count is sufficient. Handling portables.");
            HandlePortables();
        }
        // Check if fireClay is selected and there are items in category 2127 (unfinished Urns) in the backpack, log a message and handle portables
        else if (fireClay && containsItemWithPattern(Pattern.compile(".*\\(unf\\).*"))) {
            logBlue("Portables - Urns count is sufficient. Handling portables.");
            HandlePortables();
        }
        // Check if fireClayLunar is selected and there are items with "(nr)" in the name in the backpack, log a message and handle lunar spell
        else if (fireClayLunar && containsItemWithPattern(Pattern.compile(".*\\(unf\\).*"))) {
            logBlue("Lunar Spell Book - Urns count is sufficient. Handling Action Bar.");
            HandleLunarSpell();
        } else if (addRunes && containsItemWithPattern(Pattern.compile(".*\\(nr\\).*"))) {
            interactWithAnyRuneInBackpack();
        } else if (craftGems && containsItemWithPattern(Pattern.compile(".*Uncut.*"))) {
            startCraftingGems();
        }
        else {
            logOrange("Materials are insufficient. Interacting with preset loader.");
            interactWithPresetLoader();
        }
    }

    private static long lastPortableInteractionTime = 0;
    private static final long BUFF_DURATION_MILLIS = 5 * 60 * 1000; // 5 minutes

    private static void startCraftingGems() {
        long currentTime = System.currentTimeMillis();

        // Check if the buff is active
        boolean buffActive = (currentTime - lastPortableInteractionTime) < BUFF_DURATION_MILLIS;

        if (!buffActive) {
            // Check if the player is within 5.0D of the specified SceneObject
            EntityResultSet<SceneObject> results = SceneObjectQuery.newQuery().id(106595).option("Cut Gems").results();
            SceneObject portable = results.nearest();
            if (portable != null) {
                logCyan("Portable found nearby.");
                if (portable.distanceTo(player) <= 5.0D) {
                    logBlue("Player is within 5 Tiles of the portable. Interacting with portable.");
                    if (portable.interact("Cut Gems")) {
                        logSuccess("Successfully interacted with the portable to cut gems.");
                        lastPortableInteractionTime = currentTime; // Update the last interaction time
                        Execution.delayUntil(30000, () -> Interfaces.isOpen(1370));
                        return;
                    } else {
                        logError("Failed to interact with the portable to cut gems.");
                    }
                } else {
                    logPurple("Player is not within 5 Tiles of the portable.");
                }
            } else {
                logPink("No portable found nearby.");
            }
        } else {
            logCyan("Buff is active. Interacting directly with backpack.");
        }

        // Find items with the "Craft" option
        ResultSet<Item> craftingItems = InventoryItemQuery.newQuery(93).option("Craft").results();
        Item craftItem = null;

        // Check if the backpack contains any item with "Uncut" in the name
        if (containsItemWithPattern(Pattern.compile(".*Uncut.*"))) {
            for (Item item : craftingItems) {
                if (item.getName().contains("Uncut")) {
                    craftItem = item;
                    break;
                }
            }

            if (craftItem != null) {
                logBlue("Backpack contains item: " + craftItem.getName());
                if (Backpack.interact(craftItem.getName(), "Craft")) {
                    logSuccess("Successfully interacted with " + craftItem.getName() + " to craft.");
                    Execution.delayUntil(30000, () -> Interfaces.isOpen(1370));
                }
            } else {
                logWarning("No item with 'Uncut' in the name found to craft.");
            }
        }
    }


    private static void interactWithAnyRuneInBackpack() {
        Pattern pattern = Pattern.compile(".*\\(nr\\).*");
        List<Item> items = Backpack.getItems();

        for (Item item : items) {
            if (pattern.matcher(item.getName()).find()) {
                List<String> options = item.getConfigType().getBackpackOptions();
                if (!options.isEmpty()) {
                    String option = options.get(0);
                    logBlue("Interacting with item: " + item.getName() + " using option '" + option + "'.");
                    if (Backpack.interact(item.getName(), option)) {
                        logSuccess("Successfully interacted with item: " + item.getName());
                        Execution.delayUntil(30000, () -> Interfaces.isOpen(1370));
                    } else {
                        logError("Failed to interact with item: " + item.getName());
                    }
                    return; // Exit after interacting with the first matching item
                }
            }
        }
        logWarning("No item with pattern '(nr)' found in backpack.");
    }

    private static boolean containsItemWithPattern(Pattern pattern) {
        List<Item> items = Backpack.getItems();
        for (Item item : items) {
            if (pattern.matcher(item.getName()).find()) {
                logGrey("Item matching pattern found: " + item.getName());
                return true;
            }
        }
        return false;
    }


    private static void HandleLunarSpell() {
        logGrey("Checking if ActionBar contains ability 'Fire Urn'.");
        if (ActionBar.containsAbility("Fire Urn")) {
            ActionBar.useAbility("Fire Urn");
            Execution.delayUntil(30000, () -> Interfaces.isOpen(1370));
        } else {
            logWarning("Ability 'Fire Urn' not found on ActionBar.");
        }
    }


    private static void HandlePortables() {
        // Search for a SceneObject with the option "Craft"
        SceneObject portableObject = SceneObjectQuery.newQuery()
                .option("Clay Crafting")
                .results()
                .nearest();

        if (portableObject != null) {
            logBlue("Interacting with: " + portableObject.getName());
            if (portableObject.interact("Clay Crafting")) {
                logSuccess("Successfully interacted with: " + portableObject.getName() + " to craft.");
                Execution.delayUntil(30000, () -> Interfaces.isOpen(1188));
            } else {
                logError("Failed to interact with: " + portableObject.getName() + " to craft.");
            }
        }
    }


    private static void interactWithPresetLoader() {
        // Search for a SceneObject with the option "Load Last Preset from"
        SceneObject bankObject = SceneObjectQuery.newQuery()
                .option("Load Last Preset from")
                .results()
                .nearest();

        if (bankObject != null) {
            logBlue("Interacting with: " + bankObject.getName());
            if (bankObject.interact("Load Last Preset from")) {
                logSuccess("Successfully interacted with: " + bankObject.getName() + " to load last preset.");
                if (formClay) {
                    Execution.delayUntil(30000, () -> Backpack.getCount("Soft clay") >= 6);
                    if (Backpack.getCount("Soft clay") < 6) {
                        logError("Soft clay not found after 30 seconds. Logging out.");
                        shutdown();
                    }
                } else if (fireClay) {
                    Execution.delayUntil(30000, () -> Backpack.containsItemByCategory(2127));
                    if (!Backpack.containsItemByCategory(2127)) {
                        logError("Urn not found after 30 seconds. Logging out.");
                        shutdown();
                    }
                } else if (fireClayLunar) {
                    Execution.delayUntil(30000, () -> containsItemWithPattern(Pattern.compile(".*\\(unf\\).*")));
                    if (!containsItemWithPattern(Pattern.compile(".*\\(unf\\).*"))) {
                        logError("Items matching pattern '(unf)' not found after 30 seconds. Logging out.");
                        shutdown();
                    }
                } else if (addRunes) {
                    Execution.delayUntil(30000, () -> containsItemWithPattern(Pattern.compile(".*\\(nr\\).*")));
                    if (!containsItemWithPattern(Pattern.compile(".*\\(nr\\).*"))) {
                        logError("Items matching pattern '(nr)' not found after 30 seconds. Logging out.");
                        shutdown();
                    }
                } else if (craftGems) {
                    Execution.delayUntil(30000, () -> containsItemWithPattern(Pattern.compile(".*Uncut.*")));
                    if (!containsItemWithPattern(Pattern.compile(".*Uncut.*"))) {
                        logError("Items matching pattern '(uncut)' not found after 30 seconds. Logging out.");
                        shutdown();
                    }
                }
                return;
            } else {
                logError("Failed to interact with: " + bankObject.getName() + " to load last preset.");
                return;
            }
        }

        // If no SceneObject was found, search for an NPC
        Npc bankNpc = NpcQuery.newQuery()
                .option("Load Last Preset from")
                .results()
                .nearest();

        if (bankNpc != null) {
            logBlue("Interacting with: " + bankNpc.getName());
            if (bankNpc.interact("Load Last Preset from")) {
                logSuccess("Successfully interacted with: " + bankNpc.getName() + " to load last preset.");
                if (formClay) {
                    Execution.delayUntil(30000, () -> Backpack.getCount("Soft clay") >= 6);
                    if (Backpack.getCount("Soft clay") < 6) {
                        logError("Soft clay not found after 30 seconds. Logging out.");
                        shutdown();
                    }
                } else if (fireClay) {
                    Execution.delayUntil(30000, () -> Backpack.containsItemByCategory(2127));
                    if (!Backpack.containsItemByCategory(2127)) {
                        logError("Urn not found after 30 seconds. Logging out.");
                        shutdown();
                    }
                } else if (fireClayLunar) {
                    Execution.delayUntil(30000, () -> containsItemWithPattern(Pattern.compile(".*\\(unf\\).*")));
                    if (!containsItemWithPattern(Pattern.compile(".*\\(unf\\).*"))) {
                        logError("Items matching pattern '(unf)' not found after 30 seconds. Logging out.");
                        shutdown();
                    }
                } else if (addRunes) {
                    Execution.delayUntil(30000, () -> containsItemWithPattern(Pattern.compile(".*\\(nr\\).*")));
                    if (!containsItemWithPattern(Pattern.compile(".*\\(nr\\).*"))) {
                        logError("Items matching pattern '(nr)' not found after 30 seconds. Logging out.");
                        shutdown();
                    }
                } else if (craftGems) {
                Execution.delayUntil(30000, () -> containsItemWithPattern(Pattern.compile(".*Uncut.*")));
                if (!containsItemWithPattern(Pattern.compile(".*Uncut.*"))) {
                    logError("Items matching pattern '(uncut)' not found after 30 seconds. Logging out.");
                    shutdown();
                }
            }

                return;
            } else {
                logWarning("Failed to interact with: " + bankNpc.getName() + " to load last preset.");
                return;
            }
        }

        logError("No NPC or SceneObject with 'Load Last Preset from' option found.");
    }
}
