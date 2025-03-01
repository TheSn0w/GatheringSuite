package net.Snow.Smithing;

import net.Snow.AbstractScript;
import net.Snow.ImGui.SnowsTemplateGraphics;
import net.Snow.Snow;
import net.botwithus.api.game.hud.inventories.Bank;
import net.botwithus.rs3.events.impl.ChatMessageEvent;
import net.botwithus.rs3.events.impl.InventoryUpdateEvent;
import net.botwithus.rs3.game.hud.interfaces.Interfaces;
import net.botwithus.rs3.game.inventories.Backpack;
import net.botwithus.rs3.game.js5.types.vars.VarDomainType;
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery;
import net.botwithus.rs3.game.queries.results.EntityResultSet;
import net.botwithus.rs3.game.scene.entities.characters.Headbar;
import net.botwithus.rs3.game.scene.entities.object.SceneObject;
import net.botwithus.rs3.game.skills.Skills;
import net.botwithus.rs3.game.vars.VarManager;
import net.botwithus.rs3.script.Execution;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static net.Snow.Combat.Combat.player;
import static net.Snow.ImGui.CustomLogger.*;
import static net.Snow.ImGui.Experience.inventoryMap;
import static net.Snow.ImGui.Experience.startTracking;
import static net.Snow.Smithing.SmithingGraphics.selectedBarType;
import static net.Snow.Smithing.SmithingGraphics.selectedItem;
import static net.Snow.Snow.*;

public class Smithing implements AbstractScript {

    Snow script;

    @Override
    public void initialize() {
        startingSmithingXP.set(Skills.SMITHING.getSkill().getExperience());
        startingSmithingLevel.set(Skills.SMITHING.getSkill().getActualLevel());
        startTracking();

    }

    public AtomicInteger startingSmithingXP = new AtomicInteger(0);
    public AtomicInteger startingSmithingLevel = new AtomicInteger(0);


    @Override
    public void draw(SnowsTemplateGraphics sgc) throws IOException {
        SmithingGraphics.draw(sgc, script, startingSmithingLevel, startingSmithingXP);
    }

    @Override
    public void onInventoryUpdate(InventoryUpdateEvent event) {
        SmithingUpdates.onInventoryUpdate(event, inventoryMap);
    }

    @Override
    public void onChatMessageEvent(ChatMessageEvent event) {
        SmithingUpdates.onChatMessageEvent(event, inventoryMap);
    }

    private static final EntityResultSet<SceneObject> bankChest = SceneObjectQuery.newQuery().name("Bank chest").option("Use").results();
    private static final EntityResultSet<SceneObject> furnace = SceneObjectQuery.newQuery().name("Furnace").option("Smelt").results();
    private static final EntityResultSet<SceneObject> anvil = SceneObjectQuery.newQuery().name("Anvil").option("Smith").results();
    private static final EntityResultSet<SceneObject> forge = SceneObjectQuery.newQuery().name("Forge").option("Heat").results();


    @Override
    public void run(Snow script) {
        if (Backpack.isFull() && !Backpack.contains("Unfinished smithing item")) {
            handleBanking();
            return; // Exit after banking to prevent further processing
        }

        if (Backpack.contains("Unfinished smithing item")) {
            // Find the relevant headbars
            Optional<Headbar> headbar5 = player.getHeadbars().stream()
                    .filter(bar -> bar.getId() == 5 && bar.getWidth() <= 190)
                    .findAny();

            Optional<Headbar> headbar13 = player.getHeadbars().stream()
                    .filter(bar -> bar.getId() == 13 && bar.getWidth() > 200)
                    .findAny();

            // Check if headbar 13 is above 200 and headbar 5 is below 190
            if (headbar13.isPresent() && headbar5.isPresent()) {
                logOrange("Headbar 13 is over 200, but headbar 5 is still under 190. Skipping forge interaction.");
                return; // Skip forge interaction
            } else if (headbar5.isPresent()) {
                logOrange("Item is being heated. Current heat level: " + headbar5.get().getWidth());
                interactWithForge();
            } else {
                logGrey("Item is sufficiently heated. Proceeding to smith.");
                handleSmithing();
            }
        } else {
            // Use the selected bar type and item from the GUI
            if (selectedBarType != null && selectedItem != null) {
                selectSmithingItems(selectedBarType, selectedItem);
            } else {
                logError("No bar type or item selected in the GUI.");
            }
        }
    }




