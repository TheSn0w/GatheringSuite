package net.Snow.Thieving;

import net.Snow.Banking.BankLocations;
import net.botwithus.rs3.game.Item;
import net.botwithus.rs3.game.inventories.Backpack;
import net.botwithus.rs3.game.queries.builders.items.InventoryItemQuery;
import net.botwithus.rs3.game.queries.results.ResultSet;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.script.Execution;

import static net.Snow.ImGui.CustomLogger.logError;
import static net.Snow.ImGui.CustomLogger.logWarning;
import static net.Snow.Snow.random;

public class Food {

    public static void eatFood(LocalPlayer player) {
        if (isPlayerEating(player) || isHealthAboveThreshold(player)) {
            return;
        }

        Execution.delay(healHealth(player));
    }

    private static boolean isPlayerEating(LocalPlayer player) {
        boolean isEating = player.getAnimationId() == 18001;
//        logOrange("Player is eating: " + isEating);
        return isEating;
    }

    public static int healthThreshold = 80; // Default health threshold

    private static boolean isHealthAboveThreshold(LocalPlayer player) {
        double healthPercentage = calculateHealthPercentage(player);
        boolean isAboveThreshold = healthPercentage > healthThreshold;
//        logBlue("Health percentage: " + healthPercentage + ", threshold: " + healthThreshold + ", above threshold: " + isAboveThreshold);
        return isAboveThreshold;
    }

    public static double calculateHealthPercentage(LocalPlayer player) {
        double currentHealth = player.getCurrentHealth();
        double maximumHealth = player.getMaximumHealth();
        double healthPercentage = maximumHealth > 0 ? (currentHealth / maximumHealth) * 100 : 0;
//        logBlue("Calculated health percentage: " + healthPercentage);
        return healthPercentage;
    }

    private static long healHealth(LocalPlayer player) {
        Item food = findFoodInInventory();

        if (food == null) {
            handleNoFoodFound(player);
            return random.nextLong(1500, 3000);
        }

        return eatFoodItem(food);
    }

    private static Item findFoodInInventory() {
        ResultSet<Item> foodItems = InventoryItemQuery.newQuery(93).option("Eat").results();
        Item food = foodItems.isEmpty() ? null : foodItems.first();
        logWarning("Food found: " + (food != null ? food.getName() : "None"));
        return food;
    }

    private static void handleNoFoodFound(LocalPlayer player) {
        logError("No food found. Banking for food.");
        BankLocations.goToBankToLoadLastPresetfrom();
    }

    private static long eatFoodItem(Item food) {
        boolean eatSuccess = Backpack.interact(food.getName(), "Eat");

        if (eatSuccess) {
            logWarning("Successfully ate " + food.getName());
        } else {
            logWarning("Failed to eat.");
        }
        return 0;
    }
}
