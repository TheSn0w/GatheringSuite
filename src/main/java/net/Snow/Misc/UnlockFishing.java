package net.Snow.Misc;

import net.Snow.AbstractScript;
import net.Snow.Fishing.FishingGraphics;
import net.Snow.ImGui.SnowsTemplateGraphics;
import net.Snow.Snow;
import net.botwithus.rs3.game.Client;
import net.botwithus.rs3.game.Coordinate;
import net.botwithus.rs3.game.Distance;
import net.botwithus.rs3.game.movement.Movement;
import net.botwithus.rs3.game.queries.builders.characters.NpcQuery;
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery;
import net.botwithus.rs3.game.queries.results.EntityResultSet;
import net.botwithus.rs3.game.scene.entities.characters.npc.Npc;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.game.scene.entities.object.SceneObject;
import net.botwithus.rs3.game.skills.Skills;
import net.botwithus.rs3.script.Execution;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static net.Snow.ImGui.CustomLogger.*;
import static net.Snow.ImGui.Experience.startTracking;

public class UnlockFishing implements AbstractScript {

    Snow script;

    LocalPlayer player = Client.getLocalPlayer();


    @Override
    public void initialize() {
        startingFishingXP.set(Skills.FISHING.getSkill().getExperience());
        startingFishingLevel.set(Skills.FISHING.getSkill().getLevel());
        startTracking();

    }

    public AtomicInteger startingFishingXP = new AtomicInteger(0);
    public AtomicInteger startingFishingLevel = new AtomicInteger(0);


    @Override
    public void draw(SnowsTemplateGraphics sgc) throws IOException {
        FishingGraphics.draw(sgc, script, startingFishingLevel, startingFishingXP);
    }

//    @Override
//    public int getLoopDelay() {
//        return 100;
//    }

    private boolean hasRun = false;


    public void run(Snow script) {
        // Find all NPCs named "Salty crablet" with the "Mine" option
        EntityResultSet<Npc> crablets = NpcQuery.newQuery().name("Salty crablet").option("Mine").results();

        crablets.nearest().interact("Mine");
        Execution.delayUntil(1250000, () -> player.getAnimationId() == -1);
    }






    private void walkToBoat() {
        // Retrieve the nearest SceneObject with the specified ID and option
        logOrange("Querying for SceneObject with ID 104020 and option 'Use'");
        EntityResultSet<SceneObject> results = SceneObjectQuery.newQuery().id(104020).option("Use").results();
        SceneObject nearestObject = results.nearest();

        // Check if a nearest object was found
        if (nearestObject != null) {
            logOrange("Nearest object found at coordinates: (" + nearestObject.getCoordinate().getX() + ", " + nearestObject.getCoordinate().getY() + ")");

            // Attempt to move to the nearest walkable tile around the object
            moveToNearestWalkableTile(nearestObject);
        } else {
            logOrange("No nearest object found with ID 104020 and option 'Use'");
        }
    }

    public void moveToNearestWalkableTile(SceneObject object) {
        LocalPlayer player = Client.getLocalPlayer();
        int maxRange = 12; // Define the range for checking walkable tiles

        // Get surrounding tiles within the +/- 9 range and convert to a List
        List<Coordinate> surroundingTiles = new ArrayList<>(getSurroundingTiles(object, maxRange));

        // Sort the tiles by distance from the player's current position in ascending order
        surroundingTiles.sort(Comparator.comparingDouble(tile -> Distance.between(player.getCoordinate(), tile)));

        // Iterate through the sorted list of tiles in sequential order (from closest to furthest)
        for (Coordinate tile : surroundingTiles) {
            logGrey("Attempting to move to tile: " + tile);

            // Use the Movement class to move to the tile
            Movement.walkTo(tile.getX(), tile.getY(), true);

            // Check if the player starts moving within 1 second
            if (Execution.delayUntil(1000, player::isMoving)) {
                logBlue("Player started moving to tile: " + tile);

                // Wait until the player reaches the target coordinate
                boolean reachedTarget = Execution.delayUntil(360000, () -> player.getCoordinate().equals(tile));

                if (reachedTarget) {
                    logSuccess("Player successfully reached the target coordinate: " + tile);
                } else {
                    logError("Player did not reach the target coordinate within the expected time.");
                }

                return; // Exit the method once the player starts moving and reaches the target
            } else {
                logGrey("Tile at: " + tile + " is not walkable. Trying next tile.");
                Execution.delay(500); // Slight delay before attempting the next tile
            }
        }

        logGrey("No walkable tiles found within the range.");
    }

