package net.Snow.Divination;

import net.botwithus.rs3.game.Item;
import net.botwithus.rs3.game.queries.builders.items.InventoryItemQuery;
import net.botwithus.rs3.game.queries.results.ResultSet;

import static net.Snow.ImGui.CustomLogger.logError;
import static net.Snow.Snow.component;

public class PriceChecker {

    public static void energyPrice() {
        // Query for the energy
        logError("Querying for Incandescent energy.");
        ResultSet<Item> energy = InventoryItemQuery.newQuery(93).ids(29324).results();
        logError("Query result: " + (energy.isEmpty() ? "No energy found" : "Energy found"));

        if (!energy.isEmpty()) {
            Item incandescentEnergy = energy.first();
            int slotNumber = incandescentEnergy.getSlot();
            logError("Incandescent energy found in slot: " + slotNumber);

            // Log before calling the component method
            logError("Calling component method with slot number: " + slotNumber);
            //[Original]: DoAction(COMPONENT, 10, 3, 96534533)
            component(10, slotNumber, 96534533);
            logError("Component method called successfully.");
        } else {
            logError("No Incandescent energy found.");
        }
    }
}
