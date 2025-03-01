package net.Snow.Prayer;

import net.botwithus.rs3.events.impl.ChatMessageEvent;
import net.botwithus.rs3.events.impl.InventoryUpdateEvent;

import java.util.Map;

public class PrayerUpdates {

    public static void onInventoryUpdate(InventoryUpdateEvent event, Map<String, Integer> inventoryMap) {
        // Verify if the inventory update event is for inventory ID 93
        if (event.getInventoryId() != 93) {
            return;
        }

        // Retrieve the name of the new item added to the inventory
        String itemName = event.getNewItem().getName();

        // If the item name is not null, update the count of this item in the inventoryMap
        if (itemName != null) {
            int count = inventoryMap.getOrDefault(itemName, 0);
            inventoryMap.put(itemName, count + 1);
        }
    }

    public static void onChatMessageEvent(ChatMessageEvent event, Map<String, Integer> inventoryMap) {
        String message = event.getMessage();
    }
}
