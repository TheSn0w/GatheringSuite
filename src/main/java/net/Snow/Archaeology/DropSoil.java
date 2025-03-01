package net.Snow.Archaeology;

import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.rs3.game.Item;
import net.botwithus.rs3.game.actionbar.ActionBar;
import net.botwithus.rs3.game.hud.interfaces.Interfaces;
import net.botwithus.rs3.game.queries.builders.items.InventoryItemQuery;
import net.botwithus.rs3.game.queries.results.ResultSet;
import net.botwithus.rs3.script.Execution;

import static net.Snow.ImGui.CustomLogger.*;
import static net.Snow.Snow.dialog;
import static net.Snow.Snow.random;

public class DropSoil {

    public static boolean dropSoil = false;

    public static void dropAllSoil() {
        logGrey("Checking for all soil items in inventory...");

        // Dynamically query the inventory for items in category 4603
        ResultSet<Item> allItems = InventoryItemQuery.newQuery(93).category(4603).results();
        logGrey("Found " + allItems.size() + " soil items to drop.");

        for (Item item : allItems) {
            if (item != null) {
                logGrey("Dropping item: " + item.getName());
                dropItem(item);
            }
        }
    }

    public static void dropItem(Item item) {
        String itemName = item.getName();
        int category = item.getConfigType().getCategory();

        if (ActionBar.containsItem(itemName)) {
            boolean success = ActionBar.useItem(itemName, "Drop");
            if (success) {
                logGrey("Dropping (ActionBar): " + itemName);
                Execution.delay(random.nextLong(206, 405));
            }
        } else if (category == 4603) {
            logGrey("Attempting to put the: " + itemName + " in the action bar");
            boolean success = Backpack.interact(itemName, "Drop");
            if (success) {
                logGrey("Dropping (Backpack): " + itemName);
                Execution.delay(random.nextLong(620, 650));
            }
        }
    }

    public static void interactWithPages() {
        logPink("Checking for items in category 4617 in the backpack...");

        // Dynamically query the inventory for items in category 4617
        ResultSet<Item> category4617Items = InventoryItemQuery.newQuery(93).category(4617).results();
        logOrange("Found " + category4617Items.size() + " items in category 4617.");

        for (Item item : category4617Items) {
            if (item != null) {
                int slot = item.getSlot();
                String itemName = item.getName();
                logOrange("Found item: " + itemName + " in slot: " + slot);
                boolean success = Backpack.interact(slot, "Transcribe");
                if (success) {
                    logBlue("Interacting with item: " + itemName + " in slot: " + slot);
                    Execution.delayUntil(10000, () -> Interfaces.isOpen(1183));
                }
                if (Interfaces.isOpen(1183)) {
                    dialog( 0, -1, 77529093);
                    Execution.delay(random.nextLong(1000, 2000));
                    boolean closeSuccess = !Interfaces.isOpen(1183);
                    if (closeSuccess) {
                        logBlue("Interface closed successfully.");
                        Execution.delayUntil(10000, () -> !Interfaces.isOpen(1183));
                    }
                }
            }
        }
    }
}
