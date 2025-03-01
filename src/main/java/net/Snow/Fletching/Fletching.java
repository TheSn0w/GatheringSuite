package net.Snow.Fletching;

import net.Snow.AbstractScript;
import net.Snow.ImGui.SnowsTemplateGraphics;
import net.Snow.Snow;
import net.Snow.Summoning.SummoningUpdates;
import net.botwithus.api.game.hud.Dialog;
import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.rs3.events.impl.ChatMessageEvent;
import net.botwithus.rs3.events.impl.InventoryUpdateEvent;
import net.botwithus.rs3.game.Item;
import net.botwithus.rs3.game.hud.interfaces.Interfaces;
import net.botwithus.rs3.game.queries.builders.characters.NpcQuery;
import net.botwithus.rs3.game.queries.builders.items.InventoryItemQuery;
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery;
import net.botwithus.rs3.game.queries.results.ResultSet;
import net.botwithus.rs3.game.scene.entities.characters.npc.Npc;
import net.botwithus.rs3.game.scene.entities.object.SceneObject;
import net.botwithus.rs3.game.skills.Skills;
import net.botwithus.rs3.script.Execution;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static net.Snow.Combat.Combat.player;
import static net.Snow.ImGui.CustomLogger.*;
import static net.Snow.ImGui.Experience.inventoryMap;
import static net.Snow.ImGui.Experience.startTracking;
import static net.Snow.Snow.*;

public class Fletching  implements AbstractScript {

    static Snow script;

    @Override
    public void initialize() {
        startingFletchingXP.set(Skills.FLETCHING.getSkill().getExperience());
        startingFletchingLevel.set(Skills.FLETCHING.getSkill().getActualLevel());
        startTracking();

    }

    public AtomicInteger startingFletchingXP = new AtomicInteger(0);
    public AtomicInteger startingFletchingLevel = new AtomicInteger(0);


    @Override
    public void draw(SnowsTemplateGraphics sgc) throws IOException {
        FletchingGraphics.draw(sgc, script, startingFletchingLevel, startingFletchingXP);
    }

    @Override
    public void onInventoryUpdate(InventoryUpdateEvent event) {
        FletchingUpdates.onInventoryUpdate(event, inventoryMap);
    }

    @Override
    public void onChatMessageEvent(ChatMessageEvent event) {
        SummoningUpdates.onChatMessageEvent(event, inventoryMap);
    }

    @Override
    public int getLoopDelay() {
        return random.nextInt(300, 600);
    }




    @Override
    public void run(Snow script) {

        capturestuff();

        if (FletchingTypes.getSelectedFletching().equals("Disabled")) {
            logError("Fletching is disabled; no action will be performed.");
            return;
        }

        if (Interfaces.isOpen(1251)) {
            return;
        }

        if (Interfaces.isOpen(1370)) {
            selectInterface1370();
            return;
        }

        if (Interfaces.isOpen(1179)) {
            dialog(0, -1, 77266961);
            logSuccess("Dialogue option selected.");
            Execution.delayUntil(30000, () -> Interfaces.isOpen(1370));
            return;
        }

        // Perform actions based on the selected fletching type
        switch (FletchingTypes.getSelectedFletching()) {
            case "Unstrung bows":
                createUnstrungBows();
                return;
            case "String bows":
                createStrungBows();
                return;
            case "Ammo":
                createAmmo();
                return;
            default:
                return;
        }
    }

    private static void createStrungBows() {
        // Configure the Portable Fletcher to "String" if needed
        configurePortableFletcher("String", "String");

        if (Backpack.containsItemByCategory(3752)) {
            interactWithFletchingObjectOrBackpack("String", "String", 3752);
        } else {
            interactWithPresetLoader(3752);
        }
    }


    private static void createUnstrungBows() {
        // Configure the Portable Fletcher to "Fletch" if needed
        configurePortableFletcher("Fletch", "Fletch");

        if (Backpack.containsItemByCategory(22)) {
            interactWithFletchingObjectOrBackpack("Fletch", "Craft", 22);
        } else {
            interactWithPresetLoader(22);
        }
    }


