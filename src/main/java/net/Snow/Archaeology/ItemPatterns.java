package net.Snow.Archaeology;

import java.util.regex.Pattern;

public class ItemPatterns {
    public static final Pattern MATERIAL_PATTERN = Pattern.compile(
            "Imperial iron|Purpleheart wood|Third Age iron|Zarosian insignia|Samite silk|Imperial steel|" +
                    "White oak|Goldrune|Orthenglass|Vellum|Cadmium red|Ancient vis|Tyrian purple|Leather scraps|" +
                    "Chaotic brimstone|Demonhide|Eye of Dagon|Hellfire metal|Keramos|White marble|Cobalt blue|" +
                    "Everlight silvthril|Star of Saradomin|Blood of Orcus|Soapstone|Stormguard steel|Wings of War|" +
                    "Dragon metal|Orgone|Animal furs|Armadylean yellow|Malachite green|Mark of the Kyzaj|Vulcanised rubber|" +
                    "Warforged bronze|Compass rose|Carbon black|Felt|Fossilised bone|Yu'biusk clay|Aetherium alloy|Quintessence",
            Pattern.CASE_INSENSITIVE
    );
}