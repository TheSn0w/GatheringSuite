package net.Snow.Notepaper;

import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.rs3.game.Item;
import net.botwithus.rs3.game.js5.types.configs.ConfigManager;
import net.botwithus.rs3.game.minimenu.MiniMenu;
import net.botwithus.rs3.game.minimenu.actions.SelectableAction;
import net.botwithus.rs3.script.Execution;
import net.botwithus.rs3.util.RandomGenerator;

import java.util.ArrayList;
import java.util.List;

import static net.Snow.ImGui.CustomLogger.*;

public class Notepaper {

    public static String NotepaperName = "";
    public static int selectedNotepaperIndex = 0;


    public static String getNotepaperName() {
        return NotepaperName;
    }

    public static void setNotepaperName(String notepaperName) {
        NotepaperName = notepaperName;
    }

    public static void removeNotepaperName(String notepaperName) {
        log("[Notepaper] Removing " + notepaperName + " from selected notepaper names.");
        selectedNotepaperNames.remove(notepaperName);
    }

    public static List<String> selectedNotepaperNames = new ArrayList<>();

    public static List<String> getSelectedNotepaperNames() {
        return selectedNotepaperNames;
    }

    public static void addNotepaperName(String notepaperName) {
        log("[Notepaper] Adding " + notepaperName + " to selected notepaper names.");
        selectedNotepaperNames.add(notepaperName);
    }


    public static void useItemOnNotepaper() {
        List<Item> backpackItems = new ArrayList<>(Backpack.getItems());

        for (String itemName : getSelectedNotepaperNames()) {
            List<Item> matchingItems = backpackItems.stream()
                    .filter(item -> item.getName().toLowerCase().contains(itemName.toLowerCase()))
                    .toList();

            for (Item targetItem : matchingItems) {
                var itemType = ConfigManager.getItemType(targetItem.getId());
                boolean isNote = itemType != null && itemType.isNote();
                if (isNote) {
                    continue;
                }

                Item notepaper = fetchNotepaperFromInventory();
                if (notepaper == null) {
                    logError("[Notepaper] Neither Magic Notepaper nor Enchanted Notepaper found in inventory.");
                    return;
                }

                boolean itemSelected = MiniMenu.interact(SelectableAction.SELECTABLE_COMPONENT.getType(), 0, targetItem.getSlot(), 96534533);
               log("[Notepaper] Item selected: " + itemSelected);
                Execution.delay(RandomGenerator.nextInt(200, 300));

                if (itemSelected) {
                    boolean notepaperSelected = MiniMenu.interact(SelectableAction.SELECT_COMPONENT_ITEM.getType(), 0, notepaper.getSlot(), 96534533);
                    log("[Notepaper] Notepaper selected: " + notepaperSelected);

                    if (notepaperSelected) {
                        String notepaperName = notepaper.getName();
                        logSuccess("[Notepaper] " + itemName + " successfully used on " + notepaperName + ".");
                        Execution.delay(RandomGenerator.nextInt(650, 750));
                        break;
                    } else {
                        String notepaperName = notepaper.getName();
                        logError("[Notepaper] Failed to use " + itemName + " on " + notepaperName + ".");
                        logError("[Notepaper] Notepaper details - Name: " + notepaper.getName() + ", ID: " + notepaper.getId());
                    }
                } else {
                    logError("[Notepaper] Failed to select " + itemName + ".");
                    logError("[Notepaper] Item details - Name: " + targetItem.getName() + ", ID: " + targetItem.getId());
                }
            }
        }
    }

    private static Item fetchNotepaperFromInventory() {
        Item magicNotepaper = fetchSpecificNotepaper("Magic notepaper");

        if (magicNotepaper == null) {
            logError("[Notepaper] Magic Notepaper not found in inventory. Trying to fetch Enchanted notepaper...");
            Item enchantedNotepaper = fetchSpecificNotepaper("Enchanted notepaper");

            if (enchantedNotepaper == null) {
                logError("[Notepaper] Enchanted Notepaper not found in inventory.");
                return null;
            } else {
                return enchantedNotepaper;
            }
        } else {
            return magicNotepaper;
        }
    }

    private static Item fetchSpecificNotepaper(String notepaperName) {
        Item notepaper = Backpack.getItem(notepaperName);
        if (notepaper != null) {
            logOrange("[Notepaper] Notepaper found: " + notepaper.getName());
            return notepaper;
        }
        log("[Notepaper] " + notepaperName + " not found in inventory.");
        return null;
    }
}
