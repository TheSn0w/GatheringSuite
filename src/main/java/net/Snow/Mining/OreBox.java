package net.Snow.Mining;

import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.rs3.game.Item;
import net.botwithus.rs3.game.queries.builders.items.InventoryItemQuery;
import net.botwithus.rs3.script.Execution;

import java.util.regex.Pattern;

import static net.Snow.ImGui.CustomLogger.logOrange;
import static net.Snow.Snow.random;

public class OreBox {

    public static final Pattern ORE_BOXES_PATTERN = Pattern.compile("(?i)Bronze ore box|Iron ore box|Steel ore box|Mithril ore box|Adamant ore box|Rune ore box|Orikalkum ore box|Necronium ore box|Bane ore box|Elder rune ore box|Primal ore box");


    static boolean fillOreBox() {
        // Query the inventory for an ore box matching the pattern
        Item oreBox = InventoryItemQuery.newQuery(93).name(ORE_BOXES_PATTERN).results().first();

        // If an ore box is found, attempt to fill it
        if (oreBox != null) {
            Backpack.interact(oreBox.getName(), "Fill");
            Execution.delay(random.nextLong(1500, 3000)); // Delay to simulate filling time

            // Check if the backpack is no longer full after filling the ore box
            if (!Backpack.isFull()) {
                logOrange("Filled: " + oreBox.getName());
                return true; // Return true if the ore box was successfully filled
            } else {
                return false; // Return false if the backpack is still full
            }
        }
        return false; // Return false if no ore box was found
    }
}
