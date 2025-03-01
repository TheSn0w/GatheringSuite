// Navigation.java
package net.Snow;

import net.botwithus.rs3.game.Coordinate;
import net.botwithus.rs3.game.movement.Movement;
import net.botwithus.rs3.game.movement.NavPath;
import net.botwithus.rs3.game.movement.TraverseEvent;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.script.Execution;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static net.Snow.ImGui.CustomLogger.*;
import static net.Snow.ImGui.SnowsTemplateGraphics.selectedActivityIndex;
import static net.Snow.Snow.random;

public class Navigation {
    private String name;
    private Coordinate coordinate;
    private String locationName;
    private int minimumLevel;
    private List<Runnable> additionalSteps;

    public Navigation(String name, Coordinate coordinate, String locationName, int minimumLevel) {
        this(name, coordinate, locationName, minimumLevel, List.of());
    }

    public Navigation(String name, Coordinate coordinate, String locationName, int minimumLevel, List<Runnable> additionalSteps) {
        this.name = name;
        this.coordinate = coordinate;
        this.locationName = locationName;
        this.minimumLevel = minimumLevel;
        this.additionalSteps = additionalSteps;
    }

    public String getName() {
        return name;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public String getLocationName() {
        return locationName;
    }

    public int getMinimumLevel() {
        return minimumLevel;
    }

    public List<Runnable> getAdditionalSteps() {
        return additionalSteps;
    }

    public boolean hasRequiredLevel(int playerLevel) {
        return playerLevel >= minimumLevel;
    }

    public void executeAdditionalSteps() {
        for (Runnable step : additionalSteps) {
            step.run();
        }
    }

    public static void MoveTo(Coordinate coordinates) {

        logSuccess("Traversing to: " + coordinates);

        TraverseEvent.State traverseState = Movement.traverse(NavPath.resolve(coordinates));


        if (traverseState == TraverseEvent.State.FINISHED) {
            logSuccess("Arrived at: " + coordinates);
        } else if (traverseState == TraverseEvent.State.NO_PATH) {
            logError("No path to: " + coordinates + " enter new destination.");
        } else if (traverseState == TraverseEvent.State.FAILED) {
            logError("Failed path to: " + coordinates + " enter new destination.");
        } else if (traverseState == TraverseEvent.State.INTERRUPTED) {
            logError("Path to: " + coordinates + " was interrupted, enter new destination.");
        } else {
            logError("Unexpected state: " + traverseState);
        }
    }

    public static void movePlayerAwayFromAction(LocalPlayer player) {
        if (selectedActivityIndex == 0) {

            if (player.getAnimationId() != -1) {

                Coordinate playerCoordinate = player.getCoordinate();
                Set<Coordinate> surroundingTiles = new HashSet<>();

                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        surroundingTiles.add(playerCoordinate.derive(i, j, 0));
                    }
                }

                for (Coordinate nearbyCoordinate : surroundingTiles) {
                    logBlue("Trying to move to: " + nearbyCoordinate);
                    if (nearbyCoordinate.isWalkable()) {
                        Movement.walkTo(nearbyCoordinate.getX(), nearbyCoordinate.getY(), true);
                        Execution.delay(random.nextLong(1500, 2500));

                        if (player.getCoordinate().equals(nearbyCoordinate)) {
                            logSuccess("Player has moved to the desired coordinate.");
                            break;
                        } else {
                            logWarning("Player has not moved to the desired coordinate. Trying the next one.");
                        }
                    }
                }
            }
        }
    }

    public static void movePlayerAway(LocalPlayer player) {

        Coordinate playerCoordinate = player.getCoordinate();
        Set<Coordinate> surroundingTiles = new HashSet<>();

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                surroundingTiles.add(playerCoordinate.derive(i, j, 0));
            }
        }

        for (Coordinate nearbyCoordinate : surroundingTiles) {
            logBlue("Trying to move to: " + nearbyCoordinate);
            if (nearbyCoordinate.isWalkable()) {
                Movement.walkTo(nearbyCoordinate.getX(), nearbyCoordinate.getY(), true);
                Execution.delay(random.nextLong(1500, 2500));

                if (player.getCoordinate().equals(nearbyCoordinate)) {
                    logSuccess("Player has moved to the desired coordinate.");
                    break;
                } else {
                    logWarning("Player has not moved to the desired coordinate. Trying the next one.");
                }
            }
        }
    }
}