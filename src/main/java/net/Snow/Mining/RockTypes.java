package net.Snow.Mining;

import net.Snow.Navigation;
import net.botwithus.rs3.game.Coordinate;

import java.util.ArrayList;
import java.util.List;

public class RockTypes {

    public static int selectedRockIndex = 0;
    public static List<Navigation> rockTypes = new ArrayList<>();

    static {
        rockTypes.add(new Navigation("Disabled", null, null, 0)); // 0
        rockTypes.add(new Navigation("Uncommon gem rock", new Coordinate(3298, 3313, 0), "Al kharid", 20)); // 1
        rockTypes.add(new Navigation("Precious gem rock", new Coordinate(1188, 4509, 0), "Al Kharid Resource Dungeon", 25)); // 2
        rockTypes.add(new Navigation("Drakolith rock", new Coordinate(1174, 4517, 0), "Al Kharid Resource Dungeon", 60)); // 3
        rockTypes.add(new Navigation("Necrite rock", new Coordinate(1190, 4515, 0), "Al Kharid Resource Dungeon", 70)); // 4
        rockTypes.add(new Navigation("Light animica rock", new Coordinate(5339, 2253, 0), "Anachronia", 90)); // 5
        rockTypes.add(new Navigation("Common gem rock", new Coordinate(2269, 4495, 0), "Burthorpe", 1)); // 6
        rockTypes.add(new Navigation("Clay rock", new Coordinate(2259, 4503, 0), "Burthorpe", 1)); // 7
        rockTypes.add(new Navigation("Silver rock", new Coordinate(2260, 4514, 0), "Burthorpe", 20)); // 8
        rockTypes.add(new Navigation("Tin rock", new Coordinate(2289, 4513, 0), "Burthorpe", 1)); // 9
        rockTypes.add(new Navigation("Copper rock", new Coordinate(2285, 4515, 0), "Burthorpe", 1)); // 10
        rockTypes.add(new Navigation("Iron rock", new Coordinate(2281, 4500, 0), "Burthorpe", 10)); // 11
        rockTypes.add(new Navigation("Kratonium rock", new Coordinate(3441, 3642, 0), "Daemonheim", 100)); // 12
        rockTypes.add(new Navigation("Fractite rock", new Coordinate(3398, 3752, 0), "Daemonheim", 100)); // 13
        rockTypes.add(new Navigation("Zephyrium rock", new Coordinate(3393, 3715, 0), "Daemonheim", 100)); // 14
        rockTypes.add(new Navigation("Argonite rock", new Coordinate(3395, 3667, 0), "Daemonheim", 100)); // 15
        rockTypes.add(new Navigation("Katagon rock", new Coordinate(3397, 3664, 0), "Daemonheim", 100)); // 16
        rockTypes.add(new Navigation("Gorgonite rock", new Coordinate(3504, 3736, 0), "Daemonheim", 100)); // 17
        rockTypes.add(new Navigation("Promethium rock", new Coordinate(3401, 3759, 0), "Daemonheim", 100)); // 18
        rockTypes.add(new Navigation("Bathus rock", new Coordinate(3478, 3772, 0), "Daemonheim", 100)); // 19
        rockTypes.add(new Navigation("Novite rock", new Coordinate(3440, 3642, 0), "Daemonheim", 100)); // 20
        rockTypes.add(new Navigation("Marmaros rock", new Coordinate(3503, 3734, 0), "Daemonheim", 100)); // 21
        rockTypes.add(new Navigation("Luminite rock", new Coordinate(1052, 4512, 0), "Mining Guild", 40)); // 22
        rockTypes.add(new Navigation("Coal rock", new Coordinate(3042, 9744, 0), "Mining Guild", 20)); // 23
        rockTypes.add(new Navigation("Orichalcite rock", new Coordinate(3040, 9735, 0), "Mining Guild", 60)); // 24
        rockTypes.add(new Navigation("Drakolith rock", new Coordinate(1059, 4512, 0), "Mining Guild", 60)); // 25
        rockTypes.add(new Navigation("Gold rock", new Coordinate(1062, 4571, 0), "Mining Guild", 40)); // 26
        rockTypes.add(new Navigation("Copper rock", new Coordinate(3178, 3368, 0), "Varrock South-West Mine", 1)); // 27
        rockTypes.add(new Navigation("Tin rock", new Coordinate(3177, 3369, 0), "Varrock South-West Mine", 1)); // 28
        rockTypes.add(new Navigation("Iron rock", new Coordinate(3182, 3373, 0), "Varrock South-West Mine", 10)); // 29
        rockTypes.add(new Navigation("Mithril rock", new Coordinate(3182, 3377, 0), "Varrock South-West Mine", 30)); // 30
        rockTypes.add(new Navigation("Adamantite rock", new Coordinate(3289, 3361, 0), "Varrock South-East Mine", 40)); // 31
        rockTypes.add(new Navigation("Seren stone", new Coordinate(2223, 3301, 1), "Priffdinas", 89)); // 32
        rockTypes.add(new Navigation("Runite rock", new Coordinate(3102, 3567, 0), "Wilderness", 50)); // 33
        rockTypes.add(new Navigation("Necrite rock", new Coordinate(3027, 3800, 0), "Wilderness", 70)); // 34
        rockTypes.add(new Navigation("Mineral deposit", new Coordinate(3158, 2806, 0), "Workers District", 50)); // 35
        rockTypes.add(new Navigation("Mineral deposit", new Coordinate(3190, 2743, 0), "VIP Menaphos", 50)); // 35
        rockTypes.add(new Navigation("Soft clay rock", new Coordinate(2145, 3346, 1), "Workers District", 40)); // 36
        rockTypes.add(new Navigation("Clay rock", new Coordinate(2940, 3288, 0), "Crafting Guild", 1)); // 37
        rockTypes.add(new Navigation("Prifddinas gem rock", new Coordinate(2236, 3319, 1), "Traehorn District", 75)); // 38
    }
    public static int getSelectedRockIndex() {
        return selectedRockIndex;
    }

    public static void setSelectedRockIndex(int index) {
        selectedRockIndex = index;
    }

    public static void setSelectedRockByName(String rockName, String locationName) {
        for (int i = 0; i < rockTypes.size(); i++) {
            if (rockTypes.get(i).getName().equals(rockName) && rockTypes.get(i).getLocationName().equals(locationName)) {
                selectedRockIndex = i;
                break;
            }
        }
    }
}