    public static Set<Coordinate> getSurroundingTiles(SceneObject object, int range) {
        logGrey("Calculating surrounding tiles for object: " + object.getName() + " within range: " + range);
        Coordinate objectCoordinate = object.getCoordinate(); // Get the object's base coordinate

        if (objectCoordinate == null) {
            logGrey("Object's coordinate is null, returning empty set.");
            return Collections.emptySet();
        }

        Set<Coordinate> result = new HashSet<>();
        int baseX = objectCoordinate.getX();
        int baseY = objectCoordinate.getY();

        // Generate coordinates within the +/- range of the object's base coordinate
        for (int xOffset = -range; xOffset <= range; xOffset++) {
            for (int yOffset = -range; yOffset <= range; yOffset++) {
                Coordinate derivedTile = objectCoordinate.derive(xOffset, yOffset, 0);
                result.add(derivedTile);
                logGrey("Derived surrounding tile: " + derivedTile); // Log each derived coordinate
            }
        }

        logBlue("Surrounding tiles calculated: " + result.size() + " tiles.");
        return result;
    }

















//        EntityResultSet<SceneObject> stonecutter = SceneObjectQuery.newQuery().id(125053).option("Cut stone").results();
//        EntityResultSet<SceneObject> chest = SceneObjectQuery.newQuery().id(125115).option("Use").results();
//
////        ComponentQuery componentQuery = ComponentQuery.newQuery(1251).componentIndex(27);
////
////        if (!componentQuery.results().isEmpty()) {
////            // Get the first component from the result set
////            Component component = componentQuery.results().first();
////
////            // Retrieve the text of the component
////            String componentText = component.getText();
////            log("Component text: " + componentText);
////
////            // Check if the component text matches the desired format "number/number"
////            if (componentText.matches("\\d+/\\d+")) {
////                String[] parts = componentText.split("/");
////
////                // Parse the numbers before and after the "/"
////                int firstNumber = Integer.parseInt(parts[0]);
////                int secondNumber = Integer.parseInt(parts[1]);
////
////                // Check if the numbers are equal
////                if (firstNumber == secondNumber) {
////                    log("Numbers match: " + componentText);
////                    if (Backpack.interact("Arrow shaft", "Feather")) {
////                        Execution.delayUntil(5000, () -> Interfaces.isOpen(1370));
////                        return;
////                    }
////                } else {
////                    log("Numbers do not match.");
////                }
////            } else {
////                log("Component text does not match the required format.");
////            }
////        } else {
////            log("Component not found.");
////        }
//
//
////        if (VarManager.getVarValue(VarDomainType.PLAYER, 1176) == 2800) {
////            chest.nearest().interact("Load Last Preset from");
////            log("Loading preset");
////            Execution.delayUntil(5000, () -> Backpack.getCount("Limestone brick") >= 4);
////        }
//
//        if (Interfaces.isOpen(1251) || player.isMoving()) {
//            return;
//        }
//
//        if (Interfaces.isOpen(1370)) {
//            //[Original]: DoAction(DIALOGUE, 0, -1, 89784350)
//            dialog(0, -1, 89784350);
//            log("Clicking through dialogue");
//            Execution.delayUntil(5000, () -> Interfaces.isOpen(1251));
//            return;
//        }
//
//        if (Backpack.interact("Arrow shaft", "Feather")) {
//            Execution.delayUntil(5000, () -> Interfaces.isOpen(1370));
//            return;
//        }
//
//        if (Backpack.getCount("Limestone brick") >= 4) {
//            stonecutter.nearest().interact("Cut stone");
//            logBlue("Cutting stone");
//            Execution.delayUntil(5000, () -> Interfaces.isOpen(1370));
//        }

//        if (hasRun) {
//            return;
//        }
//        hasRun = true;
//
//
//        MoveTo(new Coordinate(2880, 3444, 0));
//        MiniMenu.interact(ObjectAction.OBJECT1.getType(), 69839, 2877, 3442);
//        Execution.delay(random.nextLong(7500, 10000));
//        MoveTo(new Coordinate(2966, 3406, 0));
//        //[Original]: DoAction(OBJECT1, 69835, 2966, 3403)
//        MiniMenu.interact(ObjectAction.OBJECT1.getType(), 69835, 2966, 3403);
//        Execution.delay(random.nextLong(7500, 10000));
//        MoveTo(new Coordinate(3104, 3300, 0));
//        //[Original]: DoAction(OBJECT1, 69833, 3104, 3298)
//        MiniMenu.interact(ObjectAction.OBJECT1.getType(), 69833, 3104, 3298);
//        Execution.delay(random.nextLong(7500, 10000));
//        MoveTo(new Coordinate(3086, 3230, 0));
    }