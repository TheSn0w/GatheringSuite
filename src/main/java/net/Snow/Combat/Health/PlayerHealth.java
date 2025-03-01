package net.Snow.Combat.Health;

import net.Snow.Banking.BankLocations;
import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.api.game.hud.inventories.LootInventory;
import net.botwithus.rs3.game.Item;
import net.botwithus.rs3.game.js5.types.configs.ConfigManager;
import net.botwithus.rs3.game.queries.builders.items.GroundItemQuery;
import net.botwithus.rs3.game.queries.builders.items.InventoryItemQuery;
import net.botwithus.rs3.game.queries.results.ResultSet;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.game.scene.entities.item.GroundItem;
import net.botwithus.rs3.script.Execution;
import net.botwithus.rs3.util.RandomGenerator;

import static net.Snow.Combat.Variables.getHealthPointsThreshold;
import static net.Snow.Combat.Variables.healthPointsThreshold;
import static net.Snow.ImGui.CustomLogger.logSuccess;
import static net.Snow.ImGui.CustomLogger.logWarning;
import static net.Snow.Snow.random;

public class PlayerHealth {

    public static void eatFood(LocalPlayer player) {
        // Check if the player is eating or if health is above the threshold
        if (isPlayerEating(player) || isHealthAboveThreshold(player)) {
            return;
        }

        // Attempt to heal the player by eating food
        if (!tryHealFromBackpack()) {
            if (!tryHealFromGround()) {
                logWarning("No food found.");
                BankLocations.goToBankToLoadLastPresetfrom();
            }
        }
    }

    private static boolean isPlayerEating(LocalPlayer player) {
        return player.getAnimationId() == 18001;
    }

    private static boolean isHealthAboveThreshold(LocalPlayer player) {
        double healthPercentage = calculateHealthPercentage(player);
        return healthPercentage > getHealthPointsThreshold();
    }

    public static double calculateHealthPercentage(LocalPlayer player) {
        double currentHealth = player.getCurrentHealth();
        double maximumHealth = player.getMaximumHealth();

        if (maximumHealth == 0) {
            throw new ArithmeticException("Maximum health cannot be zero.");
        }

        return (currentHealth / maximumHealth) * 100;
    }

    private static boolean tryHealFromBackpack() {
        ResultSet<Item> foodItems = InventoryItemQuery.newQuery(93).option("Eat").results();
        Item food = foodItems.isEmpty() ? null : foodItems.first();

        if (food != null && Backpack.interact(food.getName(), "Eat")) {
            logSuccess("Successfully ate " + food.getName());
            Execution.delay(RandomGenerator.nextInt(600, 650));
            return true;
        }

        return false;
    }

    private static boolean tryHealFromGround() {
        ResultSet<GroundItem> groundFoodItems = GroundItemQuery.newQuery().results();
        GroundItem groundFood = groundFoodItems.stream()
                .filter(item -> {
                    var itemType = ConfigManager.getItemType(item.getId());
                    return itemType != null && itemType.getBackpackOptions().contains("Eat");
                })
                .findFirst()
                .orElse(null);

        if (groundFood != null) {
            if (Backpack.isFull()) {
                return false;
            }

            if (LootInventory.isOpen()) {
                LootInventory.take(groundFood.getName());
            } else {
                groundFood.interact("Take");
                Execution.delayUntil(random.nextLong(15000), LootInventory::isOpen);
                return false; // Delay to allow the inventory to open before continuing
            }

            Item food = InventoryItemQuery.newQuery().ids(groundFood.getId()).results().first();
            if (food != null && Backpack.interact(food.getName(), "Eat")) {
                logSuccess("Successfully ate " + food.getName());
                Execution.delay(random.nextInt(600, 650));
                return true;
            }
        }

        return false;
    }


    public static boolean isHealthLow(LocalPlayer player) {
        double healthPercentage = calculateHealthPercentage(player);
        return healthPercentage < healthPointsThreshold;
    }
}
