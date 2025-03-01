package net.Snow.Archaeology.Collections;

import net.botwithus.rs3.game.skills.Skills;
import net.botwithus.rs3.game.vars.VarManager;

import java.util.Arrays;
import java.util.List;

import static net.Snow.ImGui.CustomLogger.*;

public class CollectionManager {

    // Enum to define the collection names and their corresponding Varbit IDs
    public enum Collection {
        ZAMORAKIAN_I("Zamorakian I", 46048, 36),
        MUSEUM_ZAMORAKIAN_I("Museum - Zamorakian I", 46071, 36),
        ZAROSIAN_I("Zarosian I", 46059, 25),
        MUSEUM_ZAROSIAN_I("Museum - Zarosian I", 46063, 25),
        SARADOMINIST_I("Saradominist I", 46055, 56),
        MUSEUM_SARADOMINIST_I("Museum - Saradominist I", 46067, 56),
        BLINGY_FINGS("Blingy Fings", 46040, 69),
        SARADOMINIST_II("Saradominist II", 46056, 72),
        MUSEUM_SARADOMINIST_II("Museum - Saradominist II", 46068, 72),
        DRAGONKIN_V("Dragonkin V", 55529, 77),
        MUSEUM_DRAGONKIN_V("Museum - Dragonkin V", 55532, 77),
        SMOKY_FINGS("Smoky Fings", 46038, 81),
        ZAROSIAN_II("Zarosian II", 46060, 81),
        ZAMORAKIAN_II("Zamorakian II", 46049, 81),
        ARMADYLEAN_I("Armadylean I", 46052, 81),
        MUSEUM_ARMADYLEAN_I("Museum - Armadylean I", 46075, 81),
        MUSEUM_ZAMORAKIAN_II("Museum - Zamorakian II", 46072, 81),
        MUSEUM_ZAROSIAN_II("Museum - Zarosian II", 46064, 81),
        GREEN_GOBBO_GOODIES_I("Green Gobbo Goodies I", 46045, 83),
        DRAGONKIN_VI("Dragonkin VI", 55530, 87),
        MUSEUM_DRAGONKIN_VI("Museum - Dragonkin VI", 55533, 87),
        ANARCHIC_ABSTRACTION("Anarchic Abstraction", 46037, 89),
        HITTY_FINGS("Hitty Fings", 46041, 89),
        MUSEUM_BANDOSIAN_I("Museum - Bandosian I", 46078, 89),
        WISE_AM_THE_MUSIC_MAN("Wise Am the Music Man", 46081, 91),
        SHOWY_FINGS("Showy Fings", 46039, 92),
        RED_RUM_RELICS_I("Red Rum Relics I", 46042, 94),
        GREEN_GOBBO_GOODIES_II("Green Gobbo Goodies II", 46046, 97),
        ARMADYLEAN_II("Armadylean II", 46053, 98),
        MUSEUM_ARMADYLEAN_II("Museum - Armadylean II", 46076, 98),
        DRAGONKIN_I("Dragonkin I", 48034, 99),
        MUSEUM_DRAGONKIN_I("Museum - Dragonkin I", 48030, 99),
        MUSEUM_BANDOSIAN_II("Museum - Bandosian II", 46079, 100),
        MUSEUM_SARADOMINIST_III("Museum - Saradominist III", 46069, 100),
        SARADOMINIST_III("Saradominist III", 46057, 100),
        MUSEUM_DRAGONKIN_II("Museum - Dragonkin II", 48031, 102),
        DRAGONKIN_II("Dragonkin II", 48035, 102),
        MUSEUM_ZAMORAKIAN_III("Museum - Zamorakian III", 46073, 104),
        ZAMORAKIAN_III("Zamorakian III", 46050, 104),
        RADIANT_RENAISSANCE("Radiant Renaissance", 46036, 105),
        MUSEUM_ZAROSIAN_III("Museum - Zarosian III", 46065, 107),
        ZAROSIAN_III("Zarosian III", 46061, 107),
        MUSEUM_DRAGONKIN_III("Museum - Dragonkin III", 48032, 108),
        DRAGONKIN_III("Dragonkin III", 48036, 108),
        RED_RUM_RELICS_II("Red Rum Relics II", 46043, 110),
        MUSEUM_DRAGONKIN_VII("Museum - Dragonkin VII", 55534, 113),
        DRAGONKIN_VII("Dragonkin VII", 55531, 113),
        HAT_PROBLEM("Hat Problem", 46083, 114),
        HAT_HOARDER("Hat Hoarder", 46082, 116),
        MUSEUM_ZAMORAKIAN_IV("Museum - Zamorakian IV", 46074, 116),
        ZAMORAKIAN_IV("Zamorakian IV", 46051, 116),
        MUSEUM_SARADOMINIST_IV("Museum - Saradominist IV", 46070, 117),
        SARADOMINIST_IV("Saradominist IV", 46058, 117),
        MAGIC_MAN("Magic Man", 46084, 118),
        MUSEUM_ARMADYLEAN_III("Museum - Armadylean III", 46077, 118),
        MUSEUM_ZAROSIAN_IV("Museum - Zarosian IV", 46066, 118),
        ZAROSIAN_IV("Zarosian IV", 46062, 118),
        ARMADYLEAN_III("Armadylean III", 46054, 118),
        IMPERIAL_IMPRESSIONISM("Imperial Impressionism", 46035, 118),
        KNOWLEDGE_IS_POWER("Knowledge is Power", 46085, 119),
        MUSEUM_BANDOSIAN_III("Museum - Bandosian III", 46080, 119),
        GREEN_GOBBO_GOODIES_III("Green Gobbo Goodies III", 46047, 119),
        RED_RUM_RELICS_III("Red Rum Relics III", 46044, 119),
        MUSEUM_DRAGONKIN_IV("Museum - Dragonkin IV", 48033, 120),
        DRAGONKIN_IV("Dragonkin IV", 48037, 120),
        MUSEUM_ZAROSIAN_V("Museum - Zarosian V", 49802, 62),
        MUSEUM_ZAROSIAN_VI("Museum - Zarosian VI", 49803, 64),
        MUSEUM_ZAROSIAN_VII("Museum - Zarosian VII", 49804, 67),
        ENTERTAINING_THE_MASSES("Entertaining the Masses", 43668, 67),
        RELIGIOUS_ICONOGRAPHY("Religious Iconography", 30938, 67),
        URNS_OF_THE_EMPIRE("Urns of the Empire", 30940, 67),
        IMPERIAL_SORCERY("Imperial Sorcery", 44901, 107),
        FINERY_OF_THE_INQUISITION("Finery of the Inquisition", 30936, 64);

