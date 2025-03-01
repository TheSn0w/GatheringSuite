package net.Snow.Woodcutting;

import net.botwithus.rs3.game.skills.Skills;

public class Woodbox {

    public static int getAdditionalWoodboxCapacity() {
        int level = Skills.WOODCUTTING.getActualLevel();
        for (int threshold = 95; threshold > 0; threshold -= 10) {
            if (level >= threshold)
                return threshold + 5;
        }
        return 0;
    }

    public static int getBaseWoodboxCapacity(String woodboxName) {
        switch (woodboxName) {
            case "Wood box":
                return 70;
            case "Oak wood box":
                return 80;
            case "Willow wood box":
                return 90;
            case "Teak wood box":
                return 100;
            case "Maple wood box":
                return 110;
            case "Acadia wood box":
                return 120;
            case "Mahogany wood box":
                return 130;
            case "Yew wood box":
                return 140;
            case "Magic wood box":
                return 150;
            case "Elder wood box":
                return 160;
        }
        return 0;
    }
}
