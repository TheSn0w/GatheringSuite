package net.Snow.Woodcutting;

import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.rs3.game.hud.interfaces.Component;
import net.botwithus.rs3.game.hud.interfaces.Interfaces;
import net.botwithus.rs3.game.queries.builders.components.ComponentQuery;
import net.botwithus.rs3.game.queries.builders.items.GroundItemQuery;
import net.botwithus.rs3.game.scene.entities.item.GroundItem;
import net.botwithus.rs3.script.Execution;
import net.botwithus.rs3.util.Regex;

import java.util.regex.Pattern;

import static net.Snow.ImGui.CustomLogger.*;

public class BirdsNests {

    public static boolean pickupNests = false;
    public static Pattern nestPattern = Regex.getPatternForContainsString("Bird's");


    public static void birdsNest() {
        if (pickupNests) {
            GroundItem nest = GroundItemQuery.newQuery().name(nestPattern).results().first();
            if (nest != null) {
                if (Interfaces.isOpen(1622) && Backpack.countFreeSlots() > 0) {
                    Component loot = ComponentQuery.newQuery(1622).componentIndex(22).results().first();
                    if (loot == null) {
                        logError("Loot button not found.");
                    } else {
                        logSuccess("Looted all: " + loot.interact(1));
                        Execution.delayUntil(3000, () -> GroundItemQuery.newQuery().name(nestPattern).results().isEmpty());
                    }
                } else {
                    logBlue("Looted ground item: " + nest.interact("Take"));
                    Execution.delayUntil(10000, () -> Interfaces.isOpen(1622));
                }
            }
        }
    }
}
