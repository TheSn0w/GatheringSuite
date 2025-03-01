package net.Snow.Spells;

import net.Snow.Banking.BankLocations;
import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.rs3.game.Item;
import net.botwithus.rs3.game.actionbar.ActionBar;
import net.botwithus.rs3.game.queries.builders.items.InventoryItemQuery;
import net.botwithus.rs3.game.queries.results.ResultSet;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.game.vars.VarManager;
import net.botwithus.rs3.script.Execution;

import static net.Snow.ImGui.CustomLogger.*;
import static net.Snow.Snow.random;

public class Lightform {

    public static boolean useLightForm = false;

    public static void applyLightForm(LocalPlayer player) {
        if (useLightForm) {
            if (player.getPrayerPoints() > 0 && VarManager.getVarbitValue(29066) != 1) {
                ActionBar.usePrayer("Light Form");
                logSuccess("Enabling Light Form");
                Execution.delay(random.nextLong(50, 350));
            } else if (player.getPrayerPoints() <= random.nextInt(500, 1000)) {
                logBlue("Prayer points are: " + player.getPrayerPoints() + ". Attempting to drink a prayer or restore potion.");
                ResultSet<Item> items = InventoryItemQuery.newQuery(93).results();

                Item prayerOrRestorePot = items.stream()
                        .filter(item -> item.getName() != null &&
                                (item.getName().toLowerCase().contains("prayer") ||
                                        item.getName().toLowerCase().contains("restore")))
                        .findFirst()
                        .orElse(null);

                if (prayerOrRestorePot != null) {

                    boolean success = Backpack.interact(prayerOrRestorePot.getName(), "Drink");
                    if (success) {
                        logSuccess("Successfully drank " + prayerOrRestorePot.getName());
                        Execution.delay(random.nextLong(1250, 1500));
                    } else {
                        logError("Failed to interact with " + prayerOrRestorePot.getName());
                    }
                } else {
                    if (BankLocations.selectedBankIndex == 0) {
                        logError("Banking turned off but we have no prayer points for Lightform");
                    } else {
                        BankLocations.goToBankToUseOrBank();
                    }
                }
            }
        }
    }

    public static void disableLightForm() {
        if (VarManager.getVarbitValue(29066) == 1) {
            ActionBar.usePrayer("Light Form");
            logBlue("Disabling Light Form");
            Execution.delay(random.nextLong(50, 350));
        }
    }
}
