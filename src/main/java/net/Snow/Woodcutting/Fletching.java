package net.Snow.Woodcutting;

import net.botwithus.rs3.game.Item;
import net.botwithus.rs3.game.hud.interfaces.Interfaces;
import net.botwithus.rs3.game.inventories.Backpack;
import net.botwithus.rs3.game.js5.types.vars.VarDomainType;
import net.botwithus.rs3.game.queries.builders.items.InventoryItemQuery;
import net.botwithus.rs3.game.queries.results.ResultSet;
import net.botwithus.rs3.game.vars.VarManager;
import net.botwithus.rs3.script.Execution;

import static net.Snow.ImGui.CustomLogger.logBlue;
import static net.Snow.ImGui.CustomLogger.logError;
import static net.Snow.Snow.*;

public class Fletching {

    public static boolean doFletching = false;

    public static void fletch() {
        logBlue("Starting Fletching process.");

        ResultSet<Item> logs = InventoryItemQuery.newQuery(93)
                .option("Craft")
                .results();

        Item woodLogs = logs.first();
        if (woodLogs == null) {
            logError("No logs found in inventory.");
            return;
        } else {
            logBlue("Logs found in inventory: " + woodLogs.getName());
        }

        Backpack.interact(woodLogs.getName(), "Craft");
        Execution.delayUntil(random.nextLong(10000), () -> Interfaces.isOpen(1370) || Interfaces.isOpen(1179));

        Execution.delay(random.nextLong(500, 800));

        if (Interfaces.isOpen(1179)) {
            logBlue("Selecting Knife.");
            //[Original]: DoAction(DIALOGUE, 0, -1, 77266961)
            dialog(0, -1, 77266961);
            Execution.delay(random.nextLong(900, 1250));
        }

        Execution.delay(random.nextLong(500, 800));

        if (Interfaces.isOpen(1370)) {
            fletchingSelectables();
            selectInterface1370();
        }
    }

    public static boolean arrowShafts = false;
    public static boolean shortBow = false;
    public static boolean woodenStock = false;
    public static boolean shieldBow = false;

