package net.Snow.AbyssRunecrafting;

import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.api.game.hud.inventories.Bank;
import net.botwithus.rs3.game.Client;
import net.botwithus.rs3.game.Item;
import net.botwithus.rs3.game.hud.interfaces.Interfaces;
import net.botwithus.rs3.game.queries.builders.items.InventoryItemQuery;
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery;
import net.botwithus.rs3.game.queries.results.EntityResultSet;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.game.scene.entities.object.SceneObject;
import net.botwithus.rs3.game.vars.VarManager;
import net.botwithus.rs3.script.Execution;

import java.util.regex.Pattern;

import static net.Snow.AbyssRunecrafting.AbyssRunecrafting.setBotState;
import static net.Snow.AbyssRunecrafting.AbyssRunecraftingGraphics.useFamiliar;
import static net.Snow.ImGui.CustomLogger.*;
import static net.Snow.Snow.component;
import static net.Snow.Snow.random;

public class Familiar {

    public static void checkFamiliar() {
        if (VarManager.getVarbitValue(6055) <= 1) {
            summonFamiliar();
        }
    }

    private static void summonFamiliar() {
        SceneObject bank = findBank();
        if (bank != null) {
            interactWithBank(bank);
        }
    }

    private static SceneObject findBank() {
        EntityResultSet<SceneObject> bankResults = SceneObjectQuery.newQuery().id(90950).option("Search").results();
        if (bankResults.isEmpty()) {
            return null;
        }
        SceneObject bank = bankResults.nearest();
        if (bank != null) {
            logOrange("Found " + bank.getName());
        }
        return bank;
    }

    private static void interactWithBank(SceneObject bank) {
        Execution.delay(random.nextLong(1500, 2000));
        if (bank.interact("Use")) {
            logOrange("Interacting with " + bank.getName() + " using 'Bank'.");
            if (waitForBankInterface()) {
                performBankingActions();
            }
        }
    }

    private static boolean waitForBankInterface() {
        Execution.delayUntil(15000, () -> Interfaces.isOpen(517));
        if (Interfaces.isOpen(517)) {
            logOrange("Bank interface is open.");
            Execution.delay(random.nextLong(600, 800));
            return true;
        }
        return false;
    }

    private static void performBankingActions() {
        Bank.depositAll();
        logOrange("Deposited all items.");
        Execution.delay(random.nextLong(600, 800));
        interactWithComponents();
        boolean itemsWithdrawn = withdrawItems();
        Execution.delay(random.nextLong(600, 800));
        if (!itemsWithdrawn) {
            logError("Failed to withdraw necessary items. Aborting banking actions.");
            return;
        }
        closeBank();
    }

    private static void interactWithComponents() {
        if (VarManager.getVarbitValue(45141) != 1) {
            component(1, -1, 33882277);
            logOrange("Interacting with component: " + VarManager.getVarbitValue(45141));
            Execution.delay(random.nextLong(600, 800));
        }
        if (VarManager.getVarbitValue(45189) != 2) {
            component(1, -1, 33882205);
            logOrange("Interacting with component: " + VarManager.getVarbitValue(45189));
            Execution.delay(random.nextLong(600, 800));
        }
    }

    private static final Pattern superRestorePattern = Pattern.compile("super restore.*", Pattern.CASE_INSENSITIVE);
    public static final Pattern familiarPattern = Pattern.compile("Abyssal parasite|Abyssal lurker|Abyssal titan", Pattern.CASE_INSENSITIVE);


    private static boolean withdrawItems() {
        LocalPlayer player = Client.getLocalPlayer();
        boolean restoreWithdrawn = true;
        boolean restoreInInventory = true;

        if (player.getSummoningPoints() < 100) {
            restoreWithdrawn = Bank.withdraw(superRestorePattern, 2);
            Execution.delay(random.nextLong(1200, 1500));
            logSuccess("Attempted to withdraw Super Restore: " + Backpack.contains(superRestorePattern));

            restoreInInventory = !InventoryItemQuery.newQuery(93).name(superRestorePattern).results().isEmpty();
        } else {
            logSuccess("Summoning points are over 100. Skipping Super Restore withdrawal.");
        }

        boolean familiarWithdrawn = Bank.withdraw(familiarPattern, 2);
        Execution.delay(random.nextLong(1200, 1500));
        logOrange("Attempted to withdraw Familiar: " + Backpack.contains(familiarPattern));

        boolean familiarInInventory = !InventoryItemQuery.newQuery(93).name(familiarPattern).results().isEmpty();

        boolean result = familiarWithdrawn && restoreWithdrawn && restoreInInventory && familiarInInventory;

        if (!result) {
            useFamiliar = false;
            logError("Familiar or Super Restore not found in bank or inventory. Disabling familiar usage.");
        }

        logSuccess("Withdrawal result: " + result);
        return result;
    }

    private static void performPostBankingActions() {
        LocalPlayer player = Client.getLocalPlayer();

        if (player.getSummoningPoints() < 100) {
            Item superRestoreItem = Backpack.getItem(superRestorePattern);
            if (superRestoreItem != null && Backpack.interact(superRestoreItem.getName(), "Drink")) {
                logSuccess("Drinking: " + superRestoreItem.getName());
                Execution.delay(random.nextLong(600, 800));
            }
        }

        if (player.getSummoningPoints() >= 100) {
            Item familiarItem = Backpack.getItem(familiarPattern);
            if (familiarItem != null && Backpack.interact(familiarItem.getName(), "Summon")) {
                logSuccess("Summoning: " + familiarItem.getName());
                Execution.delay(random.nextLong(600, 800));
            }
        }

        setBotState(AbyssState.BANKING);
    }

    private static void closeBank() {
        Execution.delay(random.nextLong(600, 800));
        while (Bank.isOpen()) {
            Bank.close();
            Execution.delayUntil(random.nextLong(15000, 20000), () -> !Bank.isOpen());
            log("[Runecrafting] Attempted to close bank.");
        }
        log("[Runecrafting] Bank is closed.");
        if (!Bank.isOpen()) {
            if (!useFamiliar) {
                setBotState(AbyssState.BANKING);
                return;
            }
            performPostBankingActions();
        }
    }
}