    public static void createAmmo() {
        // Configure the Portable Fletcher to "Ammo" if needed
        configurePortableFletcher("Ammo", "Ammo");

        // Query the inventory for items with the options "Feather", "Flight", or "Tip"
        ResultSet<Item> ammoItems = InventoryItemQuery.newQuery(93)
                .option("Feather", "'Flight'", "Tip")
                .results();

        // Proceed with the interaction logic if any such items are found
        if (!ammoItems.isEmpty()) {
            // First, handle items with "Feather" and "Flight" options
            for (Item item : ammoItems) {
                String interactionOption = getInteractionOption(item);
                if (interactionOption.equals("Feather") || interactionOption.equals("'Flight'")) {
                    logBlue("Found item: " + item.getName() + " with option '" + interactionOption + "'.");
                    interactWithAmmoItem(item, interactionOption);
                    return; // Exit the method after the first item is processed
                }
            }

            // Then, handle items with the "Tip" option
            for (Item item : ammoItems) {
                String interactionOption = getInteractionOption(item);
                if (interactionOption.equals("Tip")) {
                    logBlue("Found item: " + item.getName() + " with option 'Tip'.");
                    interactWithAmmoItem(item, interactionOption);
                    return; // Exit the method after the first item is processed
                }
            }
        } else {
            logError("No items with 'Feather', 'Flight', or 'Tip' options found in the backpack. Shutting down script.");
            shutdown(); // Shut down the script when no more items are available
        }
    }


    private static void interactWithAmmoItem(Item item, String interactionOption) {
        // Try interacting with the Portable Fletcher or Fletching Workbench first
        interactWithFletchingObjectOrBackpack("Ammo", interactionOption, -1);

        // If neither the Portable Fletcher nor the Workbench is available, interact directly with the item
        if (Backpack.interact(item.getName(), interactionOption)) {
            logSuccess("Interacted with item: " + item.getName() + " using '" + interactionOption + "' option.");
            if (Execution.delayUntil(30000, () -> Interfaces.isOpen(1370) || Interfaces.isOpen(1179))) {
                logSuccess("Interface opened successfully after interacting with " + item.getName());
                Execution.delay(random.nextInt(500, 700)); // Delay to prevent rapid clicking
            } else {
                logError("Interface did not open after interacting with " + item.getName());
            }
        } else {
            logError("Failed to interact with item: " + item.getName() + " using '" + interactionOption + "' option.");
        }
    }



    private static String getInteractionOption(Item item) {
        // Determine the correct interaction option based on available options
        if (item.getConfigType().getBackpackOptions().contains("Feather")) {
            return "Feather";
        } else if (item.getConfigType().getBackpackOptions().contains("'Flight'")) {
            return "'Flight'";
        } else if (item.getConfigType().getBackpackOptions().contains("Tip")) {
            return "Tip";
        } else {
            return "Craft"; // Default option if none of the above is available
        }
    }

    private static long lastPortableFletcherInteractionTime = 0;
    private static final long FIVE_MINUTES = 300000; // 5 minutes in milliseconds

    private static void interactWithFletchingObjectOrBackpack(String portableFletcherOption, String backpackOption, int itemCategory) {

        SceneObject portableFletcher = SceneObjectQuery.newQuery()
                .name("Portable fletcher")
                .option(portableFletcherOption)
                .results()
                .nearestTo(player.getCoordinate());

        // Calculate remaining cooldown time for the Portable Fletcher
        long timeSinceLastInteraction = System.currentTimeMillis() - lastPortableFletcherInteractionTime;
        long timeRemaining = FIVE_MINUTES - timeSinceLastInteraction;

        // Interact with the Portable Fletcher if it's available, within a distance of 7, and if 5 minutes have passed since the last interaction
        if (portableFletcher != null && portableFletcher.distanceTo(player.getCoordinate()) <= 7) {
            if (timeSinceLastInteraction > FIVE_MINUTES) {
                if (portableFletcher.interact(portableFletcherOption)) {
                    lastPortableFletcherInteractionTime = System.currentTimeMillis();
                    logSuccess("Interacted with Portable Fletcher using '" + portableFletcherOption + "' option.");
                    Execution.delayUntil(30000, () -> Interfaces.isOpen(1370) || Interfaces.isOpen(1179));
                    return;
                } else {
                    logError("Failed to interact with Portable Fletcher using '" + portableFletcherOption + "' option.");
                    return;
                }
            } else {
                logBlue("Skipping Portable Fletcher interaction; cooldown active. Time remaining: " + timeRemaining / 1000 + " seconds.");
            }
        }

        // If Portable Fletcher is not available or is on cooldown, check for Fletching Workbench
        SceneObject fletchingWorkbench = SceneObjectQuery.newQuery()
                .name("Fletching workbench")
                .option("Use")
                .results()
                .nearestTo(player.getCoordinate());

        if (fletchingWorkbench != null && fletchingWorkbench.distanceTo(player.getCoordinate()) <= 7) {
            if (fletchingWorkbench.interact("Use")) {
                logSuccess("Interacted with Fletching Workbench.");
                Execution.delayUntil(30000, () -> Interfaces.isOpen(1370) || Interfaces.isOpen(1179));
                return;
            } else {
                logError("Failed to interact with Fletching Workbench.");
                return;
            }
        }

        // If no Portable Fletcher or Fletching Workbench is available or the Portable Fletcher is on cooldown, interact with items in the backpack
        ResultSet<Item> results = InventoryItemQuery.newQuery(93).option(backpackOption).results();
        for (Item item : results) {
            String itemName = item.getName();
            logBlue("Attempting to interact with item: " + itemName);
            if (Backpack.interact(itemName, backpackOption)) {
                logSuccess("Interacted with item: " + itemName + " using '" + backpackOption + "' option.");
                if (Execution.delayUntil(30000, () -> Interfaces.isOpen(1370) || Interfaces.isOpen(1179))) {
                    logSuccess("Interface opened successfully after interacting with " + itemName);
                    return;
                } else {
                    logError("Interface did not open after interacting with " + itemName);
                    return;
                }
            } else {
                logError("Failed to interact with item: " + itemName + " using '" + backpackOption + "' option.");
                return;
            }
        }
    }



