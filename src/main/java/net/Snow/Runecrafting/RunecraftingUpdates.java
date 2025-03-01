package net.Snow.Runecrafting;

import net.botwithus.rs3.events.impl.ChatMessageEvent;
import net.botwithus.rs3.events.impl.InventoryUpdateEvent;
import net.botwithus.rs3.game.Item;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RunecraftingUpdates {

    public static void onInventoryUpdate(InventoryUpdateEvent event, Map<String, Integer> inventoryMap) {
        // Verify if the inventory update event is for inventory ID 93
        if (event.getInventoryId() != 93) {
            return;
        }

        // Retrieve the new item added to the inventory
        Item newItem = event.getNewItem();

        // If the new item is not null, proceed to update the inventory map
        if (newItem != null) {
            String itemName = newItem.getName();
            int stackSize = newItem.getStackSize();

            // Ignore updates for items that are not "Magical thread" or "Nature rune"
            if (itemName == null || (!"Flesh rune".equals(itemName) && !"Miasma rune".equals(itemName) && !"Spirit rune".equals(itemName) && !"Bone rune".equals(itemName))) {
                return;
            }

            // Update the count of the specified items in the inventoryMap
            int currentCount = inventoryMap.getOrDefault(itemName, 0);
            inventoryMap.put(itemName, currentCount + stackSize);
        }
    }


    public static void onChatMessageEvent(ChatMessageEvent event, Map<String, Integer> inventoryMap, Map<String, Integer> itemPrices, boolean fleshAltar, boolean miasmaAltar, boolean spiritAltar, boolean boneAltar) {
        String message = event.getMessage();

        // Check if the message contains GE price information
        if (message.contains("GE guide price:")) {
            Pattern pattern = Pattern.compile("GE guide price: ([\\d,]+) coins each"); // Adjust regex to handle commas
            Matcher matcher = pattern.matcher(message);

            if (matcher.find()) {
                // Extract the price from the message and remove commas
                String priceString = matcher.group(1).replace(",", "");
                int price = Integer.parseInt(priceString);

                // Determine the item name based on the altar type
                String itemName = null;
                if (fleshAltar) {
                    itemName = "Flesh rune";
                } else if (miasmaAltar) {
                    itemName = "Miasma rune";
                } else if (spiritAltar) {
                    itemName = "Spirit rune";
                } else if (boneAltar) {
                    itemName = "Bone rune";
                }

                // Update the item price map if itemName is determined
                if (itemName != null) {
                    itemPrices.put(itemName, price);
                }
            }
        }
    }

}
