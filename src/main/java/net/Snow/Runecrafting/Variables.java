package net.Snow.Runecrafting;

import net.botwithus.rs3.game.Area;
import net.botwithus.rs3.game.Coordinate;
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery;

import java.time.Duration;
import java.time.Instant;

public class Variables {

    public static Runestate botstate = Runestate.TELEPORTING;
    public static Instant lastStateChangeTime = Instant.now();
    public static int loopCounter = 0; // Loop counter for tracking number of runs
    public static Instant startTime = Instant.now();
    public static boolean worldHop = false;

    public static final Area HAUNTED_HILL_AREA = new Area.Rectangular(
            new Coordinate(1160, 1820, 1), // Bottom-left coordinate of the area
            new Coordinate(1166, 1839, 1)  // Top-right coordinate of the area
    );
    public static final Area BANK_AREA = new Area.Rectangular(
            new Coordinate(3859, 6825, 0), // Bottom-left coordinate of the area
            new Coordinate(3862, 6829, 0)  // Top-right coordinate of the area
    );

    public static final Area ALTAR_AREA = new Area.Rectangular(
            new Coordinate(1290, 1930, 1), // Bottom-left coordinate of the area
            new Coordinate(1330, 1975, 1)  // Top-right coordinate of the area
    );

    public static final Area SURGE = new Area.Rectangular(
            new Coordinate(1301, 1953, 1), // Bottom-left coordinate of the area
            new Coordinate(1306, 1955, 1)  // Top-right coordinate of the area
    );

    public static final SceneObjectQuery DEAD_MANS_CHEST = SceneObjectQuery.newQuery()
            .id(90950)
            .option("Load Last Preset from");
    public static final SceneObjectQuery DARK_PORTALS = SceneObjectQuery.newQuery()
            .id(127376)
            .option("Enter");
    public static final SceneObjectQuery BONE_ALTARS = SceneObjectQuery.newQuery()
            .id(127381)
            .option("Craft runes");
    public static final SceneObjectQuery SPIRIT_ALTARS = SceneObjectQuery.newQuery()
            .id(127380)
            .option("Craft runes");
    public static final SceneObjectQuery MIASMA_ALTARS = SceneObjectQuery.newQuery()
            .id(127383)
            .option("Craft runes");
    public static final SceneObjectQuery FLESH_ALTARS = SceneObjectQuery.newQuery()
            .id(127382)
            .option("Craft runes");

    public static int getRunsPerHour() {
        Duration elapsed = Duration.between(startTime, Instant.now());
        double hours = elapsed.toMillis() / (1000.0 * 60 * 60);
        return (int) (loopCounter / hours);
    }
}
