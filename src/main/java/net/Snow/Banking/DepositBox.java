package net.Snow.Banking;

import net.botwithus.rs3.game.Area;
import net.botwithus.rs3.game.Client;
import net.botwithus.rs3.game.Coordinate;
import net.botwithus.rs3.game.Distance;
import net.botwithus.rs3.game.hud.interfaces.Component;
import net.botwithus.rs3.game.hud.interfaces.Interfaces;
import net.botwithus.rs3.game.movement.Movement;
import net.botwithus.rs3.game.movement.NavPath;
import net.botwithus.rs3.game.movement.TraverseEvent;
import net.botwithus.rs3.game.queries.builders.components.ComponentQuery;
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery;
import net.botwithus.rs3.game.queries.results.EntityResultSet;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.game.scene.entities.object.SceneObject;
import net.botwithus.rs3.script.Execution;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static net.Snow.ImGui.CustomLogger.*;
import static net.Snow.Snow.component;
import static net.Snow.Snow.random;

public class DepositBox {

    public static final EntityResultSet<SceneObject> depositBoxes;

    static {
        logGrey("Initializing Deposit Box or Bank Deposit Box...");

        // Query for Deposit box first
        EntityResultSet<SceneObject> depositBoxResults = SceneObjectQuery.newQuery()
                .name("Deposit box")
                .results();

        // Check if Deposit box exists
        if (depositBoxResults.isEmpty()) {
            // If Deposit box is not found, query for Bank deposit box
            logGrey("Deposit box not found, querying for Bank deposit box...");
            depositBoxResults = SceneObjectQuery.newQuery()
                    .name("Bank deposit box")
                    .results();
        }

        depositBoxes = depositBoxResults;
        logGrey("Deposit Box initialized with " + depositBoxes.size() + " results.");
    }


    public static SceneObject getNearestReachableDepositBox() {
        logGrey("Checking if Deposit Box is within range...");
        LocalPlayer player = Client.getLocalPlayer();
        Coordinate playerCoordinate = player.getCoordinate();

        logOrange("Player Coordinate: " + playerCoordinate);

        // Iterate over each deposit box and check the distance
        for (SceneObject bank : depositBoxes) {
            double distance = Distance.between(playerCoordinate, bank.getCoordinate());
            logGrey("Distance to deposit box '" + bank.getName() + "': " + distance);

            // Check if the deposit box is within 25 units and reachable
            if (distance <= 25.0 && isBoxReachable(bank, playerCoordinate)) {
                return bank;
            }
        }

        logBlue("No reachable Deposit boxes within range.");
        return null;
    }


    public static boolean isBoxReachable(SceneObject cart, Coordinate playerCoordinate) {
        logGrey("Checking reachability for deposit box at: " + cart.getCoordinate());


            Set<Coordinate> surroundingTiles = getSurroundingTiles(cart);
            for (Coordinate tile : surroundingTiles) {
                // Use the Movement API to determine if the tile is reachable
                TraverseEvent.State traverseState = Movement.traverse(NavPath.resolve(tile));

                if (traverseState == TraverseEvent.State.FINISHED) {
                    log("Reachable tile found at: " + tile);
                    return true;
                } else {
                    log("Tile at: " + tile + " is not reachable. State: " + traverseState);
                }
            }
        logGrey("No reachable tiles found around the deposit box.");
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

    private static final String EXCLUDED_ITEMS_REGEX = "(?i).*\\b(Fishing bait|Feather|Bronze ore box|Iron ore box|Steel ore box|" +
    "Mithril ore box|Adamant ore box|Rune ore box|Orikalkum ore box|Necronium ore box|Bane ore box|Elder rune ore box|Primal ore box|Wood box|Oak wood box|Willow wood box|Teak wood box|" +
    "Maple wood box|Acadia wood box|Mahogany wood box|Yew wood box|Magic wood box|Elder wood box)\\b.*";

    public static void handleDepositBox(SceneObject nearestBox) {
        try {
            int bankInterfaceId = 11;

            if (nearestBox.interact("Deposit")) {
                logBlue("Interacted with Deposit Box: " + nearestBox.getName());

                if (Execution.delayUntil(random.nextLong(25000, 30000), () -> Interfaces.isOpen(bankInterfaceId))) {
                    log("Bank Deposit is open.");

                    var components = ComponentQuery.newQuery(bankInterfaceId).componentIndex(19).results();
                    var uniqueItems = components.stream()
                            .map(component -> component.getText().replaceAll("<[^>]*>", ""))
                            .filter(item -> !item.matches(EXCLUDED_ITEMS_REGEX))
                            .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

                    uniqueItems.forEach((item, count) -> {
                        int subComponentIndex = components.stream()
                                .filter(component -> component.getText().replaceAll("<[^>]*>", "").equals(item))
                                .findFirst()
                                .map(Component::getSubComponentIndex)
                                .orElse(-1);

                        if (subComponentIndex != -1) {
                            logBlue("Unique Item: " + item + ", Stack Size: " + count);
                            component(4, subComponentIndex, 720915);
                            Execution.delay(random.nextLong(500, 1000));
                        }
                    });
                } else {
                    logError("Bank Deposit Interface is not open.");
                }
            }
        } catch (Exception e) {
            logError("Exception in handleDepositBox method: " + e.getMessage());
            e.printStackTrace();
            net.botwithus.api.game.hud.inventories.DepositBox.depositAllExcept(EXCLUDED_ITEMS_REGEX);
        }
    }
}
