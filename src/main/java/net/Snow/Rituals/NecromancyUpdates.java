package net.Snow.Rituals;

import net.botwithus.rs3.events.impl.ChatMessageEvent;
import net.botwithus.rs3.events.impl.InventoryUpdateEvent;

import java.util.HashMap;
import java.util.Map;

import static net.Snow.ImGui.CustomLogger.logError;
import static net.Snow.ImGui.CustomLogger.logWarning;
import static net.Snow.Snow.shutdown;

public class NecromancyUpdates {


    public static void onInventoryUpdate(InventoryUpdateEvent event, Map<String, Integer> inventoryMap) {
    }


    public static void onChatMessageEvent(ChatMessageEvent event, HashMap<String, Integer> inventoryMap) {
        String message = event.getMessage();

        if (message.contains("You need to choose a focus object before starting a ritual.")) {
            logError("You have run out of supplies in your Focus Storage, logging off");
            shutdown();
        }
        if (message.contains("You need the following materials to repair")) {
            logError("You are missing materials to repair, logging off");
            shutdown();
        }
        if (message.contains("The following reward is added to the ritual chest:")) {
            // Split the message to isolate the part after ": "
            String[] parts = message.split(": ");
            if (parts.length > 1) {
                // Split the part after ": " to separate the quantity and the item name
                String[] itemParts = parts[1].split(" x ");
                if (itemParts.length > 1) {
                    // Extract and clean the item name
                    String itemType = itemParts[1].trim();
                    itemType = itemType.replace("</col>", ""); // Remove the </col> tag if present

                    // Parse the quantity
                    int quantity = Integer.parseInt(itemParts[0].trim());

                    // Add the quantity to the inventory map for the corresponding item name
                    int count = inventoryMap.getOrDefault(itemType, 0);
                    inventoryMap.put(itemType, count + quantity);
                } else {
                    logWarning("Unexpected item format in message: " + message);
                }
            } else {
                logWarning("Unexpected message format: " + message);
            }
        }


    }
}
