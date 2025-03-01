package net.Snow.Fishing;

import net.botwithus.rs3.game.hud.interfaces.Component;
import net.botwithus.rs3.game.minimenu.MiniMenu;
import net.botwithus.rs3.game.minimenu.actions.SelectableAction;
import net.botwithus.rs3.game.queries.builders.characters.NpcQuery;
import net.botwithus.rs3.game.queries.builders.components.ComponentQuery;
import net.botwithus.rs3.game.queries.results.EntityResultSet;
import net.botwithus.rs3.game.scene.entities.characters.npc.Npc;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.script.Execution;

import java.util.List;
import java.util.stream.Collectors;

import static net.Snow.ImGui.CustomLogger.*;
import static net.Snow.Snow.random;

public class Crystallise {

    public static boolean applyCrystallise(Npc fishNpc, LocalPlayer player) {
        // Check if Crystallise is active and player is currently performing an animation
        if (isCrystalliseActive() && player.getAnimationId() != -1) {
            logBlue("Crystallise is active, and the player is currently performing an animation. No further interaction needed.");
            return false;
        }

        // Check if the NPC is within 3 tiles of the player
        if (fishNpc.getCoordinate().distanceTo(player.getCoordinate()) > 3) {
            logError("Fishing spot is not within 3 tiles of the player. Cannot cast Crystallise.");
            return false;
        }

        logBlue("No existing Crystallise animation found or player is idle. Proceeding to cast Crystallise.");

        // Find the Crystallise spell component
        Component spell = ComponentQuery.newQuery(1430, 1671, 1672, 1673, 1670)
                .spriteId(25939)
                .results()
                .first();

        if (spell != null) {
            logGrey("Crystallise spell component found. Interacting with spell component.");
            boolean spellInteracted = MiniMenu.interact(SelectableAction.SELECTABLE_COMPONENT.getType(), 0, -1,
                    (spell.getInterfaceIndex() << 16 | spell.getComponentIndex()));

            if (spellInteracted) {
                logGrey("Interacted with spell component. Delaying for a random period.");
                Execution.delay(random.nextLong(400, 600));

                logGrey("Interacting with fishing spot to cast Crystallise.");
                boolean npcInteracted = MiniMenu.interact(SelectableAction.SELECT_NPC.getType(), fishNpc.getId(),
                        fishNpc.getCoordinate().getX(), fishNpc.getCoordinate().getY());

                if (npcInteracted) {
                    logSuccess("Successfully cast Crystallise. Now interacting with fishing spot.");
                    Execution.delay(random.nextLong(400, 600));

                    return true;
                } else {
                    logError("Failed to interact with the fishing NPC to cast Crystallise.");
                }
            } else {
                logError("Failed to interact with the Crystallise spell component.");
            }
        } else {
            logError("Crystallise spell component not found.");
        }
        return false;
    }

    //private static boolean isCrystalliseActive() {
    //        // Query for an NPC with Crystallise spot animations
    //        Npc crystalliseNpc = NpcQuery.newQuery()
    //                .spotAnimation(5802) // Crystallise spot animation ID
    //                .results()
    //                .first();
    //
    //        if (crystalliseNpc == null) {
    //            crystalliseNpc = NpcQuery.newQuery()
    //                    .spotAnimation(5804) // Alternate Crystallise spot animation ID
    //                    .results()
    //                    .first();
    //        }
    //
    //        return crystalliseNpc != null;
    //    }

    private static boolean isCrystalliseActive() {
        // Query for an NPC with Crystallise spot animations
        Npc crystalliseNpc = getCrystalliseNpc();

        return crystalliseNpc != null;
    }


    public static Npc findFishForCrystallise(LocalPlayer player) {
        FishingSpot selectedSpot = FishingTypes.fishingSpots.get(FishingTypes.selectedFishIndex);
        if ("Disabled".equals(selectedSpot.getSpotNames())) {
            logError("No fishing spot selected.");
            return null;
        }

        EntityResultSet<Npc> results = NpcQuery.newQuery()
                .name(selectedSpot.getSpotNames().toArray(new String[0]))
                .option(selectedSpot.getFishingMethod())
                .results();

        List<Npc> fishNpcList = results.stream().collect(Collectors.toList());
        List<Npc> validFishNpcs = findClosestValidFishingSpots(fishNpcList, player);

        if (!validFishNpcs.isEmpty()) {
            return validFishNpcs.get(0);
        } else {
            logError("No valid fishing spots found.");
            return null;
        }
    }

    private static List<Npc> findClosestValidFishingSpots(List<Npc> fishNpcs, LocalPlayer player) {
        return fishNpcs.stream()
                .filter(fishNpc -> {
                    // Verify that the distanceTo method works correctly
                    double distance = fishNpc.getCoordinate().distanceTo(player.getCoordinate());
                    return distance <= 3;
                })
                .sorted((npc1, npc2) -> {
                    // Check the return type of distanceTo
                    double distance1 = npc1.getCoordinate().distanceTo(player.getCoordinate());
                    double distance2 = npc2.getCoordinate().distanceTo(player.getCoordinate());

                    // Compare the distances
                    return Double.compare(distance1, distance2);
                })
                .collect(Collectors.toList());
    }


    public static Npc getCrystalliseNpc() {
        // Check for the NPC with the Crystallise spot animation
        Npc crystalliseNpc = NpcQuery.newQuery()
                .spotAnimation(5802) // Crystallise spot animation ID
                .results()
                .first();

        if (crystalliseNpc == null) {
            crystalliseNpc = NpcQuery.newQuery()
                    .spotAnimation(5804) // Another potential Crystallise spot animation ID
                    .results()
                    .first();
        }
        return crystalliseNpc;
    }

}
