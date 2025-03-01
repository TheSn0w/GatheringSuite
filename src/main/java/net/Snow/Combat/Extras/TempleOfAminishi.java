package net.Snow.Combat.Extras;

import net.botwithus.rs3.game.Coordinate;

import static net.Snow.Combat.Combat.player;
import static net.Snow.ImGui.CustomLogger.logBlue;
import static net.Snow.ImGui.CustomLogger.logOrange;
import static net.Snow.Navigation.MoveTo;

public class TempleOfAminishi {

   private static Coordinate playerPosition;

    public static void recordPosition() {
        playerPosition = player.getServerCoordinate();
        logOrange("Player position recorded." + playerPosition);
    }

    public static void moveToEntrance() {
        if (playerPosition != null) {
            MoveTo(playerPosition);
        } else {
            logBlue("Player position has not been recorded yet.");
        }
    }
}
//changed