        private final String name;
        private final int varbitId;
        private final int minLevel;

        Collection(String name, int varbitId, int minLevel) {
            this.name = name;
            this.varbitId = varbitId;
            this.minLevel = minLevel;
        }

        public String getName() {
            return name;
        }

        public int getVarbitId() {
            return varbitId;
        }

        public int getMinLevel() {
            return minLevel;
        }
    }

    /**
     * Checks if a collection is complete based on its Varbit value.
     *
     * @param collection The collection enum representing the desired collection.
     * @return True if the collection is complete, false otherwise.
     */
    public static boolean isCollectionComplete(Collection collection) {
        int varbitValue = VarManager.getVarbitValue(collection.getVarbitId());
        return varbitValue == 1;
    }

    /**
     * Prints whether a collection is complete.
     *
     * @param collection The collection enum representing the desired collection.
     */
    public void printCollectionStatus(Collection collection) {
        boolean isComplete = isCollectionComplete(collection);
        logError(collection.getName() + " complete: " + isComplete);
    }

    /**
     * Logs the completion status of all collections.
     */
    // In CollectionManager.java

    public static void logAllCollectionsStatus() {
        int completedCount = 0;
        int totalCount = Collection.values().length;

        for (Collection collection : Collection.values()) {
            boolean isComplete = isCollectionComplete(collection);
            boolean canComplete = canCompleteCollection(collection);

            if (isComplete) {
                logSuccess(collection.getName() + " - complete: " + isComplete + ".");
                completedCount++;
            } else {
                logError(collection.getName() + " - complete: " + isComplete + ", can complete: " + canComplete);
            }
        }

        int notCompletedCount = totalCount - completedCount;
        logOrange("Total collections: " + totalCount);
        logOrange("Completed collections: " + completedCount);
        logOrange("Not completed collections: " + notCompletedCount);
    }

    /**
     * Checks if a collection can be completed based on the player's level.
     *
     * @param collection The collection enum representing the desired collection.
     * @return True if the player can complete the collection, false otherwise.
     */
    public static boolean canCompleteCollection(Collection collection) {
        int playerLevel = Skills.ARCHAEOLOGY.getActualLevel(); // Example: Replace with actual method to get player level
        return playerLevel >= collection.getMinLevel();
    }
    // In CollectionManager.java
    public static List<Collection> getAllCollections() {
        return Arrays.asList(Collection.values());
    }

    /**
     * Cross-references collection names from the Artifact class.
     *
     * @param artifact The artifact to check.
     */
    public void checkArtifactCollections(Artifact artifact) {
        List<String> collectionNames = artifact.getCollectionNames();
        for (String collectionName : collectionNames) {
            try {
                Collection collection = Collection.valueOf(collectionName.toUpperCase().replace(" ", "_"));
                printCollectionStatus(collection);
            } catch (IllegalArgumentException e) {
                logError("Collection not found for name: " + collectionName);
            }
        }
    }

}