package net.Snow.Divination;

import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.api.game.hud.inventories.Equipment;
import net.botwithus.rs3.game.Item;
import net.botwithus.rs3.game.queries.builders.items.InventoryItemQuery;
import net.botwithus.rs3.game.queries.results.ResultSet;
import net.botwithus.rs3.game.vars.VarManager;
import net.botwithus.rs3.script.Execution;

import java.util.concurrent.ThreadLocalRandom;

import static net.Snow.ImGui.CustomLogger.log;
import static net.Snow.ImGui.CustomLogger.logGrey;

public class DivineOMatic {

    public static boolean useDivineoMatic = false;

    public static void divineoMatic() { // 37521 = empty charges // 37522 = filled charges
        ResultSet<Item> divineomaticvacuum = InventoryItemQuery.newQuery(94).ids(41083).results();
        if (!divineomaticvacuum.isEmpty()) {
            int emptyCharges = VarManager.getInvVarbit(94, 3, 37521);
            int filledCharges = VarManager.getInvVarbit(94, 3, 37522);
            logGrey("Initial Empty Charges: " + emptyCharges + " - Initial Filled Charges: " + filledCharges);

            if (filledCharges > ThreadLocalRandom.current().nextInt(25, 100) && Equipment.interact(Equipment.Slot.WEAPON, "Withdraw")) {
                log("Divine-o-matic is full, withdrawing.");
                Execution.delayUntil(30000, () -> VarManager.getInvVarbit(94, 3, 37521) + VarManager.getInvVarbit(94, 3, 37522) < 100);
                emptyCharges = VarManager.getInvVarbit(94, 3, 37521);
                filledCharges = VarManager.getInvVarbit(94, 3, 37522);
                log("After withdrawal, Empty Charges: " + emptyCharges + " - Filled Charges: " + filledCharges);

                if (Backpack.contains("Divine charge (empty)") && emptyCharges < 100) {
                    log("[Divination] Adding all to vacuum.");
                    if (Backpack.interact("Divine charge (empty)", "Add all to vacuum")) {
                        emptyCharges = VarManager.getInvVarbit(94, 3, 37521);
                        filledCharges = VarManager.getInvVarbit(94, 3, 37522);
                        log("After adding to vacuum, Empty Charges: " + emptyCharges + " - Filled Charges: " + filledCharges);
                    } else {
                        log("Failed to add all to vacuum.");
                    }
                } else {
                    log("No empty charges found in backpack, turning option off");
                    useDivineoMatic = false;
                }
            }
        } else {
            log("Divine-o-matic vacuum not found in inventory.");
        }
    }
}
