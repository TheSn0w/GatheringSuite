package net.Snow.Mining;

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

import java.util.List;
import java.util.stream.Collectors;

import static net.Snow.ImGui.CustomLogger.logBlue;
import static net.Snow.ImGui.CustomLogger.logError;
import static net.Snow.Mining.RockTypes.rockTypes;
import static net.Snow.Mining.RockTypes.selectedRockIndex;
import static net.Snow.Snow.random;

public class Crystallise {

    public static boolean applyCrystallise(SceneObject rock, LocalPlayer player) {
        // Check if the rock is within 3 tiles of the player
        if (rock.getCoordinate().distanceTo(player.getCoordinate()) > 3) {
            logError("Rock is not within 3 tiles of the player. Cannot cast Crystallise.");
            return false;
        }

        SpotAnimation crystallise = SpotAnimationQuery.newQuery().ids(5802, 5804).results().first();
        if (crystallise == null) {
            logBlue("No existing crystallise animation found. Proceeding to cast Crystallise.");

            // Cast Crystallise on the rock
            Component spell = ComponentQuery.newQuery(1430, 1671, 1672, 1673, 1670).spriteId(25939).results().first();
            if (spell != null) {
                logBlue("Crystallise spell component found. Interacting with spell component.");
                MiniMenu.interact(SelectableAction.SELECTABLE_COMPONENT.getType(), 0, -1, (spell.getInterfaceIndex() << 16 | spell.getComponentIndex()));
                logBlue("Interacted with spell component. Delaying for a random period.");
                Execution.delay(random.nextLong(950, 1250));

                logBlue("Interacting with rock to cast Crystallise.");
                MiniMenu.interact(SelectableAction.SELECT_OBJECT.getType(), rock.getId(), rock.getCoordinate().getX(), rock.getCoordinate().getY());
                Execution.delay(random.nextLong(950, 1250));
                return true;
            } else {
                logError("Crystallise spell component not found.");
            }
        } else {
            logBlue("Existing crystallise animation found. No need to cast Crystallise.");
        }
        return false;
    }

    public static SceneObject findRockForCrystallise(LocalPlayer player) {
        Navigation selectedRock = rockTypes.get(selectedRockIndex);
        if (selectedRock.getName().equals("Disabled")) {
            logError("No rock type selected.");
            return null;
        }

        EntityResultSet<SceneObject> results = SceneObjectQuery.newQuery().name(selectedRock.getName()).option("Mine").hidden(false).results();
        List<SceneObject> rockList = results.stream().collect(Collectors.toList());
        List<SceneObject> validRocks = findClosestValidRocks(rockList, player);

        if (!validRocks.isEmpty()) {
            return validRocks.get(0);
        } else {
            logError("No valid rocks found.");
            return null;
        }
    }

    private static List<SceneObject> findClosestValidRocks(List<SceneObject> rocks, LocalPlayer player) {
        return rocks.stream()
                .filter(rock -> rock.getCoordinate().distanceTo(player.getCoordinate()) <= 3)
                .sorted((rock1, rock2) -> Double.compare(rock1.getCoordinate().distanceTo(player.getCoordinate()), rock2.getCoordinate().distanceTo(player.getCoordinate())))
                .collect(Collectors.toList());
    }
}
