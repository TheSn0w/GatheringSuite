package net.Snow.Archaeology;

import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.rs3.game.js5.types.vars.VarDomainType;
import net.botwithus.rs3.game.vars.VarManager;
import net.botwithus.rs3.script.Execution;

import static net.Snow.ImGui.CustomLogger.*;
import static net.Snow.Snow.random;

public class SoilBox {

    public enum SoilType {
        ANCIENT_GRAVEL(9370),
        SALTWATER_MUD(9371),
        FIERY_BRIMSTONE(9372),
        AERATED_SEDIMENT(9373),
        EARTHEN_CLAY(9374),
        VOLCANIC_ASH(9578),
        UNKNOWN(9370);

        private final int soilBoxId;

        SoilType(int soilBoxId) {
            this.soilBoxId = soilBoxId;
        }

        public int getSoilBoxId() {
            return soilBoxId;
        }
    }


    public static boolean handleSoilBox() {
        int soilBoxUpgradeLevel = VarManager.getVarbitValue(47021);
        int soilBoxCapacity = switch (soilBoxUpgradeLevel) {
            case 0 -> 50;
            case 1 -> 100;
            case 2 -> 250;
            case 3 -> 500;
            default -> 0;
        };

        logBlue("Soil box capacity: " + soilBoxCapacity);

        // Log and check if any soil type is at the soil box capacity
        for (SoilType soilType : SoilType.values()) {
            int soilAmount = VarManager.getVarValue(VarDomainType.PLAYER, soilType.getSoilBoxId());
            logBlue("Soil Type: " + soilType.name() + ", Current Amount: " + soilAmount);
            if (soilAmount >= soilBoxCapacity) {
                logWarning("Soil box is full for soil type: " + soilType.name());
                return false;
            }
        }

        for (SoilType soilType : SoilType.values()) {
            int soilAmount = VarManager.getVarValue(VarDomainType.PLAYER, soilType.getSoilBoxId());
            logBlue("Soil Type: " + soilType.name() + ", Current Amount: " + soilAmount);
            if (Backpack.containsItemByCategory(4603) && soilAmount < soilBoxCapacity) {
                boolean success = Backpack.interact("Archaeological soil box", "Fill");
                Execution.delayUntil(random.nextLong(4000, 5000), () -> !Backpack.containsItemByCategory(4603));
                if (success && !Backpack.isFull()) {
                    logSuccess("Soil box filled.");
                    return true;
                }
            }
        }
        return false;
    }
}
