package net.Snow.Smelting;

import net.Snow.AbstractScript;
import net.Snow.ImGui.SnowsTemplateGraphics;
import net.Snow.Snow;
import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.rs3.events.impl.ChatMessageEvent;
import net.botwithus.rs3.events.impl.InventoryUpdateEvent;
import net.botwithus.rs3.game.hud.interfaces.Interfaces;
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery;
import net.botwithus.rs3.game.queries.results.EntityResultSet;
import net.botwithus.rs3.game.scene.entities.object.SceneObject;
import net.botwithus.rs3.game.skills.Skills;
import net.botwithus.rs3.script.Execution;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import static net.Snow.ImGui.CustomLogger.logSuccess;
import static net.Snow.ImGui.Experience.inventoryMap;
import static net.Snow.ImGui.Experience.startTracking;
import static net.Snow.Snow.component;

public class Smelting implements AbstractScript {

    Snow script;

    @Override
    public void initialize() {
        startingSmithingXP.set(Skills.SMITHING.getSkill().getExperience());
        startingSmithingLevel.set(Skills.SMITHING.getSkill().getActualLevel());
        startTracking();

    }

    public AtomicInteger startingSmithingXP = new AtomicInteger(0);
    public AtomicInteger startingSmithingLevel = new AtomicInteger(0);


    @Override
    public void draw(SnowsTemplateGraphics sgc) throws IOException {
        SmeltingGraphics.draw(sgc, script, startingSmithingLevel, startingSmithingXP);
    }

    @Override
    public void onInventoryUpdate(InventoryUpdateEvent event) {
        SmeltingUpdates.onInventoryUpdate(event, inventoryMap);
    }

    @Override
    public void onChatMessageEvent(ChatMessageEvent event) {
        SmeltingUpdates.onChatMessageEvent(event, inventoryMap);
    }

    private static final EntityResultSet<SceneObject> bankChest = SceneObjectQuery.newQuery().name("Bank chest").option("Use").results();
    private static final EntityResultSet<SceneObject> furnace = SceneObjectQuery.newQuery().name("Furnace").option("Smelt").results();


    @Override
    public void run(Snow script) {
        // Ensure the selected option is not "Disabled"
        if (SelectedType.getSelectedSkill().equals("Disabled")) {
            return;
        }

        if (Interfaces.isOpen(1251)) {
            return;
        }

        switch (SelectedType.getSelectedSkill()) {
            case "Enchanted gem":
                handleEnchantedGem();
                break;
            case "Bars":
                handleSmelting();
                break;
            case "Jewellery":
                handleJewellery();
                break;
            default:
                break;
        }
    }

    private static void handleEnchantedGem() {
        if (Backpack.contains(4155)) {
            SceneObject smelter = furnace.nearest();
            if (smelter != null) {
                smelter.interact("Smelt");
                Execution.delayUntil(30000, () -> Interfaces.isOpen(37));
                if (Interfaces.isOpen(37)) {
                    //[Original]: DoAction(COMPONENT, 1, -1, 2424995)
                    component(1, -1, 2424995);
                    Execution.delayUntil(30000, () -> Interfaces.isOpen(1251));
                }
            }
        } else {
            SceneObject bank = bankChest.nearest();
            if (bank != null) {
                bank.interact("Load Last Preset from");
                Execution.delayUntil(30000, () -> Backpack.contains(4155));
            }
        }
    }

    private static void handleSmelting() {
        SceneObject smelter = furnace.nearest();
        if (smelter != null) {
            if (Backpack.isFull()) {
                smelter.interact("Deposit-all (into metal bank)");
                logSuccess("Depositing all items into metal bank.");
                Execution.delayUntil(30000, () -> !Backpack.isFull());
            } else {
                smelter.interact("Smelt");
                Execution.delayUntil(30000, () -> Interfaces.isOpen(37));
                if (Interfaces.isOpen(37)) {
                    //[Original]: DoAction(COMPONENT, 1, -1, 2424995)
                    logSuccess("Smelting bars.");
                    component(1, -1, 2424995);
                    Execution.delayUntil(30000, () -> Interfaces.isOpen(1251));
                }
            }
        }
    }

    private static void handleJewellery() {
        if (Backpack.containsItemByCategory(5290)) {
            SceneObject smelter = furnace.nearest();
            if (smelter != null) {
                smelter.interact("Smelt");
                Execution.delayUntil(30000, () -> Interfaces.isOpen(37));
                if (Interfaces.isOpen(37)) {
                    //[Original]: DoAction(COMPONENT, 1, -1, 2424995)
                    component(1, -1, 2424995);
                    Execution.delayUntil(30000, () -> Interfaces.isOpen(1251));
                }
            }
        } else {
            SceneObject bank = bankChest.nearest();
            if (bank != null) {
                bank.interact("Load Last Preset from");
                Execution.delayUntil(30000, () -> Backpack.containsItemByCategory(5290));
            }
        }
    }


}
