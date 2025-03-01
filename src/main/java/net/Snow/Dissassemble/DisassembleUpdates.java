package net.Snow.Dissassemble;

import net.botwithus.rs3.events.impl.ChatMessageEvent;
import net.botwithus.rs3.events.impl.InventoryUpdateEvent;
import net.botwithus.rs3.game.Item;

import java.util.Map;

import static net.Snow.Dissassemble.DisassembleGraphics.materialsGained;
import static net.Snow.ImGui.CustomLogger.logError;
import static net.Snow.Snow.shutdown;

public class DisassembleUpdates {

    public static void onInventoryUpdate(InventoryUpdateEvent event, Map<String, Integer> inventoryMap) {
        if (event.getInventoryId() != 93) {
            return;
        }

        Item newItem = event.getNewItem();
        Item oldItem = event.getOldItem();

        if (newItem != null) {
            String itemName = newItem.getName();
            int newStackSize = newItem.getStackSize();
            int oldStackSize = (oldItem != null) ? oldItem.getStackSize() : 0;

            if (itemName != null) {
                int currentCount = inventoryMap.getOrDefault(itemName, 0);

                if (newItem.getId() != oldItem.getId()) {
                    // Non-stackable item was added
                    inventoryMap.put(itemName, currentCount + 1);
                } else {
                    // Stackable item or similar item updated
                    int addedAmount = newStackSize - oldStackSize;
                    inventoryMap.put(itemName, currentCount + addedAmount);
                }
            }
        }
    }



    public static void onChatMessageEvent(ChatMessageEvent event, Map<String, Integer> inventoryMap) {
        String message = event.getMessage();

        if (message.contains("Materials gained:")) {
            String[] parts = message.split(": ");
            if (parts.length > 1) {
                String[] materials = parts[1].split(", ");
                for (String material : materials) {
                    // Handle materials with HTML tags and ensure consistent formatting
                    material = material.replaceAll("</?col[^>]*>", "").trim();
                    String[] materialParts = material.split(" x ");
                    if (materialParts.length == 2) {
                        int materialCount = Integer.parseInt(materialParts[0].trim());
                        String materialName = materialParts[1].trim().toLowerCase();  // Use lowercase for consistency

                        // Update the materialsGained map
                        materialsGained.merge(materialName, materialCount, Integer::sum);
                    }
                }
            }
        }

        if (message.contains("You don't have enough")) {
            logError("[Error] You have run out of supplies, logging off");
            shutdown();
        }
    }

}
