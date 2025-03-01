package net.Snow.Mining;

import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.rs3.game.actionbar.ActionBar;
import net.botwithus.rs3.game.queries.builders.items.InventoryItemQuery;
import net.botwithus.rs3.script.Execution;

import java.util.Objects;

import static net.Snow.ImGui.CustomLogger.logGrey;
import static net.Snow.Snow.random;

public class DropOres {

    public static void dropAllOres() {
        // Query for ores in the inventory and drop them
        InventoryItemQuery.newQuery(93).category(91).results().stream().filter(Objects::nonNull).forEach(item -> {
            String itemName = item.getName();
            // Drop the item using the action bar if possible, otherwise use the backpack
            if (ActionBar.containsItem(itemName) && ActionBar.useItem(itemName, "Drop")) {
                logGrey("Dropping (ActionBar): " + itemName);
                Execution.delay(random.nextLong(206, 405));
            } else if (item.getConfigType().getCategory() == 91 && Backpack.interact(itemName, "Drop")) {
                logGrey("Dropping (Backpack): " + itemName);
                Execution.delay(random.nextLong(620, 650));
            }
        });
    }
}
