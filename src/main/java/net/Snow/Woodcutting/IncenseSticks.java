package net.Snow.Woodcutting;

import net.botwithus.rs3.game.Item;
import net.botwithus.rs3.game.hud.interfaces.Interfaces;
import net.botwithus.rs3.game.inventories.Backpack;
import net.botwithus.rs3.game.queries.builders.items.InventoryItemQuery;
import net.botwithus.rs3.game.queries.results.ResultSet;
import net.botwithus.rs3.script.Execution;

import static net.Snow.ImGui.CustomLogger.*;
import static net.Snow.Snow.*;

public class IncenseSticks {

    public static boolean doIncenseSticks = false;

    public static void burnIncenseSticks() {
        logBlue("Starting incense sticks burning process.");

        ResultSet<Item> logs = InventoryItemQuery.newQuery(93)
                .option("Craft")
                .results();

        logGrey("Logs in inventory: " + logs.size());

        Item woodLogs = logs.first();
        if (woodLogs == null) {
            logError("No logs found in inventory.");
            return;
        } else {
            logSuccess("Logs found in inventory: " + woodLogs.getName());
        }

        Backpack.interact(woodLogs.getName(), "Craft");
        Execution.delayUntil(random.nextLong(10000), () -> Interfaces.isOpen(1370) || Interfaces.isOpen(1179));

        if (Interfaces.isOpen(1179)) {
            logBlue("Selecting Incense Sticks.");
            dialog(0, -1, 77266976);
            Execution.delay(random.nextLong(900, 1250));
        }

        if (Interfaces.isOpen(1370)) {
            selectInterface1370();
        }
    }
}

