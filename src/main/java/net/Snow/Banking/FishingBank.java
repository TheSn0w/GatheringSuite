package net.Snow.Banking;

import net.botwithus.api.game.hud.inventories.Bank;
import net.botwithus.rs3.game.Item;
import net.botwithus.rs3.game.hud.interfaces.Interfaces;
import net.botwithus.rs3.game.queries.builders.items.InventoryItemQuery;
import net.botwithus.rs3.game.queries.results.ResultSet;
import net.botwithus.rs3.script.Execution;

import java.util.regex.Pattern;

import static net.Snow.Banking.BankLocations.checkBankTabs;
import static net.Snow.Banking.Porters.handlePorterWithdrawals;
import static net.Snow.Banking.Porters.usePorters;
import static net.Snow.ImGui.CustomLogger.logBlue;
import static net.Snow.ImGui.CustomLogger.logError;
import static net.Snow.Mining.Familiar.useFamiliar;
import static net.Snow.Mining.MiningVariables.useJujuPotion;
import static net.Snow.Snow.component;
import static net.Snow.Snow.random;
import static net.Snow.Spells.Lightform.useLightForm;

public class FishingBank {

    public static void depositAll() {
        // if the bank is open, deposit all items except the ore boxes and gem bags and Perfect Juju Mining Potions 1/2/3/4
        if (Bank.isOpen()) {
            Execution.delay(random.nextLong(500, 1000));
            checkBankTabs();
            Bank.depositAllExcept(313, 314, 20323, 20329, 20335, 20341, 20347);
            Execution.delay(random.nextLong(500, 1000));


            if (useLightForm) {
                withdrawRestore();
                Execution.delay(random.nextLong(500, 1000));
            }

            if (useJujuPotion) {
                handleJujuPotionWithdrawals();
                Execution.delay(random.nextLong(500, 1000));
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


    private static final Pattern perfectJujuPotion = Pattern.compile("perfect juju fishing potion.*", Pattern.CASE_INSENSITIVE);
    public static final Pattern superRestorePattern = Pattern.compile("super restore.*", Pattern.CASE_INSENSITIVE);


    private static void handleJujuPotionWithdrawals() {
        if (Interfaces.isOpen(517)) {
            logBlue("Attempting to withdraw Perfect Juju Fishing Potion.");
            ResultSet<Item> jujuPotions = InventoryItemQuery.newQuery(95).name(perfectJujuPotion).results();
            if (!jujuPotions.isEmpty()) {
                Item jujuPotion = jujuPotions.first();
                String itemName = jujuPotion.getName();
                int itemSlot = jujuPotion.getSlot();
                component(2, itemSlot, 33882307);
                logBlue("Withdrew: " + itemName + " from slot: " + itemSlot);
            } else {
                logBlue("Perfect Juju Mining Potion not found in inventory.");
            }
            Execution.delay(random.nextLong(1200, 1500));
        } else {
            logError("Interface 517 is not open.");
        }
    }

    private static void handleFamiliarWithdrawals() {
        if (Interfaces.isOpen(517)) {
            logBlue("Attempting to withdraw a Granite Lobster.");
            boolean familiarWithdrawn = Bank.withdraw(12069, 2);
            logBlue("Familiar withdrawal result for Granite Lobster: " + familiarWithdrawn);

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

    private static void withdrawRestore() {
        if (Interfaces.isOpen(517)) {
            logBlue("Attempting to withdraw a Super Restore.");
            ResultSet<Item> superRestores = InventoryItemQuery.newQuery(95).name(superRestorePattern).results();
            if (!superRestores.isEmpty()) {
                Item superRestore = superRestores.first();
                String itemName = superRestore.getName();
                int itemSlot = superRestore.getSlot();
                Bank.withdraw(superRestore.getId(), 3);
                logBlue("Withdrew: " + itemName + " from slot: " + itemSlot);
            } else {
                logBlue("Super Restore not found in inventory.");
            }
            Execution.delay(random.nextLong(1200, 1500));
        } else {
            logError("Interface 517 is not open.");
        }
    }
}

