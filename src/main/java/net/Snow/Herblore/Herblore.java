package net.Snow.Herblore;

import net.Snow.AbstractScript;
import net.Snow.ImGui.SnowsTemplateGraphics;
import net.Snow.Snow;
import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.rs3.events.impl.ChatMessageEvent;
import net.botwithus.rs3.events.impl.InventoryUpdateEvent;
import net.botwithus.rs3.game.Client;
import net.botwithus.rs3.game.hud.interfaces.Component;
import net.botwithus.rs3.game.hud.interfaces.Interfaces;
import net.botwithus.rs3.game.js5.types.vars.VarDomainType;
import net.botwithus.rs3.game.minimenu.MiniMenu;
import net.botwithus.rs3.game.queries.builders.characters.NpcQuery;
import net.botwithus.rs3.game.queries.builders.components.ComponentQuery;
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery;
import net.botwithus.rs3.game.scene.entities.characters.npc.Npc;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.game.scene.entities.object.SceneObject;
import net.botwithus.rs3.game.skills.Skills;
import net.botwithus.rs3.game.vars.VarManager;
import net.botwithus.rs3.script.Execution;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import static net.Snow.ImGui.CustomLogger.*;
import static net.Snow.ImGui.Experience.inventoryMap;
import static net.Snow.ImGui.Experience.startTracking;
import static net.Snow.Snow.*;

public class Herblore implements AbstractScript {

    private Snow script;

    public AtomicInteger startingHerbloreXP = new AtomicInteger(0);
    public AtomicInteger startingHerbloreLevel = new AtomicInteger(0);


    @Override
    public void initialize() {
        startingHerbloreXP.set(Skills.HERBLORE.getSkill().getExperience());
        startingHerbloreLevel.set(Skills.HERBLORE.getSkill().getActualLevel());
        startTracking();
    }

    @Override
    public void draw(SnowsTemplateGraphics sgc) throws IOException {
        HerbloreGraphics.draw(sgc, script, startingHerbloreLevel, startingHerbloreXP);
    }

    @Override
    public void onInventoryUpdate(InventoryUpdateEvent event) {
        HerbloreUpdates.onInventoryUpdate(event, inventoryMap);
    }

    @Override
    public void onChatMessageEvent(ChatMessageEvent event) {
        HerbloreUpdates.onChatMessageEvent(event, inventoryMap);
    }

    @Override
    public int getLoopDelay() {
        return random.nextInt(200, 400);
    }


    @Override
    public void run(Snow script) {
        capturestuff();

        if (Interfaces.isOpen(1251) || (Interfaces.isOpen(1370) && selectInterface())) {
            return;
        }

        if (Backpack.isFull()) {
            handleMixing(Client.getLocalPlayer());
        } else {
            loadLastPreset();
        }
    }

    private static void handleMixing(LocalPlayer player) {
        SceneObject target = getClosestObject(player, "Portable well", "Botanist's workbench", "Mix Potions");

        if (target != null && target.distanceTo(player) < 7.0D) {
            logSuccess("Interacting with " + target.getName() + ": " + target.interact("Mix Potions"));
            Execution.delayUntil(30000, () -> Interfaces.isOpen(1370));
        } else {
            logError("Portable well or Botanist's workbench not found or interaction failed");
            Execution.delay(random.nextLong(10000, 20000));
        }
    }

    private static final double MAX_INTERACTION_DISTANCE = 7.0; // Maximum distance for interaction

    private static void loadLastPreset() {
        LocalPlayer player = Client.getLocalPlayer();
        HerbloreUpdates.setIsLoadingPreset(true);

        if (Backpack.isFull()) {
            handleMixing(player);
            return;
        }

        // Search for any scene object or NPC with the "Load Last Preset from" option
        SceneObject loadPresetObject = findSceneObjectWithOption("Load Last Preset from", player);
        if (loadPresetObject != null && loadPresetObject.distanceTo(player) <= MAX_INTERACTION_DISTANCE) {
            if (interactWithSceneObject(loadPresetObject, "Load Last Preset from")) return;
        }

        Npc loadPresetNpc = findNpcWithOption("Load Last Preset from", player);
        if (loadPresetNpc != null && loadPresetNpc.distanceTo(player) <= MAX_INTERACTION_DISTANCE) {
            if (interactWithNpc(loadPresetNpc, "Load Last Preset from")) return;
        }

        HerbloreUpdates.setIsLoadingPreset(false);
    }

    private static SceneObject findSceneObjectWithOption(String option, LocalPlayer player) {
        // Search for any scene object with the specified option
        return SceneObjectQuery.newQuery()
                .option(option)
                .results()
                .nearest();
    }

    private static Npc findNpcWithOption(String option, LocalPlayer player) {
        // Search for any NPC with the specified option
        return NpcQuery.newQuery()
                .option(option)
                .results()
                .nearest();
    }

    private static boolean interactWithSceneObject(SceneObject sceneObject, String action) {
        if (sceneObject != null && sceneObject.interact(action)) {
            logSuccess("Interacting with " + sceneObject.getName());
            Execution.delayUntil(30000, Backpack::isFull);
            return checkInventoryFull();
        }
        return false;
    }

    private static boolean interactWithNpc(Npc npc, String action) {
        if (npc != null && npc.interact(action)) {
            logSuccess("Interacting with NPC");
            Execution.delayUntil(30000, Backpack::isFull);
            return checkInventoryFull();
        }
        return false;
    }

    private static boolean checkInventoryFull() {
        if (!Backpack.isFull()) {
            logError("Backpack is not full, shutting down.");
            shutdown();
            return true;
        }
        return false;
    }

    private static boolean selectInterface() {
        Component craftingInterface = ComponentQuery.newQuery(1370).results().first();
        if (craftingInterface == null) {
            logError("Interface not found.");
            return false;
        }

        int resourcesCanMake = VarManager.getVarValue(VarDomainType.PLAYER, 8847);
        if (resourcesCanMake <= 0) {
            logBlue("Interacting with bank.");
            return false;
        }

        if (MiniMenu.interact(16, 0, -1, 89784350)) {
            logSuccess("Successfully interacted with Dialog!");
            Execution.delayUntil(30000, () -> Interfaces.isOpen(1251));
            return true;
        } else {
            logError("Failed to Interact with Dialog.");
            return false;
        }
    }



    private static SceneObject getClosestObject(LocalPlayer player, String name1, String name2, String action) {
        SceneObject obj1 = SceneObjectQuery.newQuery().name(name1).results().nearest();
        SceneObject obj2 = SceneObjectQuery.newQuery().name(name2).option(action).results().nearest();

        if (obj1 == null) return obj2;
        if (obj2 == null) return obj1;

        return obj1.distanceTo(player) < obj2.distanceTo(player) ? obj1 : obj2;
    }
}

