package net.Snow.Runecrafting;

import net.botwithus.rs3.game.Item;
import net.botwithus.rs3.game.queries.builders.items.InventoryItemQuery;
import net.botwithus.rs3.game.queries.results.ResultSet;

import static net.Snow.ImGui.CustomLogger.logError;
import static net.Snow.Snow.component;

public class PriceChecker {

    public static void fleshRunePrice() {

        // Query for the flesh rune
        logError("Querying for Flesh rune.");
        ResultSet<Item> rune = InventoryItemQuery.newQuery(93).ids(55339).results();
        logError("Query result: " + (rune.isEmpty() ? "No rune found" : "Rune found"));

        if (!rune.isEmpty()) {
            Item fleshRune = rune.first();
            int slotNumber = fleshRune.getSlot();
            logError("Flesh rune found in slot: " + slotNumber);

            // Log before calling the component method
            logError("Calling component method with slot number: " + slotNumber);
            component(10, slotNumber, 96534533);
            logError("Component method called successfully.");
        } else {
            logError("No Flesh rune found.");
        }
    }

    public static void boneRunePrice() {
        logError("Querying for Bone rune.");
        ResultSet<Item> rune = InventoryItemQuery.newQuery(93).ids(55338).results();
        logError("Query result: " + (rune.isEmpty() ? "No rune found" : "Rune found"));

        if (!rune.isEmpty()) {
            Item boneRune = rune.first();
            int slotNumber = boneRune.getSlot();
            logError("Bone rune found in slot: " + slotNumber);

            // Log before calling the component method
            logError("Calling component method with slot number: " + slotNumber);
            component(10, slotNumber, 96534533);
            logError("Component method called successfully.");
        } else {
            logError("No Bone rune found.");
        }
    }

    public static void spiritRunePrice() {
        logError("Querying for Spirit rune.");
        ResultSet<Item> rune = InventoryItemQuery.newQuery(93).ids(55337).results();
        logError("Query result: " + (rune.isEmpty() ? "No rune found" : "Rune found"));

        if (!rune.isEmpty()) {
            Item spiritRune = rune.first();
            int slotNumber = spiritRune.getSlot();
            logError("Spirit rune found in slot: " + slotNumber);

            // Log before calling the component method
            logError("Calling component method with slot number: " + slotNumber);
            component(10, slotNumber, 96534533);
            logError("Component method called successfully.");
        } else {
            logError("No Spirit rune found.");
        }
    }

    public static void miasmaRunePrice() {
        logError("Querying for Miasma rune.");
        ResultSet<Item> rune = InventoryItemQuery.newQuery(93).ids(55340).results();
        logError("Query result: " + (rune.isEmpty() ? "No rune found" : "Rune found"));

        if (!rune.isEmpty()) {
            Item miasmaRune = rune.first();
            int slotNumber = miasmaRune.getSlot();
            logError("Miasma rune found in slot: " + slotNumber);

            // Log before calling the component method
            logError("Calling component method with slot number: " + slotNumber);
            component(10, slotNumber, 96534533);
            logError("Component method called successfully.");
        } else {
            logError("No Miasma rune found.");
        }
    }
}
