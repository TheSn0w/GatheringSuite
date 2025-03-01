package net.Snow.Archaeology;

import net.botwithus.rs3.game.Coordinate;
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.game.scene.entities.object.SceneObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Scanner {
    private static final List<List<String>> sceneObjectTableData = new ArrayList<>();
    private static final long SCAN_DELAY_MS = 5000; // Delay between scans in milliseconds
    private static long lastScanTime = 0; // Time of the last scan

    public static void updateSceneObjectTableData(LocalPlayer player) {
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastScanTime >= SCAN_DELAY_MS) {
            List<SceneObject> sceneObjects = SceneObjectQuery.newQuery()
                    .option("Excavate")
                    .results()
                    .stream().toList();

            sceneObjectTableData.clear();

            Set<String> uniqueSceneObjectNames = new HashSet<>();
            Coordinate playerCoordinate = player.getCoordinate();

            for (SceneObject sceneObject : sceneObjects) {
                if (sceneObject.getCoordinate().distanceTo(playerCoordinate) <= 25) {
                    uniqueSceneObjectNames.add(sceneObject.getName());
                }
            }

            for (SceneObject sceneObject : sceneObjects) {
                if (uniqueSceneObjectNames.contains(sceneObject.getName())) {
                    List<String> row = new ArrayList<>();
                    row.add(sceneObject.getName());
                    Coordinate coordinate = sceneObject.getCoordinate();
                    row.add(coordinate.getX() + "," + coordinate.getY() + "," + coordinate.getZ());
                    sceneObjectTableData.add(row);
                    uniqueSceneObjectNames.remove(sceneObject.getName());
                }
            }

            lastScanTime = currentTime;
        }
    }

    public static List<List<String>> getSceneObjectTableData() {
        return sceneObjectTableData;
    }
}
