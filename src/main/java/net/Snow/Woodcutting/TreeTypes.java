package net.Snow.Woodcutting;

import net.Snow.Navigation;
import net.botwithus.rs3.game.Coordinate;
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery;
import net.botwithus.rs3.game.queries.results.EntityResultSet;
import net.botwithus.rs3.game.scene.entities.object.SceneObject;
import net.botwithus.rs3.script.Execution;

import java.util.ArrayList;
import java.util.List;

import static net.Snow.Combat.Combat.player;
import static net.Snow.ImGui.CustomLogger.logGrey;
import static net.Snow.ImGui.CustomLogger.logWarning;
import static net.Snow.Navigation.MoveTo;
import static net.Snow.Snow.random;

public class TreeTypes {

    public static int selectedTreeIndex = 0;
    public static List<Navigation> treeTypes = new ArrayList<>();

    static {
        treeTypes.add(new Navigation("Tree", new Coordinate(3146, 3421, 0), "Varrock", 1));
        treeTypes.add(new Navigation("Oak", new Coordinate(2701, 3483, 0), "Seers' Village", 15));
        treeTypes.add(new Navigation("Oak", new Coordinate(3166, 3421, 0), "Varrock", 15));
        treeTypes.add(new Navigation("Willow", new Coordinate(2712, 3509, 0), "Seers' Village", 30));
        treeTypes.add(new Navigation("Willow", new Coordinate(3085, 3236, 0), "Draynor Village", 30));
        treeTypes.add(new Navigation("Maple Tree", new Coordinate(2725, 3500, 0), "Seers' Village", 40));
        treeTypes.add(new Navigation("Yew", new Coordinate(2715, 3462, 0), "Seers' Village", 70));
        treeTypes.add(new Navigation("Magic tree", new Coordinate(2699, 3424, 0), "Seers' Village", 80));
        treeTypes.add(new Navigation("Ivy", new Coordinate(3216, 3498, 0), "Grand Exchange", 68));
        treeTypes.add(new Navigation("Acadia tree", new Coordinate(3189, 2721, 0), "Menophos (Not VIP)", 50));
        treeTypes.add(new Navigation("Acadia tree", new Coordinate(3186, 2739, 0), "Menophos (VIP)", 50, List.of(
                TreeTypes::performMenaphosVipSteps
        )));
        treeTypes.add(new Navigation("Eternal magic tree", new Coordinate(2324, 3585, 0), "Piscatoris", 100));
    }

    public static void setSelectedTreeByNameAndLocation(String treeName, String locationName) {
        for (int i = 0; i < treeTypes.size(); i++) {
            if (treeTypes.get(i).getName().equals(treeName) && treeTypes.get(i).getLocationName().equals(locationName)) {
                selectedTreeIndex = i;
                break;
            }
        }
    }

    public static void executeAdditionalSteps() {
        Navigation selectedTree = treeTypes.get(selectedTreeIndex);
        selectedTree.getAdditionalSteps().forEach(Runnable::run);
    }

    private static void performMenaphosVipSteps() {
        EntityResultSet<SceneObject> archway = SceneObjectQuery.newQuery().id(109014).option("Go through").results();

        logGrey("Archway found. Interacting with archway.");
        archway.first().interact("Go through");

        // Delay until the player has passed through the archway
        boolean passedThrough = Execution.delayUntil(
                random.nextLong(2000, 3000),
                () -> player.getCoordinate().equals(new Coordinate(3186, 2740, 0))
        );

        if (passedThrough) {
            // Move to the final fishing spot coordinate
            logGrey("Moving to the final fishing spot after passing through the archway.");
            MoveTo(new Coordinate(3180, 2746, 0));
        } else {
            logWarning("Failed to pass through the archway. Retrying...");
            // You may want to handle retry logic here if needed
        }

        logGrey("Arrived at Menaphos VIP Area.");
    }
}
