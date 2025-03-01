package net.Snow.SlimeCollector;

import net.botwithus.rs3.events.impl.ChatMessageEvent;
import net.botwithus.rs3.events.impl.InventoryUpdateEvent;
import net.botwithus.rs3.game.Client;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;

import java.util.Map;

public class SlimeCollectorUpdates {

    public static void onInventoryUpdate(InventoryUpdateEvent event, Map<String, Integer> inventoryMap) {
        // Verify if the inventory update event is for inventory ID 93
        if (event.getInventoryId() != 93) {
            return;
        }
    }

    public static void onChatMessageEvent(ChatMessageEvent event, Map<String, Integer> inventoryMap) {
        LocalPlayer player = Client.getLocalPlayer();
        String message = event.getMessage();

    }
}
