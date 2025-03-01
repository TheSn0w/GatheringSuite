package net.Snow.Runecrafting;

import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.rs3.game.Item;
import net.botwithus.rs3.game.hud.interfaces.Interfaces;
import net.botwithus.rs3.game.queries.builders.items.InventoryItemQuery;
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery;
import net.botwithus.rs3.game.queries.results.EntityResultSet;
import net.botwithus.rs3.game.queries.results.ResultSet;
import net.botwithus.rs3.game.scene.entities.object.SceneObject;
import net.botwithus.rs3.script.Execution;

import static net.Snow.ImGui.CustomLogger.logError;
import static net.Snow.ImGui.CustomLogger.logLime;
import static net.Snow.Snow.*;

public class SoulAltar {

    public static void useSoulAltar() {

        capturestuff();

        // Check if interface 1251 is open, log a message and exit
        if (Interfaces.isOpen(1251)) {
            return;
        }

        // Check if interface 1370 is open, log a message and select the interface
        if (Interfaces.isOpen(1370)) {
            selectInterface1370();
            return;
        }

        if (Backpack.contains("Protean essence")) {
            ResultSet<Item> proteanEssenceItems = InventoryItemQuery.newQuery(93).name("Protean essence").results();
            if (!proteanEssenceItems.isEmpty()) {
                Item proteanEssence = proteanEssenceItems.first();
                int slot = proteanEssence.getSlot();
                int quantity = proteanEssence.getStackSize();
                logLime("Protean essence found in slot: " + slot + ", quantity: " + quantity + ". Using the essence to craft runes.");
            } else {
                logError("Protean essence not found in inventory.");
                shutdown();
            }
        } else {
            logError("Protean essence not found in inventory.");
            shutdown();
            return;
        }

        EntityResultSet<SceneObject> results = SceneObjectQuery.newQuery().id(109429).option("Craft-rune").results();
        SceneObject altar = results.nearest();
        if (altar != null) {
            if (altar.interact("Craft-rune")) {
                logLime("Successfully interacted with the altar to craft runes.");
                Execution.delayUntil(30000, () -> Interfaces.isOpen(1370));
                return;
            } else {
                logError("Failed to interact with the altar to craft runes.");
                return;
            }
        } else {
            logError("Altar not found.");
        }
        return;
    }
}
