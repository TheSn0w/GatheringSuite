package net.Snow.Prayer;

import net.Snow.AbstractScript;
import net.Snow.ImGui.SnowsTemplateGraphics;
import net.Snow.Snow;
import net.botwithus.rs3.events.impl.ChatMessageEvent;
import net.botwithus.rs3.events.impl.InventoryUpdateEvent;
import net.botwithus.rs3.game.Area;
import net.botwithus.rs3.game.Client;
import net.botwithus.rs3.game.Coordinate;
import net.botwithus.rs3.game.queries.builders.characters.NpcQuery;
import net.botwithus.rs3.game.queries.builders.items.InventoryItemQuery;
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery;
import net.botwithus.rs3.game.queries.results.EntityResultSet;
import net.botwithus.rs3.game.scene.entities.characters.npc.Npc;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.game.scene.entities.object.SceneObject;
import net.botwithus.rs3.game.skills.Skills;
import net.botwithus.rs3.script.Execution;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

import static net.Snow.ImGui.CustomLogger.*;
import static net.Snow.ImGui.Experience.inventoryMap;
import static net.Snow.ImGui.Experience.startTracking;
import static net.Snow.Navigation.MoveTo;
import static net.Snow.Snow.shutdown;

public class Prayer implements AbstractScript {

    Snow script;

    LocalPlayer player = Client.getLocalPlayer();


    @Override
    public void initialize() {
        startingPrayerXP.set(Skills.PRAYER.getSkill().getExperience());
        startingPrayerLevel.set(Skills.PRAYER.getSkill().getLevel());
        startTracking();

    }

    public AtomicInteger startingPrayerXP = new AtomicInteger(0);
    public AtomicInteger startingPrayerLevel = new AtomicInteger(0);


    @Override
    public void draw(SnowsTemplateGraphics sgc) {
        PrayerGraphics.draw(sgc, script, startingPrayerLevel, startingPrayerXP);
    }

    @Override
    public void onInventoryUpdate(InventoryUpdateEvent event) {
        PrayerUpdates.onInventoryUpdate(event, inventoryMap);
    }

    @Override
    public void onChatMessageEvent(ChatMessageEvent event) {
        PrayerUpdates.onChatMessageEvent(event, inventoryMap);
    }

    private static final Pattern ITEM_PATTERN = Pattern.compile("(?i)(bone|ashes)");

    public static final Area ALTAR_AREA = new Area.Rectangular(
            new Coordinate(3220, 3591, 0), // Bottom-left coordinate of the area
            new Coordinate(3255, 3621, 0)  // Top-right coordinate of the area
    );

    public void run(Snow script) {

        if (ALTAR_AREA.contains(player)) {
            EntityResultSet<Npc> bank = NpcQuery.newQuery().name("Simon").option("Talk to").results();

            if (player.isMoving() || player.getAnimationId() != -1) {
                return;
            }

            if (InventoryItemQuery.newQuery(93).name(ITEM_PATTERN).results().isEmpty()) {
                logBlue("No bones or ashes found");
                if (bank == null) {
                    logBlue("No bank found");
                } else {
                    bank.nearest().interact("Load Last Preset from");
                    logSuccess("Interacted with " + bank.nearest().getName());
                    Execution.delayUntil(120000, () -> !InventoryItemQuery.newQuery(93).name(ITEM_PATTERN).results().isEmpty());
                    if (InventoryItemQuery.newQuery(93).name(ITEM_PATTERN).results().isEmpty()) {
                        shutdown();
                    }
                }
            } else {
                EntityResultSet<SceneObject> altar = SceneObjectQuery.newQuery().id(122374).option("Pray at").results();
                if (altar == null) {
                    logBlue("No altar found");
                } else {
                    altar.nearest().interact("Offer");
                    logSuccess("Interacted with " + altar.nearest().getName());
                    Execution.delayUntil(120000, () -> InventoryItemQuery.newQuery(93).name(ITEM_PATTERN).results().isEmpty());
                }
            }
        } else {
            logWarning("Not in the correct area");
            MoveTo(new Coordinate(3240, 3609, 0));
        }
    }
}
