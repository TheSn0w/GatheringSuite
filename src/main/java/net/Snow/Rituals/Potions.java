package net.Snow.Rituals;

import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.rs3.game.Client;
import net.botwithus.rs3.game.Item;
import net.botwithus.rs3.game.queries.builders.items.InventoryItemQuery;
import net.botwithus.rs3.game.queries.results.ResultSet;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.game.scene.entities.characters.player.Player;
import net.botwithus.rs3.game.skills.Skills;
import net.botwithus.rs3.game.vars.VarManager;
import net.botwithus.rs3.script.Execution;
import net.botwithus.rs3.util.RandomGenerator;

import java.util.regex.Pattern;

import static net.Snow.ImGui.CustomLogger.logError;
import static net.Snow.ImGui.CustomLogger.logSuccess;
import static net.Snow.Rituals.NecromancyGraphics.getNecroThreshold;
import static net.Snow.Snow.random;
import static net.Snow.Snow.shutdown;

public class Potions {

    public static void drinkOverloads(LocalPlayer player) {
        Pattern overloadPattern = Pattern.compile("overload", Pattern.CASE_INSENSITIVE);
        if (VarManager.getVarbitValue(48834) <= 1 && player.getAnimationId() != 18000 && Backpack.contains(overloadPattern)) {

            Item overloadPot = InventoryItemQuery.newQuery(93)
                    .results()
                    .stream()
                    .filter(item -> item.getName() != null && overloadPattern.matcher(item.getName()).find())
                    .findFirst()
                    .orElse(null);

            if (overloadPot == null) {
                logError("No overload potion found.");
                shutdown();
                return;
            }

            boolean success = Backpack.interact(overloadPot.getName(), "Drink");
            if (success) {
                logSuccess("Successfully drank " + overloadPot.getName());
                Execution.delayUntil(5000, () -> VarManager.getVarbitValue(48834) != 0);
                Execution.delay(random.nextLong(2000, 3000));
            } else {
                logError("Failed to interact with overload potion.");
            }
        }
    }


    public static void necromancyPotion() {
        Player localPlayer = Client.getLocalPlayer();
        if (localPlayer != null) {
            if (Skills.NECROMANCY.getLevel() <= getNecroThreshold()) {
                ResultSet<Item> results = InventoryItemQuery.newQuery(93)
                        .name("necromancy", String::contains)
                        .option("Drink")
                        .results();
                if (!results.isEmpty()) {
                    Item necromancyItem = results.first();
                    if (necromancyItem != null) {
                        boolean success = Backpack.interact(necromancyItem.getName(), "Drink");
                        if (success) {
                            logSuccess("Using necromancy item: " + necromancyItem.getName());
                        } else {
                            logError("Failed to use necromancy item: " + necromancyItem.getName());
                        }
                        Execution.delay(RandomGenerator.nextInt(2000, 3000));
                    }
                } else {
                    logError("No necromancy potions found.");
                    shutdown();
                }
            }
        }
    }
}
