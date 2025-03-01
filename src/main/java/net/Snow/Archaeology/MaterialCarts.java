package net.Snow.Archaeology;

import net.botwithus.rs3.game.Area;
import net.botwithus.rs3.game.Client;
import net.botwithus.rs3.game.Coordinate;
import net.botwithus.rs3.game.Distance;
import net.botwithus.rs3.game.movement.Movement;
import net.botwithus.rs3.game.movement.NavPath;
import net.botwithus.rs3.game.movement.TraverseEvent;
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery;
import net.botwithus.rs3.game.queries.results.EntityResultSet;
import net.botwithus.rs3.game.scene.entities.object.SceneObject;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static net.Snow.ImGui.CustomLogger.*;

public class MaterialCarts {

    public static final EntityResultSet<SceneObject> materialCarts;

    static {
        logGrey("Initializing MaterialCarts...");
        materialCarts = SceneObjectQuery.newQuery()
                .name("Materials cart")
                .option("Deposit materials")
                .results();
        logGrey("MaterialCarts initialized with " + materialCarts.size() + " results.");
    }

    public static SceneObject getNearestReachableMaterialCart() {
        logGrey("Finding nearest reachable material cart within 25 tiles...");

        // Get player's current coordinate
        Coordinate playerCoordinate = Client.getLocalPlayer().getCoordinate();

        // Filter carts within 25-tile radius of the player
        List<SceneObject> cartsWithinRadius = materialCarts.stream()
                .filter(cart -> Distance.between(playerCoordinate, cart.getCoordinate()) <= 50)
                .toList();

        logOrange("Found " + cartsWithinRadius.size() + " material carts within 50 tiles radius.");

        // Find the closest reachable material cart
        SceneObject closestCart = null;
        double closestDistance = Double.MAX_VALUE;

        for (SceneObject cart : cartsWithinRadius) {
            if (isCartReachable(cart)) {
                double distance = Distance.between(playerCoordinate, cart.getCoordinate());
                logOrange("Material cart at " + cart.getCoordinate() + " is reachable with distance: " + distance);
                if (distance < closestDistance) {
                    closestDistance = distance;
                    closestCart = cart;
                }
            } else {
                logError("Material cart at " + cart.getCoordinate() + " is not reachable.");
            }
        }

        if (closestCart != null) {
            logBlue("Nearest reachable material cart found at: " + closestCart.getCoordinate() + " with distance: " + closestDistance);
        } else {
            logGrey("No reachable material cart found within the specified radius.");
        }

        return closestCart;
    }

    public static boolean isCartReachable(SceneObject cart) {
        logGrey("Checking reachability for Material cart at: " + cart.getCoordinate());

        Set<Coordinate> surroundingTiles = getSurroundingTiles(cart);
        for (Coordinate tile : surroundingTiles) {
            TraverseEvent.State traverseState = Movement.traverse(NavPath.resolve(tile));

            if (traverseState == TraverseEvent.State.FINISHED) {
                log("Reachable tile found at: " + tile);
                return true;
            } else {
                log("Tile at: " + tile + " is not reachable. State: " + traverseState);
            }
        }
        logGrey("No reachable tiles found around the Material cart.");
        return false;
    }

    public static Set<Coordinate> getSurroundingTiles(SceneObject object) {
        logGrey("Calculating surrounding tiles for object: " + object.getName());
        Area area;
        if (object == null || (area = object.getArea()) == null) {
            logGrey("Object or area is null, returning empty set.");
            return Collections.emptySet();
        }

        Set<Coordinate> result = new HashSet<>();
        for (Coordinate tile : area.getCoordinates()) {
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    result.add(tile.derive(i, j, 0));
                }
            }
        }
        logBlue("Surrounding tiles calculated: " + result.size() + " tiles.");
        return result;
    }
}