    private static void configurePortableFletcher(String expectedOption, String dialogOption) {
        // Query for the nearest Portable Fletcher
        SceneObject portableFletcher = SceneObjectQuery.newQuery()
                .name("Portable fletcher")
                .results()
                .nearestTo(player.getCoordinate());

        if (portableFletcher != null && portableFletcher.distanceTo(player.getCoordinate()) <= 7) {
            // Get the available options for the Portable Fletcher
            List<String> options = portableFletcher.getOptions();
            logGrey("Available options on Portable Fletcher: " + String.join(", ", options));

            // Check if the first option is the expected one
            if (!options.isEmpty() && !options.get(0).equalsIgnoreCase(expectedOption)) {
                logWarning("The first option on the Portable Fletcher is not '" + expectedOption + "'. Configuring Portable Fletcher...");

                // Interact with the Portable Fletcher to configure it
                if (portableFletcher.interact("Configure")) {
                    logSuccess("Interacted with Portable Fletcher to configure.");

                    // Wait for the configuration interface to open
                    if (Execution.delayUntil(30000, () -> Interfaces.isOpen(1188))) {
                        logSuccess("Configuration interface 1188 opened.");

                        // Interact with the dialog to select the appropriate option
                        if (Dialog.interact(dialogOption)) {
                            logSuccess("Selected '" + dialogOption + "' in the configuration dialog.");
                            Execution.delay(random.nextInt(1000, 2000)); // Delay to prevent rapid clicking
                        } else {
                            logError("Failed to select '" + dialogOption + "' in the configuration dialog.");
                        }
                    } else {
                        logError("Failed to open configuration interface 1188.");
                    }
                } else {
                    logError("Failed to interact with Portable Fletcher to configure.");
                }
            } else {
                logGrey("The first option on the Portable Fletcher is already set to '" + expectedOption + "'. No need to configure.");
            }
        } else {
            logGrey("No Portable Fletcher found within distance.");
        }
    }

    private static void interactWithPresetLoader(int requiredItemCategory) {
        // Search for a SceneObject with the option "Load Last Preset from"
        SceneObject bankObject = SceneObjectQuery.newQuery()
                .option("Load Last Preset from")
                .results()
                .nearest();

        if (bankObject != null) {
            logBlue("Interacting with: " + bankObject.getName());
            if (bankObject.interact("Load Last Preset from")) {
                logSuccess("Successfully interacted with: " + bankObject.getName() + " to load last preset.");
                Execution.delayUntil(30000, () -> Backpack.containsItemByCategory(requiredItemCategory));
                checkItemsInBackpack(requiredItemCategory);
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
                Execution.delayUntil(30000, () -> Backpack.containsItemByCategory(requiredItemCategory));
                checkItemsInBackpack(requiredItemCategory);
                return;
            } else {
                logWarning("Failed to interact with: " + bankNpc.getName() + " to load last preset.");
                return;
            }
        }

        logError("No NPC or SceneObject with 'Load Last Preset from' option found.");
    }



    private static void checkItemsInBackpack(int requiredItemCategory) {
        // Check if the backpack contains the required items
        if (Backpack.containsItemByCategory(requiredItemCategory)) {
            logSuccess("Required items found in the backpack.");
        } else {
            logError("Required items not found in the backpack after loading preset. Shutting down script.");
            shutdown(); // Shutdown the script if items are not found
        }
    }

}
