package net.Snow.Archaeology.Mysteries;

import net.Snow.Archaeology.Archaeology;
import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.rs3.game.Coordinate;
import net.botwithus.rs3.game.js5.types.vars.VarDomainType;
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery;
import net.botwithus.rs3.game.queries.results.EntityResultSet;
import net.botwithus.rs3.game.scene.entities.object.SceneObject;
import net.botwithus.rs3.game.vars.VarManager;

import static net.Snow.Archaeology.Archaeology.setBotState;
import static net.Snow.Archaeology.MaterialTypes.setSelectedExcavationIndexByName;
import static net.Snow.Banking.BankLocations.setBank;
import static net.Snow.Combat.Combat.player;
import static net.Snow.ImGui.CustomLogger.logOrange;
import static net.Snow.ImGui.CustomLogger.logWarning;
import static net.Snow.Navigation.MoveTo;

public class BreakingTheSeal {


    // In BreakingTheSeal.java
    public static void breakTheSeal() {
        if (VarManager.getVarValue(VarDomainType.PLAYER, 45393) == 0) {
            logWarning("You need to complete the Breaking the Seal mystery first.");

            if (player.getAnimationId() != -1) {
                return;
            }

            setSelectedExcavationIndexByName("Fort debris");
            setBank("Karid Et Bank Chest");

            EntityResultSet<SceneObject> fortDebris = SceneObjectQuery.newQuery().name("Fort debris").option("Clear").results();

            if (fortDebris.isEmpty()) {
                logWarning("Fort debris not found.");
                MoveTo(new Coordinate(3374, 3181, 0));
            } else {
                logOrange("Interacting with fort: " + fortDebris.nearest().interact("Clear"));
                if (Backpack.isFull()) {
                    setBotState(Archaeology.ArchaeologyState.BANKING);
                }
            }
        }
    }
}