package net.Snow.Combat.NPCLocation;

import net.botwithus.rs3.game.Area;
import net.botwithus.rs3.game.Coordinate;
import net.botwithus.rs3.game.movement.Movement;
import net.botwithus.rs3.game.movement.NavPath;
import net.botwithus.rs3.game.movement.TraverseEvent;

import java.util.List;

import static net.Snow.ImGui.CustomLogger.logError;
import static net.Snow.ImGui.CustomLogger.logSuccess;

public class CombatNavigation {

    private String name;
    private Coordinate coordinate;
    private String locationName;
    private List<Runnable> additionalSteps;

    // Constructor
    public CombatNavigation(String name, Coordinate coordinate, String locationName) {
        this(name, coordinate, locationName, List.of());
    }

    public CombatNavigation(String name, Coordinate coordinate, String locationName, List<Runnable> additionalSteps) {
        this.name = name;
        this.coordinate = coordinate;
        this.locationName = locationName;
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
    public List<Runnable> getAdditionalSteps() {
        return additionalSteps;
    }

    public void executeAdditionalSteps() {
        for (Runnable step : additionalSteps) {
            step.run();
        }
    }

    public static boolean MoveToRandom(Coordinate coordinates) {
        // Generate a circular area with a 5-tile radius around the target coordinate
        Area.Circular area = new Area.Circular(coordinates, 5);

        // Get a random walkable coordinate within this area
        Coordinate randomTile = area.getRandomWalkableCoordinate();

        // If no walkable tile is found, fall back to the original coordinate
        if (randomTile == null) {
            randomTile = coordinates;
        }

        // Traverse to the selected coordinate (either randomTile or original coordinates)
        TraverseEvent.State traverseState = Movement.traverse(NavPath.resolve(randomTile));

        logSuccess("Traversing to: " + randomTile);

        // Check the traversal state and return accordingly
        switch (traverseState) {
            case FINISHED:
                logSuccess("Arrived at: " + randomTile);
                return true; // Success

            case NO_PATH:
                logError("No path to: " + randomTile + " enter new destination.");
                break;

            case FAILED:
                logError("Failed path to: " + randomTile + " enter new destination.");
                break;

            case INTERRUPTED:
                logError("Path to: " + randomTile + " was interrupted, enter new destination.");
                break;

            default:
                logError("Unexpected state: " + traverseState);
                break;
        }

        return false; // If any state other than FINISHED
    }


    public static boolean MoveToRandom(Area area) {
        Coordinate randomTile = area.getRandomWalkableCoordinate();

        if (randomTile == null) {
            randomTile = area.getRandomWalkableCoordinate();
        }

        TraverseEvent.State traverseState = Movement.traverse(NavPath.resolve(randomTile));

        logSuccess("Traversing to: " + randomTile);

        switch (traverseState) {
            case FINISHED:
                logSuccess("Arrived at: " + randomTile);
                return true;
            case NO_PATH:
                logError("No path to: " + randomTile + " enter new destination.");
                break;
            case FAILED:
                logError("Failed path to: " + randomTile + " enter new destination.");
                break;
            case INTERRUPTED:
                logError("Path to: " + randomTile + " was interrupted, enter new destination.");
                break;
            default:
                logError("Unexpected state: " + traverseState);
                break;
        }
        return false;
    }
}
