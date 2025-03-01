package net.Snow.Archaeology;

import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.rs3.game.vars.VarManager;
import net.botwithus.rs3.script.Execution;
import net.botwithus.rs3.util.RandomGenerator;

import static net.Snow.ImGui.CustomLogger.logOrange;

public class Buffs {

    public static void useArchaeologyBuffs() {
        Object[][] buffs = {
                {47028, "Archaeologist's tea", "Drink"},
                {47026, "Hi-spec monocle", "Wear"},
                {47025, "Material manual", "Read"},
                {47027, "Tarpaulin sheet", "Place"}
        };

        // Iterate over each buff and check conditions
        for (Object[] buff : buffs) {
            int varbitId = (int) buff[0];
            String itemName = (String) buff[1];
            String action = (String) buff[2];

            // Check if the varbit value is less than a random number and if the item is in the backpack
            if (VarManager.getVarbitValue(varbitId) < RandomGenerator.nextInt(0, 80)) {
                if (Backpack.contains(itemName)) {
                    Backpack.interact(itemName, action);
                    logOrange("Using " + itemName + ".");
                    Execution.delay(RandomGenerator.nextInt(1500, 3000));
                }
            }
        }
    }
}
