package net.Snow.Herblore;

import net.botwithus.rs3.events.impl.ChatMessageEvent;
import net.botwithus.rs3.events.impl.InventoryUpdateEvent;

import java.util.Map;

public class HerbloreUpdates {

    private static boolean isLoadingPreset = false; // Flag to indicate if the inventory is being loaded from a preset

    public static void onInventoryUpdate(InventoryUpdateEvent event, Map<String, Integer> inventoryMap) {
        // Verify if the inventory update event is for inventory ID 93
        if (event.getInventoryId() != 93 || isLoadingPreset) {
            return; // Ignore updates when loading preset or if the inventory ID is not 93
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

        if (message.contains("You transport the following item to your bank: ")) {
            String itemName = message.substring(message.indexOf("You transport the following item to your bank: ") + 46).trim();
            int count = inventoryMap.getOrDefault(itemName, 0);
            inventoryMap.put(itemName, count + 1);
        }
    }

    // Methods to set and reset the flag
    public static void setIsLoadingPreset(boolean isLoading) {
        isLoadingPreset = isLoading;
    }

//    public static boolean isLoadingPreset() {
//        return isLoadingPreset;
//    }
}

