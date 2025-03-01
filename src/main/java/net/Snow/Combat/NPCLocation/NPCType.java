package net.Snow.Combat.NPCLocation;

import net.botwithus.rs3.game.Coordinate;

import java.util.ArrayList;
import java.util.List;

public class NPCType {

    public static int selectedNpcIndex = 0;
    public static List<CombatNavigation> NpcTypes = new ArrayList<>();


    static {
        NpcTypes.add(new CombatNavigation("Disabled", null, null)); // 0
        NpcTypes.add(new CombatNavigation("Chicken", new Coordinate(3230, 3298, 0), "Lumbridge")); // 0
        NpcTypes.add(new CombatNavigation("Cow", new Coordinate(3256, 3283, 0), "Lumbridge")); // 1
        NpcTypes.add(new CombatNavigation("Goblin", new Coordinate(3250, 3239, 0), "Lumbridge")); // 2
    }
}
