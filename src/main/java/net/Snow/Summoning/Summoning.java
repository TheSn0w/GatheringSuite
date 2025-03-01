package net.Snow.Summoning;

import net.Snow.AbstractScript;
import net.Snow.ImGui.SnowsTemplateGraphics;
import net.Snow.Snow;
import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.rs3.events.impl.ChatMessageEvent;
import net.botwithus.rs3.events.impl.InventoryUpdateEvent;
import net.botwithus.rs3.game.Item;
import net.botwithus.rs3.game.hud.interfaces.Interfaces;
import net.botwithus.rs3.game.js5.types.vars.VarDomainType;
import net.botwithus.rs3.game.minimenu.MiniMenu;
import net.botwithus.rs3.game.minimenu.actions.ComponentAction;
import net.botwithus.rs3.game.queries.builders.characters.NpcQuery;
import net.botwithus.rs3.game.queries.builders.items.InventoryItemQuery;
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery;
import net.botwithus.rs3.game.queries.results.EntityResultSet;
import net.botwithus.rs3.game.queries.results.ResultSet;
import net.botwithus.rs3.game.scene.entities.characters.npc.Npc;
import net.botwithus.rs3.game.scene.entities.object.SceneObject;
import net.botwithus.rs3.game.skills.Skills;
import net.botwithus.rs3.game.vars.VarManager;
import net.botwithus.rs3.script.Execution;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import static net.Snow.ImGui.CustomLogger.log;
import static net.Snow.ImGui.CustomLogger.logSuccess;
import static net.Snow.ImGui.Experience.inventoryMap;
import static net.Snow.ImGui.Experience.startTracking;
import static net.Snow.Snow.*;

public class Summoning implements AbstractScript {

    Snow script;

    @Override
    public void initialize() {
        startingSummoningXP.set(Skills.SUMMONING.getSkill().getExperience());
        startingSummoningLevel.set(Skills.SUMMONING.getSkill().getActualLevel());
        startTracking();

    }

    public AtomicInteger startingSummoningXP = new AtomicInteger(0);
    public AtomicInteger startingSummoningLevel = new AtomicInteger(0);


    @Override
    public void draw(SnowsTemplateGraphics sgc) throws IOException {
        SummoningGraphics.draw(sgc, script, startingSummoningLevel, startingSummoningXP);
    }

    @Override
    public void onInventoryUpdate(InventoryUpdateEvent event) {
        SummoningUpdates.onInventoryUpdate(event, inventoryMap);
    }

    @Override
    public void onChatMessageEvent(ChatMessageEvent event) {
        SummoningUpdates.onChatMessageEvent(event, inventoryMap);
    }

    @Override
    public int getLoopDelay() {
        return 50;
    }

    private static final EntityResultSet<Npc> magestix = NpcQuery.newQuery().id(17094).results();
    private final EntityResultSet<SceneObject> obelisk = SceneObjectQuery.newQuery().id(67036).results();


    @Override
    public void run(Snow script) {

        // Check if the backpack has at least 10 of both items
        if (Backpack.getCount(49504) >= 10 && Backpack.getCount(49508) >= 10) {
            obelisk.nearest().interact("Infuse-pouch");
            Execution.delayUntil(5000, () -> Interfaces.isOpen(1370));
            if (Interfaces.isOpen(1370)) {
                selectInterface();
            }
        } else {
            interactWithMagestix();
        }
    }

