package net.Snow.Spells;

import net.botwithus.rs3.game.actionbar.ActionBar;
import net.botwithus.rs3.game.queries.builders.components.ComponentQuery;
import net.botwithus.rs3.game.vars.VarManager;
import net.botwithus.rs3.script.Execution;

import static net.Snow.ImGui.CustomLogger.logSuccess;
import static net.Snow.Snow.random;

public class CrystalMask {

    public static boolean useCrystalMask = false;

    public static void enableCrystalMask() {
        ComponentQuery query = ComponentQuery.newQuery(284).spriteId(25938);
        if (query.results().isEmpty()) {

            int earthRuneQuantity = Rune.EARTH.getQuantity();
            int fireRuneQuantity = Rune.FIRE.getQuantity();
            int bodyRuneQuantity = Rune.BODY.getQuantity();
            int soulRuneQuantity = Rune.SOUL.getQuantity();

            boolean sufficientEarthRunes = earthRuneQuantity > 7;
            boolean sufficientFireRunes = fireRuneQuantity > 6;
            boolean sufficientBodyRunes = bodyRuneQuantity > 5;
            boolean sufficientSoulRunes = soulRuneQuantity > 4;

            if (sufficientSoulRunes && sufficientBodyRunes && sufficientFireRunes && sufficientEarthRunes) {
                ActionBar.useAbility("Crystal Mask");
                logSuccess("Activated Crystal Mask.");
                Execution.delay(random.nextLong(1900, 2100));
            }
        }
    }
    public enum Rune {
        AIR(5886),
        WATER(5887),
        EARTH(5889),
        FIRE(5888),
        MIND(5902),
        BODY(5896),
        COSMIC(5897),
        CHAOS(5898),
        NATURE(5899),
        LAW(5900),
        ASTRAL(5903),
        DEATH(5901),
        BLOOD(5904),
        SOUL(5905);

        private final int index;

        Rune(int index) {
            this.index = index;
        }

        public int getQuantity() {
            return VarManager.getVarc(index);
        }
    }
}