    public static void fletchingSelectables() {
        int scroll = VarManager.getVarValue(VarDomainType.PLAYER, 1169); // == the varbit of the Dropdown
        int selectable = VarManager.getVarValue(VarDomainType.PLAYER, 1170); // == the varbit of the First selectable

        if (Backpack.contains("Logs")) {
            logBlue("Logs found in backpack.");
            if (scroll != 6947) {
                /*logInfo("Setting scroll to 6947.");
                component(1, -1, 89849884);
                Execution.delay(random.nextLong(300, 500));*/
                component(1, 1, 96797565);
                Execution.delay(random.nextLong(300, 500));
            }
            if (arrowShafts) {
                if (selectable != 52) {
                    logBlue("Selecting Arrow Shafts.");
                    component(1, 1, 89849878);
                    Execution.delay(random.nextLong(300, 500));
                }
            }
            if (shortBow) {
                if (selectable != 50) {
                    logBlue("Selecting Shortbow.");
                    component(1, 5, 89849878);
                    Execution.delay(random.nextLong(300, 500));
                }
            }
            if (woodenStock) {
                if (selectable != 9440) {
                    logBlue("Selecting Wooden Stock.");
                    component(1, 9, 89849878);
                    Execution.delay(random.nextLong(300, 500));
                }
            }
            if (shieldBow) {
                if (selectable != 48) {
                    logBlue("Selecting Shieldbow.");
                    component(1, 13, 89849878);
                    Execution.delay(random.nextLong(300, 500));
                }
            }
        } else if (Backpack.contains("Oak logs")) {
            logBlue("Oak logs found in backpack.");
            if (scroll != 6949) {
                /*logInfo("Setting scroll to 13276.");
                component(1, -1, 89849884);
                Execution.delay(random.nextLong(300, 500));*/
                //[Original]: DoAction(COMPONENT, 1, 5, 96797565)
                component(1, 5, 96797565);
                Execution.delay(random.nextLong(300, 500));
            }
            if (arrowShafts) {
                if (selectable != 34672) {
                    logBlue("Selecting Arrow Shafts for Oak logs.");
                    component(1, 1, 89849878);
                    Execution.delay(random.nextLong(300, 500));
                } else {
                    logBlue("Arrow Shafts already selected.");
                }
            }
            if (shortBow) {
                if (selectable != 54) {
                    logBlue("Selecting Shortbow for Oak logs.");
                    component(1, 5, 89849878);
                    Execution.delay(random.nextLong(300, 500));
                } else {
                    logBlue("Shortbow already selected.");
                }
            }
            if (woodenStock) {
                if (selectable != 9442) {
                    logBlue("Selecting Wooden Stock for Oak logs.");
                    component(1, 9, 89849878);
                    Execution.delay(random.nextLong(300, 500));
                } else {
                    logBlue("Wooden Stock already selected.");
                }
            }
            if (shieldBow) {
                if (selectable != 56) {
                    logBlue("Selecting Shieldbow for Oak logs.");
                    component(1, 13, 89849878);
                    Execution.delay(random.nextLong(300, 500));
                } else {
                    logBlue("Shieldbow already selected.");
                }
            }
        } else if (Backpack.contains("Willow logs")) {
            logBlue("Willow logs found in backpack.");
            if (scroll != 6950) {
                /*logInfo("Setting scroll to 13276.");
                component(1, -1, 89849884);
                Execution.delay(random.nextLong(300, 500));*/
                component(1, 7, 96797565);
                Execution.delay(random.nextLong(300, 500));
            }
            if (arrowShafts) {
                if (selectable != 34673) {
                    logBlue("Selecting Arrow Shafts for Willow logs.");
                    component(1, 1, 89849878);
                    Execution.delay(random.nextLong(300, 500));
                } else {
                    logBlue("Arrow Shafts already selected.");
                }
            }
            if (shortBow) {
                if (selectable != 60) {
                    logBlue("Selecting Shortbow for Willow logs.");
                    component(1, 5, 89849878);
                    Execution.delay(random.nextLong(300, 500));
                } else {
                    logBlue("Shortbow already selected.");
                }
            }
            if (woodenStock) {
                if (selectable != 9444) {
                    logBlue("Selecting Wooden Stock for Willow logs.");
                    component(1, 9, 89849878);
                    Execution.delay(random.nextLong(300, 500));
                } else {
                    logBlue("Wooden Stock already selected.");
                }
            }
            if (shieldBow) {
                if (selectable != 58) {
                    logBlue("Selecting Shieldbow for Willow logs.");
                    component(1, 13, 89849878);
                    Execution.delay(random.nextLong(300, 500));
                } else {
                    logBlue("Shieldbow already selected.");
                }
            }
        } else if (Backpack.contains("Maple logs")) {
            logBlue("Maple logs found in backpack.");
            if (scroll != 6952) {
                /*logInfo("Setting scroll to 13276.");
                component(1, -1, 89849884);
                Execution.delay(random.nextLong(300, 500));*/
                component(1, 11, 96797565);
                Execution.delay(random.nextLong(300, 500));
            }
            if (arrowShafts) {
                if (selectable != 34674) {
                    logBlue("Selecting Arrow Shafts for Maple logs.");
                    component(1, 1, 89849878);
                    Execution.delay(random.nextLong(300, 500));
                } else {
                    logBlue("Arrow Shafts already selected.");
                }
            }
            if (shortBow) {
                if (selectable != 64) {
                    logBlue("Selecting Shortbow for Maple logs.");
                    component(1, 5, 89849878);
                    Execution.delay(random.nextLong(300, 500));
                } else {
                    logBlue("Shortbow already selected.");
                }
            }
            if (woodenStock) {
                if (selectable != 9448) {
                    logBlue("Selecting Wooden Stock for Maple logs.");
                    component(1, 9, 89849878);
                    Execution.delay(random.nextLong(300, 500));
                } else {
                    logBlue("Wooden Stock already selected.");
                }
            }
            if (shieldBow) {
                if (selectable != 62) {
                    logBlue("Selecting Shieldbow for Maple logs.");
                    component(1, 13, 89849878);
                    Execution.delay(random.nextLong(300, 500));
                } else {
                    logBlue("Shieldbow already selected.");
                }
            }
        } else if (Backpack.contains("Acadia logs")) {
            logBlue("Acadia logs found in backpack.");
            if (scroll != 13276) {
                /*logInfo("Setting scroll to 13276.");
                component(1, -1, 89849884);
                Execution.delay(random.nextLong(300, 500));*/
                component(1, 1, 96797565);
                Execution.delay(random.nextLong(300, 500));
            }
            if (arrowShafts) {
                if (selectable != 41378) {
                    logBlue("Selecting Arrow Shafts for Acadia logs.");
                    component(1, 1, 89849878);
                    Execution.delay(random.nextLong(300, 500));
                } else {
                    logBlue("Arrow Shafts already selected.");
                }
            }
        }
    }
}