    private static void interactWithMagestix() {
        // Query for the NPC Magestix
        EntityResultSet<Npc> magestix = NpcQuery.newQuery().name("Magestix").option("Trade").results();
        if (!magestix.isEmpty()) {
            Npc nearestMagestix = magestix.nearest();
            nearestMagestix.interact("Trade");
            log("[Summoning] Interacting with Magestix");
            Execution.delayUntil(5000, () -> Interfaces.isOpen(1265)); // Consistent delayUntil usage
        }

        // Check if the trade interface is open
        if (Interfaces.isOpen(1265)) {
            log("[Summoning] Interface is open");
            Execution.delay(random.nextLong(200, 500));

            // Query for the items to buy
            ResultSet<Item> items1 = InventoryItemQuery.newQuery(628).ids(49504).results();
            ResultSet<Item> items2 = InventoryItemQuery.newQuery(628).ids(49508).results();

            // Check if both items have at least 10 in quantity
            boolean hasEnoughItemsToBuy = !items1.isEmpty() && items1.first().getStackSize() >= 10 &&
                    !items2.isEmpty() && items2.first().getStackSize() >= 10;

            if (hasEnoughItemsToBuy) {
                log("[Summoning] Enough items to buy.");

                // Attempt to buy the first item
                Item item = items1.first();
                int slot = item.getSlot();
                MiniMenu.interact(ComponentAction.COMPONENT.getType(), 4, slot, 82903060);
                log("[Summoning] Buying all: " + item.getName());

                boolean firstItemBought = Execution.delayUntil(5000, () -> Backpack.contains(49504));
                if (!firstItemBought) {
                    log("[Error] Failed to buy the first item. Retrying...");
                    interactWithMagestix();
                    return;
                }

//                Execution.delay(random.nextLong(500, 700));

                // Attempt to buy the second item
                Item item2 = items2.first();
                int slot2 = item2.getSlot();
                MiniMenu.interact(ComponentAction.COMPONENT.getType(), 4, slot2, 82903060);
                log("[Summoning] Buying all: " + item2.getName());

                boolean secondItemBought = Execution.delayUntil(5000, () -> Backpack.contains(49508));
                if (!secondItemBought) {
                    log("[Error] Failed to buy the second item. Retrying to buy only the second item...");
                    retryBuyingSecondItem();
                    return;
                }

            } else {
                handleSelling();
                // After selling, recheck if enough items can be bought
                interactWithMagestix();
            }
        }
    }

    private static void retryBuyingSecondItem() {
        // Ensure the trade interface is open
        if (Interfaces.isOpen(1265)) {
            ResultSet<Item> items2 = InventoryItemQuery.newQuery(628).ids(49508).results();
            if (!items2.isEmpty() && items2.first().getStackSize() >= 10) {
                // Buy the second item
                Item item2 = items2.first();
                int slot2 = item2.getSlot();
                MiniMenu.interact(ComponentAction.COMPONENT.getType(), 4, slot2, 82903060);
                log("[Summoning] Retrying to buy the second item: " + item2.getName());

                if (!Execution.delayUntil(5000, () -> Backpack.contains(49508))) {
                    log("[Error] Failed again to buy the second item. Stopping attempts.");
                    shutdown();
                }
            } else {
                log("[Summoning] Not enough of the second item to buy. Exiting.");
                shutdown();
            }
        } else {
            log("[Summoning] Trade interface is not open. Exiting.");
            shutdown();
        }
    }


    private static void handleSelling() {
        log("[Summoning] Not enough items to buy.");
        if (MiniMenu.interact(ComponentAction.COMPONENT.getType(), 1, -1, 82903072)) {
            log("[Summoning] Selecting [Sell]");
            Execution.delayUntil(5000, () -> VarManager.getVarValue(VarDomainType.PLAYER, 303) == 1);
        }

        if (VarManager.getVarValue(VarDomainType.PLAYER, 303) == 1) {
            Execution.delay(random.nextLong(500, 700));

            // Sell the first item
            ResultSet<Item> sellItems1 = InventoryItemQuery.newQuery(93).ids(49505).results();
            if (!sellItems1.isEmpty()) {
                Item sellItem1 = sellItems1.first();
                int sellSlot1 = sellItem1.getSlot();
                MiniMenu.interact(ComponentAction.COMPONENT.getType(), 5, sellSlot1, 82903060);
            } else {
                log("[Error] No items left to sell.");
                Execution.delay(random.nextLong(1000, 5000));
                shutdown();
                return;
            }

            Execution.delay(random.nextLong(500, 700));

            // Sell the second item
            ResultSet<Item> sellItems2 = InventoryItemQuery.newQuery(93).ids(49509).results();
            if (!sellItems2.isEmpty()) {
                Item sellItem2 = sellItems2.first();
                int sellSlot2 = sellItem2.getSlot();
                MiniMenu.interact(ComponentAction.COMPONENT.getType(), 5, sellSlot2, 82903060);
                log("[Summoning] Sold the second item successfully.");
            } else {
                log("[Error] No items left to sell.");
                Execution.delay(random.nextLong(1000, 5000));
                shutdown();
                return; // Exit the method if no second item to sell
            }
        }
    }





    private static void selectInterface() {

        Execution.delayUntil(5000, () -> Interfaces.isOpen(1370));

        //[Original]: DoAction(DIALOGUE, 0, -1, 89784350)
        dialog(0, -1, 89784350);
            logSuccess("Successfully interacted with Dialog!");
            Execution.delayUntil(30000, () -> VarManager.getVarValue(VarDomainType.PLAYER, 1176) == 500);

    }
}
