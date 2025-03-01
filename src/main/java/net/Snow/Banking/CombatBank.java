package net.Snow.Banking;

import net.botwithus.api.game.hud.inventories.Bank;
import net.botwithus.rs3.game.Item;
import net.botwithus.rs3.game.queries.builders.items.InventoryItemQuery;
import net.botwithus.rs3.game.queries.results.ResultSet;
import net.botwithus.rs3.script.Execution;

import static net.Snow.Banking.BankLocations.checkBankTabs;
import static net.Snow.ImGui.CustomLogger.*;
import static net.Snow.Snow.random;

public class CombatBank {

    public static void depositAll() {
        try {
            // Check if the bank is open
            if (Bank.isOpen()) {
                logWarning("Bank is open.");

                // Add a small delay to simulate a natural interaction
                Execution.delay(random.nextLong(500, 1000));
                logWarning("Delay added.");

                // Check bank tabs or perform any necessary setup (if required)
                checkBankTabs();
                logWarning("Checked bank tabs.");

                // Query for items in the backpack that have the "Eat" option
                ResultSet<Item> backpackFoodItems = InventoryItemQuery.newQuery(95).option("Eat").results();
                logWarning("Queried Bank for food items.");

                if (backpackFoodItems.isEmpty()) {
                    logError("No food items found in the backpack.");
                } else {
                    logWarning("Starting to search for food items in the backpack.");
                    backpackFoodItems.forEach(foodItem -> logWarning("Found food item: " + foodItem.getName()));

                    // Withdraw the first item found with the "Eat" option, if any
                    backpackFoodItems.stream().findFirst().ifPresent(foodItem -> {
                        logWarning("Attempting to withdraw food item: " + foodItem.getName());
                        if (Bank.withdraw(foodItem.getName(), 1)) {
                            logSuccess("Withdrew " + foodItem.getName() + " from the bank.");
                            logWarning("Starting delay after withdrawing item.");
                            Execution.delay(random.nextLong(500, 1000));
                            logWarning("Delay added after withdrawing item.");
                        } else {
                            logError("Failed to withdraw " + foodItem.getName());
                        }
                    });
                    logWarning("Finished searching for food items in the backpack.");
                }
                // Close the bank
                Bank.close();
                logWarning("Bank closed.");
            } else {
                logError("Bank is not open.");
            }
        } catch (Exception e) {
            logError("An error occurred during the deposit process: " + e.getMessage());
        }}

}
