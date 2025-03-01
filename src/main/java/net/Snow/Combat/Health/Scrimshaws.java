package net.Snow.Combat.Health;

import net.botwithus.api.game.hud.inventories.Equipment;
import net.botwithus.rs3.game.Item;
import net.botwithus.rs3.game.queries.builders.items.InventoryItemQuery;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.game.vars.VarManager;
import net.botwithus.rs3.script.Execution;

import java.util.regex.Pattern;

import static net.Snow.ImGui.CustomLogger.logError;
import static net.Snow.ImGui.CustomLogger.logSuccess;
import static net.botwithus.rs3.game.Client.getLocalPlayer;

public class Scrimshaws {

    public static boolean scriptureofJas = false;
    public static boolean scriptureofWen = false;
    public static boolean useScrimshaws = false;

    public static void manageScripturesAndScrimshaws(LocalPlayer player) {
        if (scriptureofJas) {
            manageScriptureOfJas();
        }
        if (scriptureofWen) {
            manageScriptureOfWen();
        }
        if (useScrimshaws) {
            manageScrimshaws(player);
        }
    }

    public static void manageScriptureOfJas() {
        if (getLocalPlayer() != null) {
            if (getLocalPlayer().inCombat()) {
                activateScriptureOfJas();
            } else {
                deactivateScriptureOfJas();
            }
        }
    }

    public static void activateScriptureOfJas() {
        if (VarManager.getVarbitValue(30605) == 0 && VarManager.getVarbitValue(30604) >= 60) {
            logSuccess("[Combat] Activated Scripture of Jas:  " + Equipment.interact(Equipment.Slot.POCKET, "Activate/Deactivate"));
            Execution.delayUntil(2000, () -> VarManager.getVarbitValue(30605) == 1);
        }
    }

    public static void deactivateScriptureOfJas() {
        if (VarManager.getVarbitValue(30605) == 1) {
            logSuccess("[Combat] Deactivated Scripture of Jas:  " + Equipment.interact(Equipment.Slot.POCKET, "Activate/Deactivate"));
            Execution.delayUntil(2000, () -> VarManager.getVarbitValue(30605) == 0);
        }
    }

    public static void manageScriptureOfWen() {
        if (getLocalPlayer() != null) {
            if (getLocalPlayer().inCombat()) {
                activateScriptureOfWen();
            } else {
                deactivateScriptureOfWen();
            }
        }
    }

    public static void activateScriptureOfWen() {
        if (VarManager.getVarbitValue(30605) == 0 && VarManager.getVarbitValue(30604) >= 60) {
            logSuccess("[Combat] Activated Scripture of Wen:  " + Equipment.interact(Equipment.Slot.POCKET, "Activate/Deactivate"));
            Execution.delayUntil(2000, () -> VarManager.getVarbitValue(30605) == 1);
        }
    }

    public static void deactivateScriptureOfWen() {
        if (VarManager.getVarbitValue(30605) == 1) {
            logSuccess("[Combat] Deactivated Scripture of Wen:  " + Equipment.interact(Equipment.Slot.POCKET, "Activate/Deactivate"));
            Execution.delayUntil(2000, () -> VarManager.getVarbitValue(30605) == 0);
        }
    }
    public static void manageScrimshaws(LocalPlayer player) {
        Pattern scrimshawPattern = Pattern.compile("scrimshaw", Pattern.CASE_INSENSITIVE);
        Item Scrimshaw = InventoryItemQuery.newQuery(94).name(scrimshawPattern).results().first();

        if (Scrimshaw != null) {
            if (player.inCombat()) {
                activateScrimshaws();
            } else {
                deactivateScrimshaws();
            }
        } else {
            logError("Pocket slot does not contain a scrimshaw.");
        }
    }

    private static void activateScrimshaws() {
        Pattern scrimshawPattern = Pattern.compile("scrimshaw", Pattern.CASE_INSENSITIVE);
        Item Scrimshaw = InventoryItemQuery.newQuery(94).name(scrimshawPattern).results().first();
        if (Scrimshaw != null && VarManager.getInvVarbit(Scrimshaw.getInventoryType().getId(), Scrimshaw.getSlot(), 17232) == 0) {
            logSuccess("[Combat] Activating Scrimshaws.");
            Equipment.interact(Equipment.Slot.POCKET, "Activate/Deactivate");
            Execution.delayUntil(2000, () -> VarManager.getInvVarbit(Scrimshaw.getInventoryType().getId(), Scrimshaw.getSlot(), 17232) == 1);
        }
    }


    private static void deactivateScrimshaws() {
        Pattern scrimshawPattern = Pattern.compile("scrimshaw", Pattern.CASE_INSENSITIVE);
        Item Scrimshaw = InventoryItemQuery.newQuery(94).name(scrimshawPattern).results().first();
        if (Scrimshaw != null && VarManager.getInvVarbit(Scrimshaw.getInventoryType().getId(), Scrimshaw.getSlot(), 17232) == 1) {
            logSuccess("[Combat] Deactivating Scrimshaws.");
            Equipment.interact(Equipment.Slot.POCKET, "Activate/Deactivate");
            Execution.delayUntil(2000, () -> VarManager.getInvVarbit(Scrimshaw.getInventoryType().getId(), Scrimshaw.getSlot(), 17232) == 0);
        }
    }
}
