package net.Snow;

import net.Snow.ImGui.SnowsTemplateGraphics;
import net.botwithus.rs3.events.impl.ChatMessageEvent;
import net.botwithus.rs3.events.impl.InventoryUpdateEvent;

import java.io.IOException;

import static net.Snow.Snow.random;

public interface AbstractScript {



    default void initialize() {
    }

    default void saveConfiguration() {
    }

    default void loadConfiguration() {
    }

    void draw(SnowsTemplateGraphics sgc) throws IOException;

    void run(Snow script);

    default void onInventoryUpdate(InventoryUpdateEvent event) {
    }

    default void onChatMessageEvent(ChatMessageEvent event) {
    }

    default int getLoopDelay() {
        return random.nextInt(750, 1900);
    }

}
