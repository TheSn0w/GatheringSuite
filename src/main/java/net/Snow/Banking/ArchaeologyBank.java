package net.Snow.Banking;

import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.api.game.hud.inventories.Bank;
import net.botwithus.rs3.game.Coordinate;
import net.botwithus.rs3.game.Item;
import net.botwithus.rs3.game.hud.interfaces.Interfaces;
import net.botwithus.rs3.game.movement.Movement;
import net.botwithus.rs3.game.movement.NavPath;
import net.botwithus.rs3.game.movement.TraverseEvent;
import net.botwithus.rs3.game.queries.builders.items.InventoryItemQuery;
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery;
import net.botwithus.rs3.game.queries.results.EntityResultSet;
import net.botwithus.rs3.game.scene.entities.object.SceneObject;
import net.botwithus.rs3.game.vars.VarManager;
import net.botwithus.rs3.script.Execution;

import static net.Snow.Banking.BankLocations.checkBankTabs;
import static net.Snow.Banking.Porters.handlePorterWithdrawals;
import static net.Snow.Banking.Porters.usePorters;
import static net.Snow.ImGui.CustomLogger.*;
import static net.Snow.Mining.Familiar.useFamiliar;
import static net.Snow.Snow.component;
import static net.Snow.Snow.random;

public class ArchaeologyBank {

    public static void depositAll() {
        Item soilBox = InventoryItemQuery.newQuery(93).name("Archaeological soil box").results().first();

        if (Bank.isOpen()) {
            checkBankTabs();
            Execution.delay(random.nextLong(500, 1000));
            Bank.depositAllExcept(49538, 50096, 4614, 49976, 50431, 49947, 49949, 50753, 41092, 49429, 50161, 15707, 49948);
            Execution.delay(random.nextLong(500, 1000));

            withdrawSoilBox();

            if (soilBox != null) {
                logBlue("Soil box found in the Backpack. Emptying it.");
                if (soilBox.getSlot() >= 0) {
                    component(9, soilBox.getSlot(), 33882127);
                    logSuccess("Emptied: " + soilBox.getName());
                    Execution.delay(random.nextLong(500, 1000));
                }
            }
            if (useFamiliar) {
                handleFamiliarWithdrawals();
                Execution.delay(random.nextLong(500, 1000));
            }
            // Handle Porters if they are toggled to withdraw them
            if (usePorters) {
                handlePorterWithdrawals();
                Execution.delay(random.nextLong(500, 1000));
            }

            // close the bank
            Bank.close();
        } else {
            logError("Bank is not open.");
        }
    }
    public static void handleFamiliarWithdrawals() {
        if (Interfaces.isOpen(517)) {
            logBlue("Attempting to withdraw a Waterfiend.");
            boolean familiarWithdrawn = Bank.withdraw(49420, 2);
            logBlue("Familiar withdrawal result for Waterfiend: " + familiarWithdrawn);

            Execution.delay(random.nextLong(1200, 1500));

            logBlue("Attempting to withdraw a Super Restore.");
            int[] superRestoreIds = {23399, 23401, 23403, 23405, 23407, 23409, 3024, 3026, 3028, 3030};
            boolean superRestoreWithdrawn = false;

            for (int id : superRestoreIds) {
                superRestoreWithdrawn = Bank.withdraw(id, 2);
                logBlue("Super Restore withdrawal result for ID " + id + ": " + superRestoreWithdrawn);
                if (superRestoreWithdrawn) {
                    break;
                }
                Execution.delay(random.nextLong(1200, 1500));
            }
        } else {
            logError("Interface 517 is not open.");
        }
    }

    public static void handleCompleteTome() {
        Coordinate deskCoordinate = new Coordinate(3327, 3378, 1);
        if (Movement.traverse(NavPath.resolve(deskCoordinate)) == TraverseEvent.State.FINISHED) {
            EntityResultSet<SceneObject> studyDesk = SceneObjectQuery.newQuery().name("Desk").option("Study").results();
            if (!studyDesk.isEmpty()) {
                SceneObject desk = studyDesk.nearest();
                if (desk.interact("Study")) {
                    log("[Archaeology] Interacting with Study Desk.");
                    Execution.delayUntil(15000, () -> !Backpack.contains("Complete tome"));
                }
            }
        }
    }

    public static void withdrawSoilBox() {
        int Relic1 = VarManager.getVarbitValue(46702);
        int Relic2 = VarManager.getVarbitValue(46703);
        int Relic3 = VarManager.getVarbitValue(46704);
        Item soilBox = InventoryItemQuery.newQuery(95).name("Archaeological soil box").results().first();
        Item autoScreener = InventoryItemQuery.newQuery(95).name("Auto-screener v1.080").results().first();

        if (Relic1 == 28 || Relic2 == 28 || Relic3 == 28 || Backpack.contains("Auto-screener v1.080")) {
            log("Flow State Relic is Active or Auto Screener detected. Not withdrawing Soil Box.");
        } else {
            if (Backpack.contains("Archaeological soil box")) {
                log("Soil Box found in the Backpack.");
            } else {
                if (soilBox != null) {
                    log("Soil Box found. Attempting to withdraw it.");
                    if (Bank.isOpen()) {
                        component(1, soilBox.getSlot(), 33882314);
                        Execution.delay(random.nextLong(1200, 1500));
                    } else {
                        logError("Bank is not open.");
                    }
                } else {
                    log("Soil Box not found in the Bank.");
                }
            }
            if (Backpack.contains("Auto-screener v1.080")) {
                log("Auto-screener v1.080 found in the Backpack.");
            } else {
                if (autoScreener != null) {
                    log("Auto-screener v1.080 found. Attempting to withdraw it.");
                    if (Bank.isOpen()) {
                        component(1, autoScreener.getSlot(), 33882314);
                        Execution.delay(random.nextLong(1200, 1500));
                    } else {
                        logError("Bank is not open.");
                    }
                } else {
                    log("Auto-screener v1.080 not found in the Bank.");
                }
            }
        }
    }
}