    private static void interactWithForge() {
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
                    logSuccess("Item fully heated. Heat level is now greater than 250, or the headbar is no longer present.");
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


    private static void handleSmithing() {

        if (isAnimating()) {
            return;
        }

        SceneObject hammer = anvil.nearest();
        if (hammer != null) {
            logOrange("Smithing the unfinished item at the anvil.");
            if (hammer.interact("Smith")) {
                logSuccess("Successfully started smithing the item at the anvil.");
                Execution.delayUntil(30000, () -> player.getAnimationId() != -1);
            } else {
                logError("Failed to interact with the anvil.");
            }
        } else {
            logError("Anvil not found nearby.");
        }
    }

    private static void handleBanking() {
        logOrange("No unfinished smithing items found in the backpack. Attempting to load the last preset.");

        SceneObject bank = bankChest.nearest();
        if (bank != null) {
            logOrange("Found a bank nearby: " + bank.getName());

            if (bank.interact("Use")) {
                logSuccess("Successfully interacted with the bank to load the last preset.");
                if (Execution.delayUntil(30000, Bank::isOpen)) {
                    logSuccess("Bank interface opened successfully.");
                } else {
                    logError("Bank interface did not open within the expected time.");
                }
            } else {
                logError("Failed to interact with the bank to load the last preset.");
            }
        } else {
            logError("No bank found nearby.");
            return;
        }

        if (Bank.isOpen()) {
            logOrange("Depositing all items in the bank.");
            if (Bank.depositAll()) {
                logSuccess("Successfully deposited all items.");
            } else {
                logError("Failed to deposit all items.");
            }

            logOrange("Closing the bank interface.");
            if (Bank.close()) {
                logSuccess("Bank interface closed successfully.");
            } else {
                logError("Failed to close the bank interface.");
            }
        } else {
            logError("Bank was not open, unable to deposit items.");
        }
    }

    private static void selectSmithingItems(SmithingTypes selectedBarType, SmithingTypes.SmithingItem selectedItem) {
        SceneObject hammer = anvil.nearest();
        if (hammer != null) {
            logOrange("Found an anvil nearby. Attempting to open the smithing interface.");
            if (hammer.interact("Open smithing interface")) {
                logSuccess("Successfully interacted with the anvil. Waiting for the smithing interface to open.");
                if (Execution.delayUntil(30000, () -> Interfaces.isOpen(37))) {
                    logSuccess("Smithing interface opened successfully.");
                } else {
                    logError("Smithing interface did not open within the expected time.");
                    return;
                }
            } else {
                logError("Failed to interact with the anvil to open the smithing interface.");
                return;
            }
        } else {
            logError("No anvil found nearby.");
            return;
        }

        if (Interfaces.isOpen(37)) {
            logOrange("Attempting to select the bar and smithing item.");

            // Check if the bar type is already selected
            if (VarManager.getVarValue(VarDomainType.PLAYER, 8332) != selectedBarType.getVarbit()) {
                if (component(1, selectedBarType.getBarComponentParameter(), selectedBarType.getBarComponentId())) {
                    logOrange("Selected the bar component: " + selectedBarType.getName());
                    Execution.delay(random.nextInt(1500, 2500));
                } else {
                    logError("Failed to interact with the bar component: " + selectedBarType.getName());
                    return;
                }
            } else {
                logSuccess("The bar component for " + selectedBarType.getName() + " is already selected.");
            }

            // Check if the item is already selected
            if (VarManager.getVarValue(VarDomainType.PLAYER, selectedItem.getVarbit()) != selectedItem.getItemId()) {
                if (component(1, selectedItem.getItemComponentParameter(), selectedItem.getItemComponentId())) {
                    logSuccess("Successfully selected the smithing item: " + selectedItem.getItemName());
                    Execution.delay(random.nextInt(1500, 2500));
                } else {
                    logError("Failed to select the smithing item: " + selectedItem.getItemName());
                    return;
                }
            } else {
                logSuccess("The smithing item " + selectedItem.getItemName() + " is already selected.");
            }

            // Handle the enhancement level selection based on item name
            if (selectedItem.getItemName().contains("+")) {
                int enhancementVarbit = VarManager.getVarValue(VarDomainType.PLAYER, 8329);
                String itemName = selectedItem.getItemName();

                if (itemName.contains("+ 1") && enhancementVarbit != 4108) {
                    component(1, -1, 2424993);
                    logOrange("Selected +1 enhancement.");
                    Execution.delay(random.nextInt(1500, 2500));
                } else if (itemName.contains("+ 2") && enhancementVarbit != 8204) {
                    component(1, -1, 2424991);
                    logOrange("Selected +2 enhancement.");
                    Execution.delay(random.nextInt(1500, 2500));
                } else if (itemName.contains("+ 3") && enhancementVarbit != 12300) {
                    component(1, -1, 2424989);
                    logOrange("Selected +3 enhancement.");
                    Execution.delay(random.nextInt(1500, 2500));
                } else if (itemName.contains("+ 4") && enhancementVarbit != 16396) {
                    component(1, -1, 2424987);
                    logOrange("Selected +4 enhancement.");
                    Execution.delay(random.nextInt(1500, 2500));
                } else if (itemName.contains("+ 5") && enhancementVarbit != 20492) {
                    component(1, -1, 2424985);
                    logOrange("Selected +5 enhancement.");
                    Execution.delay(random.nextInt(1500, 2500));
                } else if (itemName.contains("Burial") && enhancementVarbit != 204812) {
                    component(1, -1, 2424983);
                    logOrange("Selected Burial enhancement.");
                    Execution.delay(random.nextInt(1500, 2500));

                } else {
                    logSuccess("The correct enhancement level is already selected.");
                }
            }

            // Handle unfinished item selection logic
            int varManagerValue = VarManager.getVarValue(VarDomainType.PLAYER, 8336);
            logOrange("Current VarManager value: " + varManagerValue);

            if (varManagerValue == 0) {
                logError("VarManager value is 0. Shutting down.");
                shutdown();
            } else {
                int freeSlots = net.botwithus.api.game.hud.inventories.Backpack.countFreeSlots();
                int slotsFilled = 28 - freeSlots;
                logOrange("Current free slots in backpack: " + freeSlots);
                logOrange("Current slots filled in backpack: " + slotsFilled);

                // Calculate the number of interactions needed
                int interactionsNeeded = 28 - (slotsFilled + varManagerValue);
                logOrange("Calculated interactions needed: " + interactionsNeeded);

                // Ensure the number of interactions needed is positive
                if (interactionsNeeded > 0) {
                    for (int i = 0; i < interactionsNeeded; i++) {
                        logWarning("Performing interaction " + (i + 1) + " of " + interactionsNeeded + ".");
                        component(1, 7, 2424866);
                        logWarning("Interaction " + (i + 1) + " completed. Delaying...");
                        Execution.delay(random.nextInt(500, 600));
                    }
                } else {
                    logBlue("No additional interactions needed based on current conditions.");
                }

                // Perform some final action or reset
                logSuccess("Performing final interaction with component 1, -1, 2424995.");
                component(1, -1, 2424995);
                logBlue("Waiting for the smithing interface to close...");
                boolean interfaceClosed = Execution.delayUntil(30000, () -> !Interfaces.isOpen(37));

                if (interfaceClosed) {
                    logSuccess("Smithing interface closed successfully.");
                } else {
                    logError("Smithing interface did not close within the expected time.");
                }
            }

        }
    }
}

