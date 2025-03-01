package net.Snow.Combat.Health;

import net.Snow.Banking.BankLocations;
import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.rs3.game.Item;
import net.botwithus.rs3.game.queries.builders.items.InventoryItemQuery;
import net.botwithus.rs3.game.queries.results.ResultSet;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.game.vars.VarManager;
import net.botwithus.rs3.script.Execution;

import java.util.regex.Pattern;

import static net.Snow.Combat.Combat.useBank;
import static net.Snow.Combat.Variables.prayerPointsThreshold;
import static net.Snow.ImGui.CustomLogger.*;
import static net.Snow.Snow.random;

public class Potions {

    public static boolean useAgressionPotions = false;
    public static boolean usePrayerPotions = false;
    public static boolean useOverloads = false;
    public static boolean useWeaponPoison = false;

    public static boolean useAggression(LocalPlayer player) {
        if (!useAgressionPotions || player == null || !player.inCombat() || player.getAnimationId() == 18000 || VarManager.getVarbitValue(33448) != 0) {
            return true; // No need to go to the bank, continue execution
        }

        ResultSet<Item> results = InventoryItemQuery.newQuery(93)
                .name("Aggression", String::contains)
                .option("Drink")
                .results();

        if (results.isEmpty()) {
            logError("No aggression flasks found in the inventory.");
            if (useBank) {
                BankLocations.goToBankToLoadLastPresetfrom();
            }
            return false; // Need to go to the bank, stop further execution
        }

        Item aggressionFlask = results.first();
        if (aggressionFlask != null) {
            boolean success = Backpack.interact(aggressionFlask.getName(), "Drink");
            if (success) {
                logOrange("Using aggression potion: " + aggressionFlask.getName());
                Execution.delay(random.nextLong(600, 1000));
            } else {
                logError("Failed to use aggression potion: " + aggressionFlask.getName());
            }
        }

        return true; // Potion used successfully, continue execution
    }


    public static boolean usePrayerOrRestorePots(LocalPlayer player) {
        if (!usePrayerPotions || player == null || !player.inCombat() || player.getAnimationId() == 18000 || player.getPrayerPoints() > prayerPointsThreshold) {
            return true; // No need to go to the bank, continue execution
        }

        ResultSet<Item> items = InventoryItemQuery.newQuery(93).results();

        Item prayerOrRestorePot = items.stream()
                .filter(item -> item.getName() != null &&
                        (item.getName().toLowerCase().contains("prayer") ||
                                item.getName().toLowerCase().contains("restore")))
                .findFirst()
                .orElse(null);

        if (prayerOrRestorePot == null) {
            logError("No prayer or restore potions found in the backpack.");
            if (useBank) {
                BankLocations.goToBankToLoadLastPresetfrom();
            }
            return false; // Need to go to the bank, stop further execution
        }

        boolean success = Backpack.interact(prayerOrRestorePot.getName(), "Drink");
        if (success) {
            logSuccess("Successfully drank " + prayerOrRestorePot.getName());
            Execution.delay(random.nextLong(600, 1000));
        } else {
            logError("Failed to interact with " + prayerOrRestorePot.getName());
        }

        return true; // Potion used successfully, continue execution
    }

    public static boolean drinkOverloads(LocalPlayer player) {
        if (!useOverloads || player == null || VarManager.getVarbitValue(48834) != 0 || player.getAnimationId() == 18000) {
            return true; // No need to go to the bank, continue execution
        }

        Pattern overloadPattern = Pattern.compile("overload", Pattern.CASE_INSENSITIVE);

        Item overloadPot = InventoryItemQuery.newQuery(93)
                .results()
                .stream()
                .filter(item -> item.getName() != null && overloadPattern.matcher(item.getName()).find())
                .findFirst()
                .orElse(null);

        if (overloadPot == null) {
            logError("No overload potion found in the Backpack.");
            if (useBank) {
                BankLocations.goToBankToLoadLastPresetfrom();
            }
            return false; // Need to go to the bank, stop further execution
        }

        boolean success = Backpack.interact(overloadPot.getName(), "Drink");
        if (success) {
            logSuccess("Successfully drank " + overloadPot.getName());
            Execution.delay(random.nextLong(600, 1000));
        } else {
            log("[Error] Failed to interact with overload potion.");
        }

        return true; // Potion used successfully, continue execution
    }

    public static boolean useWeaponPoison(LocalPlayer player) {
        if (player == null || !useWeaponPoison || player.getAnimationId() == 18068 || VarManager.getVarbitValue(2102) > 3) {
            return true; // No need to go to the bank, continue execution
        }

        Pattern poisonPattern = Pattern.compile("weapon poison\\+*?", Pattern.CASE_INSENSITIVE);

        Item weaponPoisonItem = InventoryItemQuery.newQuery(93)
                .results()
                .stream()
                .filter(item -> item.getName() != null && poisonPattern.matcher(item.getName()).find())
                .findFirst()
                .orElse(null);

        if (weaponPoisonItem == null) {
            logError("No weapon poison found in the Backpack.");
            if (useBank) {
                BankLocations.goToBankToLoadLastPresetfrom();
            }
            return false; // Need to go to the bank, stop further execution
        }

        boolean success = Backpack.interact(weaponPoisonItem.getName(), "Apply");
        if (success) {
            logSuccess("Successfully applied " + weaponPoisonItem.getName());
            Execution.delayUntil(5000, () -> VarManager.getVarbitValue(2102) > 1);
            Execution.delay(random.nextLong(1250, 1500));
        } else {
            log("[Error] Failed to apply weapon poison.");
        }

        return true; // Poison applied successfully, continue execution
    }
}
