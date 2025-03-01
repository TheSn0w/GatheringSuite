package net.Snow.Woodcutting;

import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.rs3.game.Item;
import net.botwithus.rs3.game.actionbar.ActionBar;
import net.botwithus.rs3.game.queries.builders.items.InventoryItemQuery;
import net.botwithus.rs3.game.queries.results.ResultSet;
import net.botwithus.rs3.script.Execution;

import static net.Snow.ImGui.CustomLogger.logGrey;
import static net.Snow.Snow.random;

public class DropLogs {

    public static void dropAllLogs() {
        ResultSet<Item> allItems = InventoryItemQuery.newQuery(93).results();

        for (Item item : allItems) {
            if (item != null) {
                String itemName = item.getName();
                int category = item.getConfigType().getCategory();

                if (ActionBar.containsItem(itemName)) {
                    boolean success = ActionBar.useItem(itemName, "Drop");
                    if (success) {
                        logGrey("Dropping (ActionBar): " + itemName);
                        Execution.delay(random.nextLong(206, 405));
                    }
                } else if (category == 22) {
                    boolean success = Backpack.interact(itemName, "Drop");
                    if (success) {
                        logGrey("Dropping (Backpack): " + itemName);
                        Execution.delay(random.nextLong(620, 650));
                    }
                }
            }
        }
    }
}
