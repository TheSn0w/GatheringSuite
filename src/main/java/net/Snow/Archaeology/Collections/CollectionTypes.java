package net.Snow.Archaeology.Collections;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CollectionTypes {

    public static int selectedCollectionIndex = 0;
    public static List<Collection> collections = new ArrayList<>();

    static {
        collections.addAll(Arrays.asList(
                new Collection("Disabled", 0, null),
                new Collection("Zamorakian I", 36, Arrays.asList(2373)),
                new Collection("Museum - Zamorakian I", 36, Arrays.asList(2373)),
                new Collection("Zarosian I", 25, Arrays.asList(2388)),
                new Collection("Museum - Zarosian I", 25,Arrays.asList(2388)),
                new Collection("Saradominist I", 56, null),
                new Collection("Museum - Saradominist I", 56, null),
                new Collection("Blingy Fings", 69 , null),
                new Collection("Saradominist II", 72, null),
                new Collection("Museum - Saradominist II", 72, null),
                new Collection("Dragonkin V", 77, null),
                new Collection("Museum - Dragonkin V", 77, null),
                new Collection("Smoky Fings", 81, null),
                new Collection("Zarosian II", 81, null),
                new Collection("Zamorakian II", 81, null),
                new Collection("Armadylean I", 81, null),
                new Collection("Museum - Armadylean I", 81, null),
                new Collection("Museum - Zamorakian II", 81, null),
                new Collection("Museum - Zarosian II", 81, null),
                new Collection("Green Gobbo Goodies I", 83, null),
                new Collection("Dragonkin VI", 87, null),
                new Collection("Museum - Dragonkin VI", 87, null),
                new Collection("Anarchic Abstraction", 89, null),
                new Collection("Hitty Fings", 89, null),
                new Collection("Museum - Bandosian I", 89, null),
                new Collection("Wise Am the Music Man", 91, null),
                new Collection("Showy Fings", 92, null),
                new Collection("Red Rum Relics I", 94, null),
                new Collection("Green Gobbo Goodies II", 97, null),
                new Collection("Armadylean II", 98, null),
                new Collection("Museum - Armadylean II", 98, null),
                new Collection("Dragonkin I", 99, null),
                new Collection("Museum - Dragonkin I", 99, null),
                new Collection("Museum - Bandosian II", 100, null),
                new Collection("Museum - Saradominist III", 100, null),
                new Collection("Saradominist III", 100, null),
                new Collection("Museum - Dragonkin II", 102, null),
                new Collection("Dragonkin II", 102, null),
                new Collection("Museum - Zamorakian III", 104, null),
                new Collection("Zamorakian III", 104, null),
                new Collection("Radiant Renaissance", 105, null),
                new Collection("Museum - Zarosian III", 107, null),
                new Collection("Zarosian III", 107, null),
                new Collection("Imperial Sorcery", 107, null),
                new Collection("Museum - Dragonkin III", 108, null),
                new Collection("Dragonkin III", 108, null),
                new Collection("Red Rum Relics II", 110, null),
                new Collection("Museum - Dragonkin VII", 113, null),
                new Collection("Dragonkin VII", 113, null),
                new Collection("Hat Problem", 114, null),
                new Collection("Hat Hoarder", 116, null),
                new Collection("Museum - Zamorakian IV", 116, null),
                new Collection("Zamorakian IV", 116, null),
                new Collection("Museum - Saradominist IV", 117, null),
                new Collection("Saradominist IV", 117, null),
                new Collection("Magic Man", 118, null),
                new Collection("Museum - Armadylean III", 118, null),
                new Collection("Museum - Zarosian IV", 118, null),
                new Collection("Zarosian IV", 118, null),
                new Collection("Armadylean III", 118, null),
                new Collection("Imperial Impressionism", 118, null),
                new Collection("Knowledge is Power", 119, null),
                new Collection("Museum - Bandosian III", 119, null),
                new Collection("Green Gobbo Goodies III", 119, null),
                new Collection("Red Rum Relics III", 119, null),
                new Collection("Museum - Dragonkin IV", 120, null),
                new Collection("Dragonkin IV", 120, null),
                new Collection("Museum - Zarosian V", 62, null),
                new Collection("Museum - Zarosian VI", 64, null),
                new Collection("Finery of the Inquisition", 64, null),
                new Collection("Museum - Zarosian VII", 67, Arrays.asList(3023)),
                new Collection("Entertaining the Masses", 67,Arrays.asList(3023)),
                new Collection("Religious Iconography", 67, Arrays.asList(3023)),
                new Collection("Urns of the Empire", 67, Arrays.asList(3023))
        ));
    }
}