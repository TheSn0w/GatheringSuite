package net.Snow.Combat;

import net.botwithus.rs3.game.Coordinate;
import net.botwithus.rs3.game.queries.builders.characters.NpcQuery;
import net.botwithus.rs3.game.scene.entities.characters.npc.Npc;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NPCScanner {

    private static final List<List<String>> npcTableData = new ArrayList<>();
    private static final long SCAN_DELAY_MS = 5000; // Delay between scans in milliseconds
    private static long lastScanTime = 0; // Time of the last scan

    public static void updateNpcTableData(LocalPlayer player) {
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastScanTime >= SCAN_DELAY_MS) {
            List<Npc> npcs = NpcQuery.newQuery()
                    .option("Attack")
                    .results()
                    .stream().toList();

            npcTableData.clear();

            Set<String> uniqueNpcNames = new HashSet<>();
            Coordinate playerCoordinate = player.getCoordinate();

            for (Npc npc : npcs) {
                if (npc.getCoordinate().distanceTo(playerCoordinate) <= 25) {
                    uniqueNpcNames.add(npc.getName());
                }
            }

            for (Npc npc : npcs) {
                if (uniqueNpcNames.contains(npc.getName())) {
                    List<String> row = new ArrayList<>();
                    row.add(npc.getName());
                    Coordinate coordinate = npc.getCoordinate();
                    row.add(coordinate.getX() + "," + coordinate.getY() + "," + coordinate.getZ());
                    npcTableData.add(row);
                    uniqueNpcNames.remove(npc.getName());
                }
            }

            lastScanTime = currentTime;
        }
    }

    public static List<List<String>> getNpcTableData() {
        return npcTableData;
    }
}
