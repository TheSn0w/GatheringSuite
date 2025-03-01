package net.Snow.Dissassemble;

import net.Snow.AbstractScript;
import net.Snow.ImGui.SnowsTemplateGraphics;
import net.Snow.Snow;
import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.rs3.events.impl.ChatMessageEvent;
import net.botwithus.rs3.events.impl.InventoryUpdateEvent;
import net.botwithus.rs3.game.Item;
import net.botwithus.rs3.game.actionbar.ActionBar;
import net.botwithus.rs3.game.hud.interfaces.Component;
import net.botwithus.rs3.game.hud.interfaces.Interfaces;
import net.botwithus.rs3.game.minimenu.MiniMenu;
import net.botwithus.rs3.game.minimenu.actions.SelectableAction;
import net.botwithus.rs3.game.queries.builders.components.ComponentQuery;
import net.botwithus.rs3.game.skills.Skills;
import net.botwithus.rs3.script.Execution;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import static net.Snow.ImGui.CustomLogger.log;
import static net.Snow.ImGui.CustomLogger.logSuccess;
import static net.Snow.ImGui.Experience.inventoryMap;
import static net.Snow.ImGui.Experience.startTracking;
import static net.Snow.Snow.capturestuff;
import static net.Snow.Snow.shutdown;

public class Disassemble implements AbstractScript {

    Snow script;

    @Override
    public void initialize() {
        startingInventionXP.set(Skills.INVENTION.getSkill().getExperience());
        startingInventionLevel.set(Skills.INVENTION.getSkill().getActualLevel());
        startTracking();

    }

    public AtomicInteger startingInventionXP = new AtomicInteger(0);
    public AtomicInteger startingInventionLevel = new AtomicInteger(0);


    @Override
    public void draw(SnowsTemplateGraphics sgc) throws IOException {
        DisassembleGraphics.draw(sgc, script, startingInventionLevel, startingInventionXP);
    }

    @Override
    public void onInventoryUpdate(InventoryUpdateEvent event) {
        DisassembleUpdates.onInventoryUpdate(event, inventoryMap);
    }

    @Override
    public void onChatMessageEvent(ChatMessageEvent event) {
        DisassembleUpdates.onChatMessageEvent(event, inventoryMap);
    }

    @Override
    public void run(Snow script) {

        capturestuff();

        if (Interfaces.isOpen(1251)) {
            return;
        }
        disassembleAllItems();
    }

    public static void disassembleAllItems() {
        for (Item item : Backpack.getItems()) {
            if (item != null && ActionBar.containsAbility("Disassemble")) {
                Component disassemble = ComponentQuery.newQuery(1430, 1670, 1671, 1672, 1673).spriteId(12510).option("Customise-keybind").results().first();
                if (disassemble != null) {
                    logSuccess("Used disassemble spell: " + MiniMenu.interact(SelectableAction.SELECTABLE_COMPONENT.getType(), 0, -1, disassemble.getInterfaceIndex() << 16 | disassemble.getComponentIndex()));
                    logSuccess("Selected disassemble item: " + MiniMenu.interact(SelectableAction.SELECT_COMPONENT_ITEM.getType(), 0, item.getSlot(), 96534533));
                    Execution.delayUntil(30000, () -> Interfaces.isOpen(1251));
                    return;
                } else {
                    log("Item is null or ActionBar does not contain 'Disassemble' ability.");
                }
            } else {
                log("Backpack does not contain the item: " + item.getName());
            }
        }
        if (Backpack.isEmpty()) {
            log("Backpack is empty. Shutting down...");
            shutdown();
        }
    }
}
