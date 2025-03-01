package net.Snow.Mining;

import net.Snow.Banking.BankLocations;
import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.rs3.game.Client;
import net.botwithus.rs3.game.Item;
import net.botwithus.rs3.game.queries.builders.items.InventoryItemQuery;
import net.botwithus.rs3.game.queries.results.ResultSet;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.script.Execution;

import static net.Snow.Banking.MiningBank.superRestorePattern;
import static net.Snow.ImGui.CustomLogger.*;
import static net.Snow.Snow.random;

public class Familiar {

    public static boolean useFamiliar = false;


    public static void summonFamiliar() {
        LocalPlayer player = Client.getLocalPlayer();

        if (player.getSummoningPoints() < 100) {
            Item superRestoreItem = Backpack.getItem(superRestorePattern);
            if (superRestoreItem != null && Backpack.interact(superRestoreItem.getName(), "Drink")) {
                logSuccess("Drinking: " + superRestoreItem.getName());
                Execution.delay(random.nextLong(600, 800));
            } else {
                if (BankLocations.selectedBankIndex == 0) {
                    logError("No super restore potions found in the inventory, you must have a bank selected to withdraw super restore potions.");
                } else {
                    logWarning("No super restore potions found in the inventory. Going to bank to withdraw.");
                    BankLocations.goToBankToUseOrBank();
                }
            }
        }

        if (player.getSummoningPoints() >= 100) {
            ResultSet<Item> familiarItems = InventoryItemQuery.newQuery(93).ids(49408).option("Summon").results();
            Item familiarItem = familiarItems.first();

            if (familiarItem == null) {
                familiarItems = InventoryItemQuery.newQuery(93).ids(12788).option("Summon").results();
                familiarItem = familiarItems.first();
            }

            if (familiarItem != null && Backpack.interact(familiarItem.getName(), "Summon")) {
                logSuccess("Summoning: " + familiarItem.getName());
                Execution.delay(random.nextLong(600, 800));
            } else {
                if (BankLocations.selectedBankIndex == 0) {
                    logError("No familiar pouches found in the inventory, you must have a bank selected to withdraw familiar pouches.");
                } else {
                    logWarning("No familiar pouches found in the inventory. Going to bank to withdraw.");
                    BankLocations.goToBankToUseOrBank();
                }
            }
        }
    }
}
