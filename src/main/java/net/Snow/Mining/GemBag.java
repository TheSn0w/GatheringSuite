package net.Snow.Mining;

import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.rs3.game.Item;
import net.botwithus.rs3.game.queries.builders.items.InventoryItemQuery;
import net.botwithus.rs3.script.Execution;

import java.util.Arrays;
import java.util.List;

import static net.Snow.ImGui.CustomLogger.logBlue;
import static net.Snow.Snow.random;

public class GemBag {

    private static final List<String> GEM_NAMES = Arrays.asList("Uncut sapphire", "Uncut emerald", "Uncut ruby", "Uncut diamond", "Uncut dragonstone");
    static final Item gemBag = InventoryItemQuery.newQuery(93).name("Gem bag", String::contains).results().first();



    static boolean fillGemBag() {
        // Check if the backpack contains any of the specified gems
        boolean containsGems = GEM_NAMES.stream().anyMatch(Backpack::contains);

        if (!containsGems) {
            return false; // Exit if no specified gems are found in the backpack
        }

        // Check if the gem bag is present in the backpack
        if (gemBag != null) {
            logBlue(gemBag.getName() + " found in the Backpack.");
            // Interact with the gem bag to fill it
            Backpack.interact(gemBag.getName(), "Fill");
            logBlue("Filling: " + gemBag.getName());
            // Delay to simulate filling time
            Execution.delay(random.nextLong(1500, 3000));
            return true; // Indicate that the gem bag was filled
        }
        return false; // Indicate that the gem bag was not filled
    }
}
