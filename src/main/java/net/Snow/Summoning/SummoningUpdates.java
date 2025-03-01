package net.Snow.Summoning;

import net.botwithus.rs3.events.impl.ChatMessageEvent;
import net.botwithus.rs3.events.impl.InventoryUpdateEvent;
import net.botwithus.rs3.game.Item;

import java.util.Map;

public class SummoningUpdates {

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
            int addedAmount = newStackSize - oldStackSize;

            if (itemName != null) {
                int currentCount = inventoryMap.getOrDefault(itemName, 0);
                inventoryMap.put(itemName, currentCount + addedAmount);
            }
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
}
