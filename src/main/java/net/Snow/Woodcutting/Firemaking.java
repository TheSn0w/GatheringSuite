package net.Snow.Woodcutting;

import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.rs3.game.Client;
import net.botwithus.rs3.game.Coordinate;
import net.botwithus.rs3.game.Item;
import net.botwithus.rs3.game.hud.interfaces.Interfaces;
import net.botwithus.rs3.game.minimenu.MiniMenu;
import net.botwithus.rs3.game.minimenu.actions.ComponentAction;
import net.botwithus.rs3.game.queries.builders.items.InventoryItemQuery;
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery;
import net.botwithus.rs3.game.queries.results.ResultSet;
import net.botwithus.rs3.game.scene.entities.object.SceneObject;
import net.botwithus.rs3.script.Execution;

import static net.Snow.ImGui.CustomLogger.*;
import static net.Snow.Snow.capturestuff;
import static net.Snow.Snow.random;

public class Firemaking {

    public static boolean doFiremaking = false;

    public static void firemake() {
        if (!doFiremaking) {
            return;
        }

        logBlue("Starting firemaking process.");

        ResultSet<Item> logs = InventoryItemQuery.newQuery(93)
                .option("Light")
                .results();

        logGrey("Logs in inventory: " + logs.size());

        Item woodLogs = logs.first();
        if (woodLogs == null) {
            logError("No logs found in inventory.");
            return;
        }

        logGrey("Found logs: " + woodLogs.getName());
        int attempts = 0;
        boolean restartFiremaking = false;

        while (Backpack.contains(woodLogs.getName()) && attempts < 3) {
            if (restartFiremaking) {
                logBlue("Restarting firemaking process.");
                firemake();
                return;
            }

            attempts++;
            logBlue("Attempt " + attempts + " to light the fire.");

            // Check if a fire is already nearby
            SceneObject fire = SceneObjectQuery.newQuery().name("Fire").results().nearest();
            boolean isFireNearby = fire != null && calculateDistance(fire.getCoordinate(), Client.getLocalPlayer().getCoordinate()) <= 3.0;

            if (!isFireNearby) {
                boolean success = Backpack.interact(woodLogs.getSlot(), "Light");
                if (!success) {
                    logError("Failed to light: " + woodLogs.getName());
                    continue;
                }

                logBlue("Lighting: " + woodLogs.getName());
                boolean fireLit = Execution.delayUntil(random.nextLong(10000, 15000), () -> {
                    SceneObject newFire = SceneObjectQuery.newQuery().name("Fire").results().nearest();
                    if (newFire != null) {
                        boolean newIsFireNearby = calculateDistance(newFire.getCoordinate(), Client.getLocalPlayer().getCoordinate()) <= 3.0;
                        if (newIsFireNearby) {
                            logGrey("Found fire at position: " + newFire.getCoordinate() + ", within 3 tiles: " + newIsFireNearby);
                        }
                        return newIsFireNearby;
                    } else {
                        logGrey("No fire found.");
                        return false;
                    }
                });

                if (!fireLit) {
                    logError("Failed to light fire within 3 tiles.");
                    continue;
                }

                Execution.delay(random.nextLong(800, 1000)); // Delay after lighting fire
            } else {
                logBlue("Fire is already nearby, skipping lighting.");
            }

            logBlue("Fire is lit and within 3 tiles.");
            boolean craftSuccess = Backpack.interact(woodLogs.getName(), "Craft");
            Execution.delayUntil(10000, () -> Interfaces.isOpen(1179));
            if (!craftSuccess) {
                logError("Failed to craft: " + woodLogs.getName());
                continue;
            }

            logBlue("Crafting: " + woodLogs.getName());
            boolean interfaceOpened = Execution.delayUntil(random.nextLong(10000), () -> Interfaces.isOpen(1179));
            if (!interfaceOpened) {
                logError("Failed to open interface 1179.");
                return;
            }

            Execution.delay(random.nextLong(800, 1000)); // Delay after lighting fire

            logBlue("Interface is open, Adding to fire");
            MiniMenu.interact(ComponentAction.DIALOGUE.getType(), 0, -1, 77266971);
            logBlue("Waiting to finish");

            long startTime = System.currentTimeMillis();
            long timeout = 180000; // 3 minutes
            boolean finished = false;

            while (System.currentTimeMillis() - startTime < timeout) {
                capturestuff();

                SceneObject currentFire = SceneObjectQuery.newQuery().name("Fire").results().first();
                boolean currentIsFireNearby = currentFire != null && calculateDistance(currentFire.getCoordinate(), Client.getLocalPlayer().getCoordinate()) <= 3.0;
                boolean hasLogs = Backpack.contains(woodLogs.getName());

                logGrey("Checking conditions: Fire nearby: " + currentIsFireNearby + ", Has logs: " + hasLogs + ", Finished: " + (currentIsFireNearby && !hasLogs));

                if (currentIsFireNearby && !hasLogs) {
                    finished = true;
                    break;
                }

                if (!currentIsFireNearby && hasLogs) {
                    logBlue("Fire is no longer nearby but logs are still present. Restarting firemaking process.");
                    restartFiremaking = true;
                    break;
                }

                try {
                    Thread.sleep(random.nextLong(2000, 4000));
                } catch (InterruptedException e) {
                    logError("Interrupted while waiting: " + e.getMessage());
                    break;
                }
            }

            if (finished) {
                logBlue("Finished firemaking, going back to Woodcutting");
            } else {
                logError("Failed to finish firemaking within the expected time.");
            }
        }
    }

    private static double calculateDistance(Coordinate pos1, Coordinate pos2) {
        double dx = pos1.getX() - pos2.getX();
        double dy = pos1.getY() - pos2.getY();
        double dz = pos1.getZ() - pos2.getZ();
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }
}