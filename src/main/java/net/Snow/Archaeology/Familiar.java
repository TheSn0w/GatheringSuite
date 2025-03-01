package net.Snow.Archaeology;

import net.Snow.Banking.BankLocations;
import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.rs3.game.Item;
import net.botwithus.rs3.game.queries.builders.items.InventoryItemQuery;
import net.botwithus.rs3.game.queries.results.ResultSet;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.game.vars.VarManager;
import net.botwithus.rs3.script.Execution;

import static net.Snow.ImGui.CustomLogger.*;
import static net.Snow.Snow.random;

public class Familiar {

    private static int randomValue = 0;


    public static void handleFamiliar(LocalPlayer player) {
        randomValue = random.nextInt(2, 5);
        int familiarTime = VarManager.getVarbitValue(6055);

        if (familiarTime <= randomValue) {
            logOrange("Familiar time is " + familiarTime + ", we're going to summon.");

            ResultSet<Item> items = InventoryItemQuery.newQuery(93).results();
            Item restorePotion = items.stream()
                    .filter(item -> item.getName() != null &&
                            (item.getName().toLowerCase().contains("restore")))
                    .findFirst()
                    .orElse(null);

            if (player.getSummoningPoints() <= 1000) {
                if (restorePotion != null) {
                    boolean success = Backpack.interact(restorePotion.getName(), "Drink");
                    if (success) {
                        logSuccess("Successfully drank " + restorePotion.getName());
                        long delay = random.nextLong(1500, 3000);
                        Execution.delay(delay);
                    } else {
                        logError("Failed to interact with " + restorePotion.getName());
                    }
                } else {
                    logError(" No restore potions found in the backpack, teleporting to bank.");
                    BankLocations.goToBankToUseOrBank();
                }
            } else {
                ResultSet<Item> results = InventoryItemQuery.newQuery(93).option("Summon").results();
                if (VarManager.getVarbitValue(6055) >= randomValue) {
                    logGrey("Familiar is already summoned.");
                } else {
                    if (!results.isEmpty()) {
                        Item summonItem = results.first();
                        if (summonItem != null) {
                            String itemName = summonItem.getName();
                            Backpack.interact(itemName, "Summon");
                            Execution.delayUntil(5000, () -> VarManager.getVarbitValue(6055) > 10);
                            logSuccess("Summoned familiar: " + itemName);
                        }
                    } else {
                        logError("No Pouches found, using bank.");
                        BankLocations.goToBankToUseOrBank();
                    }
                }
            }
        }
    }
}
