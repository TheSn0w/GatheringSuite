package net.Snow.Runecrafting;

import net.botwithus.rs3.game.Client;
import net.botwithus.rs3.game.Coordinate;
import net.botwithus.rs3.game.hud.interfaces.Component;
import net.botwithus.rs3.game.hud.interfaces.Interfaces;
import net.botwithus.rs3.game.login.LoginManager;
import net.botwithus.rs3.game.queries.builders.characters.PlayerQuery;
import net.botwithus.rs3.game.queries.builders.components.ComponentQuery;
import net.botwithus.rs3.game.queries.results.EntityResultSet;
import net.botwithus.rs3.game.scene.entities.characters.player.Player;
import net.botwithus.rs3.script.Execution;

import java.time.Duration;
import java.time.Instant;
import java.util.Random;

import static net.Snow.ImGui.CustomLogger.*;
import static net.Snow.Runecrafting.Variables.worldHop;
import static net.Snow.Snow.component;

public class WorldHop {

    private static Instant lastWorldHopTime = Instant.MIN;  // Initialize to signify no hops have occurred
    private static final Random random = new Random();
    private static boolean hasHoppedBefore = false;         // Flag to track if a hop has occurred
    private static boolean isWorldHopInProgress = false;    // Flag to track if a world hop is in progress

    // Minimum and maximum cooldown times in seconds (5 to 10 minutes)
    private static final long MIN_COOLDOWN = 150;
    private static final long MAX_COOLDOWN = 300;

    private static long nextCooldownDuration = getNextCooldownDuration();

    private static long getNextCooldownDuration() {
        return MIN_COOLDOWN + random.nextInt((int) (MAX_COOLDOWN - MIN_COOLDOWN + 1));
    }

    public static void worldHop() {
        if (worldHop && !isWorldHopInProgress) {  // Proceed only if world hop is enabled and not already in progress
            isWorldHopInProgress = true;  // Set flag to true when starting the world hop
            try {
                Player localPlayer = Client.getLocalPlayer();

                String localPlayerName = localPlayer.getName();
                Coordinate localPlayerLocation = localPlayer.getCoordinate();

                PlayerQuery query = PlayerQuery.newQuery();
                EntityResultSet<Player> players = query.results();

                // Freshly calculate if there are other players present
                boolean otherPlayersPresent = players.stream()
                        .filter(player -> !player.getName().equals(localPlayerName))
                        .filter(player -> {
                            Coordinate playerLocation = player.getCoordinate();
                            return playerLocation != null && localPlayerLocation.distanceTo(playerLocation) <= 18.0D;
                        })
                        .peek(player -> logOrange("Found player within distance: " + player.getName()))
                        .findAny()
                        .isPresent();

                // If other players are present, attempt to hop worlds
                if (otherPlayersPresent) {
                    if (canHopWorld()) {
                        int currentWorld = LoginManager.getWorld();
                        int randomMembersWorldIndex;
                        do {
                            randomMembersWorldIndex = random.nextInt(membersWorlds.length);
                        } while (membersWorlds[randomMembersWorldIndex] == currentWorld);

                        HopWorlds(membersWorlds[randomMembersWorldIndex]);
                        log("Hopped to world: " + membersWorlds[randomMembersWorldIndex]);
                        hasHoppedBefore = true;  // Mark that a hop has occurred
                        lastWorldHopTime = Instant.now();  // Update the last hop time
                        nextCooldownDuration = getNextCooldownDuration(); // Set the next cooldown duration
                    } else {
                        logGrey("Skipping world hop due to cooldown.");
                    }
                } else {
                    logGrey("No other players detected; skipping world hop.");
                }
            } finally {
                isWorldHopInProgress = false;  // Reset the flag once the hop process is done
            }
        }
    }

