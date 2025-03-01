// WoodcuttingUpdates.java
package net.Snow.Woodcutting;

import net.botwithus.rs3.events.impl.ChatMessageEvent;
import net.botwithus.rs3.events.impl.InventoryUpdateEvent;
import net.botwithus.rs3.game.Client;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;

import java.util.Map;

import static net.Snow.Navigation.movePlayerAway;
import static net.Snow.Woodcutting.Firemaking.firemake;

public class WoodcuttingUpdates {

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
        LocalPlayer player = Client.getLocalPlayer();
        String message = event.getMessage();

        if (message.contains("You transport the following item to your bank: ")) {
            String itemName = message.substring(message.indexOf("You transport the following item to your bank: ") + 46).trim();
            int count = inventoryMap.getOrDefault(itemName, 0);
            inventoryMap.put(itemName, count + 1);
        }
        if (message.contains("You can't light a fire here.")) {
            movePlayerAway(player);
            firemake(); // Retry firemaking
        }
    }
}