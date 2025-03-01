package net.Snow.Woodcutting;

import net.Snow.Navigation;
import net.botwithus.rs3.game.hud.interfaces.Component;
import net.botwithus.rs3.game.minimenu.MiniMenu;
import net.botwithus.rs3.game.minimenu.actions.SelectableAction;
import net.botwithus.rs3.game.queries.builders.animations.SpotAnimationQuery;
import net.botwithus.rs3.game.queries.builders.components.ComponentQuery;
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery;
import net.botwithus.rs3.game.queries.results.EntityResultSet;
import net.botwithus.rs3.game.scene.entities.animation.SpotAnimation;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.game.scene.entities.object.SceneObject;
import net.botwithus.rs3.script.Execution;

import static net.Snow.ImGui.CustomLogger.*;
import static net.Snow.Snow.random;
import static net.Snow.Woodcutting.TreeTypes.selectedTreeIndex;
import static net.Snow.Woodcutting.TreeTypes.treeTypes;

public class Crystallise {

    public static boolean useCrystallise = false;


    public static void applyCrystallise(SceneObject tree, LocalPlayer player) {
        // Check if the tree is within 3 tiles of the player
        if (tree.getCoordinate().distanceTo(player.getCoordinate()) > 3) {
            logError("Tree is not within 3 tiles of the player. Cannot cast Crystallise.");
            return;
        }

        SpotAnimation crystallise = SpotAnimationQuery.newQuery().ids(5802, 5804).results().first();
        if (crystallise == null) {
            logBlue("No existing crystallise animation found. Proceeding to cast Crystallise.");

            // Cast Crystallise on the tree
            Component spell = ComponentQuery.newQuery(1430, 1671, 1672, 1673, 1670).spriteId(25939).results().first();
            if (spell != null) {
                logBlue("Crystallise spell component found. Interacting with spell component.");
                MiniMenu.interact(SelectableAction.SELECTABLE_COMPONENT.getType(), 0, -1, (spell.getInterfaceIndex() << 16 | spell.getComponentIndex()));
                logBlue("Interacted with spell component. Delaying for a random period.");
                Execution.delay(random.nextLong(450, 850));

                logBlue("Interacting with tree to cast Crystallise.");
                MiniMenu.interact(SelectableAction.SELECT_OBJECT.getType(), tree.getId(), tree.getCoordinate().getX(), tree.getCoordinate().getY());
            } else {
                logError("Crystallise spell component not found.");
            }
        } else {
            logGrey("Existing crystallise animation found. No need to cast Crystallise.");
        }
    }

    public static SceneObject findTreeForCrystallise(LocalPlayer player) {
        Navigation selectedTree = treeTypes.get(selectedTreeIndex);

        if (selectedTree.getName().equals("Disabled")) {
            logError("No tree type selected.");
            return null;
        }

        // Determine the correct interaction option based on the selected tree
        String interactionOption = selectedTree.getName().equals("Ivy") ? "Chop" : "Chop down";
        if (selectedTree.getName().equals("Acadia tree")) {
            interactionOption = "Cut down";
        }

        // Query for trees matching the selected type and option
        EntityResultSet<SceneObject> results = SceneObjectQuery.newQuery()
                .name(selectedTree.getName())
                .option(interactionOption)
                .hidden(false)
                .results();

        // Find the nearest tree to the player
        SceneObject nearestTree = results.nearestTo(player);

        if (nearestTree != null) {
            logGrey("Found a valid tree for crystallising: " + nearestTree.getName());
            return nearestTree;
        } else {
            logError("No valid trees found.");
            return null;
        }
    }

}
