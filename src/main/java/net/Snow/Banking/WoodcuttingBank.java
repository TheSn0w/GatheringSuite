package net.Snow.Banking;

import net.botwithus.api.game.hud.inventories.Bank;
import net.botwithus.rs3.game.Item;
import net.botwithus.rs3.game.hud.interfaces.Interfaces;
import net.botwithus.rs3.game.queries.builders.items.InventoryItemQuery;
import net.botwithus.rs3.game.queries.results.ResultSet;
import net.botwithus.rs3.script.Execution;

import java.util.regex.Pattern;

import static net.Snow.Banking.BankLocations.checkBankTabs;
import static net.Snow.Banking.MiningBank.superRestorePattern;
import static net.Snow.Banking.Porters.handlePorterWithdrawals;
import static net.Snow.Banking.Porters.usePorters;
import static net.Snow.ImGui.CustomLogger.*;
import static net.Snow.Mining.Familiar.useFamiliar;
import static net.Snow.Mining.MiningVariables.useJujuPotion;
import static net.Snow.Snow.component;
import static net.Snow.Snow.random;
import static net.Snow.Spells.Lightform.useLightForm;

public class WoodcuttingBank {

    private static final Pattern perfectJujuPotion = Pattern.compile("perfect juju woodcutting potion.*", Pattern.CASE_INSENSITIVE);


    public static void depositAll() {
        Item woodBox = InventoryItemQuery.newQuery(93).category(5297).results().first();

        // if the bank is open, deposit all items except the ore boxes and gem bags and Perfect Juju Mining Potions 1/2/3/4
        if (Bank.isOpen()) {
            Execution.delay(random.nextLong(500, 1000));
            checkBankTabs();
            Bank.depositAllExcept(54895, 54896, 54897, 54898, 54899, 54900, 54901, 54902, 54903, 54904, 54905, 54906, 54907, 54908, 54909, 54910, 54911, 54912, 54913, 54914, 54915, 54916, 54917, 54918, 36543);
            Execution.delay(random.nextLong(500, 1000));
            // if the ore box is found in the inventory, empty it
            if (woodBox != null) {
                logBlue("Wood box found in the Backpack. Emptying it.");
                if (woodBox.getSlot() >= 0) {
                    component(8, woodBox.getSlot(), 33882127);
                    logSuccess("Emptied: " + woodBox.getName());
                    Execution.delay(random.nextLong(500, 1000));
                }
            }
            // Handle Juju Potions if they are toggled to withdraw them
            if (useJujuPotion) {
                handleJujuPotionWithdrawals();
                Execution.delay(random.nextLong(500, 1000));
            }
            // Handle Familiars if they are toggled to withdraw them
            if (useFamiliar) {
                handleFamiliarWithdrawals();
                Execution.delay(random.nextLong(500, 1000));
            }

            if (useLightForm) {
                withdrawRestore();
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
    private static void handleJujuPotionWithdrawals() {
        checkBankTabs();

        if (Interfaces.isOpen(517)) {
            logBlue("Attempting to withdraw Perfect Juju Woodcutting Potion.");
            ResultSet<Item> jujuPotions = InventoryItemQuery.newQuery(95).name(perfectJujuPotion).results();
            if (!jujuPotions.isEmpty()) {
                Item jujuPotion = jujuPotions.first();
                String itemName = jujuPotion.getName();
                int itemSlot = jujuPotion.getSlot();
                component(2, itemSlot, 33882307);
                logBlue("Withdrew: " + itemName + " from slot: " + itemSlot);
            } else {
                logBlue("Perfect Juju Woodcutting Potion not found in inventory.");
            }
            Execution.delay(random.nextLong(1200, 1500));
        } else {
            logError("Interface 517 is not open.");
        }
    }

    private static void handleFamiliarWithdrawals() {
        if (Interfaces.isOpen(517)) {
            logBlue("Attempting to withdraw a Beaver Pouch.");
            ResultSet<Item> beaverPouches = InventoryItemQuery.newQuery(95).ids(12021).results();
            if (!beaverPouches.isEmpty()) {
                Item beaverPouch = beaverPouches.first();
                String itemName = beaverPouch.getName();
                int itemSlot = beaverPouch.getSlot();
                component(2, itemSlot, 33882307);
                logBlue("Withdrew: " + itemName + " from slot: " + itemSlot);
            } else {
                logBlue("Beaver Pouch not found in inventory.");
            }
            Execution.delay(random.nextLong(1200, 1500));

            logBlue("Attempting to withdraw a Super Restore.");
            ResultSet<Item> superRestores = InventoryItemQuery.newQuery(95).name(superRestorePattern).results();
            if (!superRestores.isEmpty()) {
                Item superRestore = superRestores.first();
                String itemName = superRestore.getName();
                int itemSlot = superRestore.getSlot();
                component(2, itemSlot, 33882307);
                logBlue("Withdrew: " + itemName + " from slot: " + itemSlot);
            } else {
                logBlue("Super Restore not found in inventory.");
            }
            Execution.delay(random.nextLong(1200, 1500));
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