    public static void worldHopNoTimer() {
        if (worldHop && !isWorldHopInProgress) {  // Proceed only if world hop is enabled and not already in progress
            try {
                Player localPlayer = Client.getLocalPlayer();
                String localPlayerName = localPlayer.getName();
                Coordinate localPlayerLocation = localPlayer.getCoordinate();

                PlayerQuery query = PlayerQuery.newQuery();
                EntityResultSet<Player> players = query.results();

                // Freshly calculate if there are other players present
                boolean otherPlayersPresent = players.stream()
                        .filter(player -> !player.getName().equals(localPlayerName))
                        .filter(player -> {
                            Coordinate playerLocation = player.getCoordinate();
                            return playerLocation != null && localPlayerLocation.distanceTo(playerLocation) <= 18.0D;
                        })
                        .peek(player -> logOrange("Found player within distance: " + player.getName()))
                        .findAny()
                        .isPresent();

                // If other players are present, attempt to hop worlds
                if (otherPlayersPresent) {
                    int currentWorld = LoginManager.getWorld();
                    int randomMembersWorldIndex;
                    do {
                        randomMembersWorldIndex = random.nextInt(membersWorlds.length);
                    } while (membersWorlds[randomMembersWorldIndex] == currentWorld);

                    HopWorlds(membersWorlds[randomMembersWorldIndex]);
                    log("Hopped to world: " + membersWorlds[randomMembersWorldIndex]);
                } else {
                    logGrey("No other players detected; skipping world hop.");
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }




    private static boolean canHopWorld() {
        if (!hasHoppedBefore) {
            // Allow immediate world hop if it's the first time
            return true;
        }
        Duration timeSinceLastHop = Duration.between(lastWorldHopTime, Instant.now());
        return timeSinceLastHop.getSeconds() >= nextCooldownDuration;
    }

    public static void HopWorlds(int world) {
        if (Interfaces.isOpen(1431)) {
            log("Interacting with Settings Icon.");
            component(1, 7, 93782016);
            boolean hopperOpen = Execution.delayUntil(random.nextLong(5014, 9758), () -> Interfaces.isOpen(1433));
            log("Settings Menu Open: " + hopperOpen);
            Execution.delay(random.nextLong(100, 200));

            if (hopperOpen) {
                Component hopWorldsMenu = ComponentQuery.newQuery(1433).componentIndex(65).results().first();
                if (hopWorldsMenu != null) {
                    Execution.delay(random.nextLong(100, 200));
                    component(1, -1, 93913153);
                    log("Hop Worlds Button Clicked.");
                    boolean worldSelectOpen = Execution.delayUntil(random.nextLong(5014, 9758), () -> Interfaces.isOpen(1587));

                    if (worldSelectOpen) {
                        log("World Select Interface Open.");
                        Execution.delay(random.nextLong(100, 200));
                        component(2, world, 104005640);
                        log("Selected World: " + world);

                        if (Client.getGameState() == Client.GameState.LOGGED_IN) {
                            Execution.delay(random.nextLong(7548, 9879));
                            log("Resuming script.");
                        } else {
                            log("Failed to resume script. GameState is not LOGGED_IN or player is null.");
                        }
                    } else {
                        log("Failed to open World Select Interface.");
                    }
                } else {
                    log("Failed to find Hop Worlds Menu.");
                }
            } else {
                log("Failed to open hopper. Retrying...");
                HopWorlds(world);
            }
        } else {
            log("Interface 1431 is not open.");
        }
    }

    public static Duration getTimeUntilNextHop() {
        if (!hasHoppedBefore) {
            // If no hop has occurred yet, the cooldown is effectively zero
            return Duration.ZERO;
        }
        Instant now = Instant.now();
        Duration timeSinceLastHop = Duration.between(lastWorldHopTime, now);
        long remainingSeconds = nextCooldownDuration - timeSinceLastHop.getSeconds();
        return Duration.ofSeconds(Math.max(remainingSeconds, 0));
    }

    public static boolean isWorldHopInProgress() {
        return isWorldHopInProgress;
    }

    public static int[] membersWorlds = new int[]{
            1, 2, 4, 5, 6, 9, 10, 12, 14, 15,
            16, 21, 22, 23, 24, 25, 26, 27, 28, 31,
            32, 35, 36, 37, 39, 40, 42, 44, 45, 46,
            49, 50, 51, 53, 54, 56, 58, 59, 60,
            62, 63, 64, 65, 66, 67, 68, 69, 70, 71,
            72, 73, 74, 76, 77, 78, 79, 82, 83,
            85, 87, 88, 89, 91, 92, 97, 98, 99, 100, 103, 104, 105, 106, 116, 117, 119,
            123, 124, 134, 138, 139, 140, 252};


    public static void worldHopforArch() {
        if (worldHop && !isWorldHopInProgress) {  // Proceed only if world hop is enabled and not already in progress
            isWorldHopInProgress = true;  // Set flag to true when starting the world hop
            try {
                int currentWorld = LoginManager.getWorld();
                int randomMembersWorldIndex;
                do {
                    randomMembersWorldIndex = random.nextInt(membersWorlds.length);
                } while (membersWorlds[randomMembersWorldIndex] == currentWorld);

                HopWorlds(membersWorlds[randomMembersWorldIndex]);
                log("Hopped to world: " + membersWorlds[randomMembersWorldIndex]);
            } finally {
                isWorldHopInProgress = false;  // Reset the flag once the hop process is done
            }
        }
    }
}
