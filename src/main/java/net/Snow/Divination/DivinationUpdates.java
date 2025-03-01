package net.Snow.Divination;

import net.botwithus.rs3.events.impl.ChatMessageEvent;
import net.botwithus.rs3.events.impl.InventoryUpdateEvent;
import net.botwithus.rs3.game.Item;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DivinationUpdates {

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



    public static void onChatMessageEvent(ChatMessageEvent event, Map<String, Integer> inventoryMap, Map<String, Integer> itemPrices, long scriptStartTime) {
        String message = event.getMessage();

        // Check if the message contains GE price information
        if (message.contains("GE guide price:")) {
            Pattern pattern = Pattern.compile("GE guide price: ([\\d,]+) coins each"); // Regex to handle commas
            Matcher matcher = pattern.matcher(message);

            if (matcher.find()) {
                // Extract the price from the message and remove commas
                String priceString = matcher.group(1).replace(",", "");
                int price = Integer.parseInt(priceString);

                // Determine the item name based on the context (Incandescent energy in this case)
                String itemName = "Incandescent energy";

                // Update the item price map if itemName is determined
                if (itemName != null) {
                    itemPrices.put(itemName, price);
                }
            }
        }
    }
}
