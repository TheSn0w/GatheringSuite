package net.Snow.Rituals;

import net.Snow.AbstractScript;
import net.Snow.ImGui.SnowsTemplateGraphics;
import net.Snow.Snow;
import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.rs3.events.impl.ChatMessageEvent;
import net.botwithus.rs3.events.impl.InventoryUpdateEvent;
import net.botwithus.rs3.game.Area;
import net.botwithus.rs3.game.Client;
import net.botwithus.rs3.game.Coordinate;
import net.botwithus.rs3.game.hud.interfaces.Interfaces;
import net.botwithus.rs3.game.js5.types.vars.VarDomainType;
import net.botwithus.rs3.game.queries.builders.characters.NpcQuery;
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery;
import net.botwithus.rs3.game.queries.results.EntityResultSet;
import net.botwithus.rs3.game.scene.entities.characters.npc.Npc;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.game.scene.entities.object.SceneObject;
import net.botwithus.rs3.game.skills.Skills;
import net.botwithus.rs3.game.vars.VarManager;
import net.botwithus.rs3.script.Execution;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static net.Snow.ImGui.CustomLogger.*;
import static net.Snow.ImGui.Experience.inventoryMap;
import static net.Snow.ImGui.Experience.startTracking;
import static net.Snow.Navigation.MoveTo;
import static net.Snow.Rituals.Distrubances.handleDisturbances;
import static net.Snow.Rituals.NecromancyGraphics.*;
import static net.Snow.Rituals.Potions.drinkOverloads;
import static net.Snow.Rituals.Potions.necromancyPotion;
import static net.Snow.Snow.component;
import static net.Snow.Snow.random;

public class Necromancy implements AbstractScript {

    Snow script;

    @Override
    public void initialize() {
        startingNecromancyXP.set(Skills.NECROMANCY.getSkill().getExperience());
        startingNecromancyLevel.set(Skills.NECROMANCY.getSkill().getActualLevel());
        startTracking();
    }

    public AtomicInteger startingNecromancyXP = new AtomicInteger(0);
    public AtomicInteger startingNecromancyLevel = new AtomicInteger(0);

    @Override
    public void draw(SnowsTemplateGraphics sgc) {
        NecromancyGraphics.draw(sgc, script, startingNecromancyLevel, startingNecromancyXP);
    }

    @Override
    public void onInventoryUpdate(InventoryUpdateEvent event) {
        NecromancyUpdates.onInventoryUpdate(event, inventoryMap);
    }

    @Override
    public void onChatMessageEvent(ChatMessageEvent event) {
        NecromancyUpdates.onChatMessageEvent(event, inventoryMap);
    }

    public static final Area RITUAL_SITE = new Area.Rectangular(
            new Coordinate(1027, 1761, 1), // Bottom-left coordinate of the area
            new Coordinate(1050, 1785, 1)  // Top-right coordinate of the area
    );



    private static final Coordinate TARGET_COORDINATE = new Coordinate(1038, 1770, 1);

    @Override
    public void run(Snow script) {
        LocalPlayer player = Client.getLocalPlayer();

        if (!RITUAL_SITE.contains(player.getCoordinate())) {
            MoveTo(RITUAL_SITE.getRandomWalkableCoordinate());
        } else {
            if (Backpack.contains("Bucket of slime")) {
                EntityResultSet<SceneObject> focusStorage = SceneObjectQuery.newQuery().name("Focus storage").option("Store").results();
                if (!focusStorage.isEmpty()) {
                    SceneObject focusStorageObject = focusStorage.first();
                    if (focusStorageObject != null) {
                        logBlue("Interacting with Focus Storage SceneObject using option: Store");
                        focusStorageObject.interact("Store");
                        Execution.delayUntil(30000, () -> Interfaces.isOpen(998));
                    }
                    if (Interfaces.isOpen(998)) {
                        //[Original]: DoAction(COMPONENT, 1, 0, 65404942)
                        component(1, 0, 65404942);
                    }
                }
            }

            if (enableDisturbances && VarManager.getVarValue(VarDomainType.PLAYER, 10937) >= 5) {
                handleDisturbances();
            }

            if (player.isMoving() || player.getAnimationId() != -1) {
                Execution.delay(random.nextLong(600, 1000));
                return;
            }


            int ritualStatus = VarManager.getVarValue(VarDomainType.PLAYER, 10937);
            if (ritualStatus == 0) {


                checkDepletedNpcs(player);

                interactWithClosestSceneObject(player, "Start ritual", "Continue ritual");
            } else {
                if (player.getAnimationId() == -1) {
                    logGrey("Ritual Timer: " + ritualStatus + " seconds remaining.");
                    interactWithClosestSceneObject(player, "Start ritual", "Continue ritual");
                }
            }
        }
    }

    private void checkDepletedNpcs(LocalPlayer player) {
        EntityResultSet<Npc> allEntities = NpcQuery.newQuery().option("Repair").results();
        List<String> depletedNpcs = new ArrayList<>();

        for (Npc npc : allEntities) {
            if (npc.getName().contains("depleted")) {
                logGrey("Depleted NPC detected: " + npc.getName());
                depletedNpcs.add(npc.getName());
            }
        }

        if (!depletedNpcs.isEmpty()) {
            logBlue("Depleted Glyths: " + String.join(", ", depletedNpcs));
            logGrey("Interacting with SceneObject closest to (1038, 1776, 1) using option 'Repair all'...");

            if (useNecromancyPotion) necromancyPotion();
            if (useNecroOverloads) drinkOverloads(player);

            // Interact with the SceneObject closest to (1038, 1776, 1) with option "Repair all"
            Coordinate repairCoordinate = new Coordinate(1038, 1776, 1);
            EntityResultSet<SceneObject> nearbySceneObjects = SceneObjectQuery.newQuery().results();
            SceneObject closestObject = nearbySceneObjects.stream()
                    .min(Comparator.comparingDouble(obj -> obj.getCoordinate().distanceTo(repairCoordinate)))
                    .orElse(null);

            if (closestObject != null) {
                logBlue("Closest SceneObject found: " + closestObject.getName() + " at " + closestObject.getCoordinate());
                if (closestObject.getOptions().contains("Repair all")) {
                    logBlue("Interacting with SceneObject using option: Repair all");
                    closestObject.interact("Repair all");
                    Execution.delay(random.nextLong(1500, 3000));  // Delay for human-like behavior
                    Execution.delayUntil(random.nextLong(8000, 10000), () -> player.getAnimationId() != -1);  // Wait for animation to start
                } else {
                    logError("No Repair all option available for SceneObject: " + closestObject.getName());
                }
            } else {
                logError("No SceneObject found close to the specified coordinates.");
            }
        } else {
            logGrey("No depleted NPCs found.");
        }
    }


    private void interactWithClosestSceneObject(LocalPlayer player, String... options) {
        logGrey("Finding the closest SceneObject to " + TARGET_COORDINATE + "...");

        EntityResultSet<SceneObject> nearbySceneObjects = SceneObjectQuery.newQuery().results();
        SceneObject closestObject = nearbySceneObjects.stream()
                .min(Comparator.comparingDouble(obj -> obj.getCoordinate().distanceTo(TARGET_COORDINATE)))
                .orElse(null);

        if (closestObject != null) {
            logBlue("Closest SceneObject found: " + closestObject.getName() + " at " + closestObject.getCoordinate());

            List<String> availableOptions = closestObject.getOptions();
            if (availableOptions != null && !availableOptions.isEmpty()) {
                String firstOption = availableOptions.get(0);  // Get the first available option
                logBlue("Interacting with SceneObject using option: " + firstOption);
                closestObject.interact(firstOption);  // Interact using the first option
                Execution.delay(random.nextLong(1500, 3000));  // Delay for human-like behavior
                Execution.delayUntil(random.nextLong(8000, 10000), () -> player.getAnimationId() != -1);  // Wait for animation to start
            } else {
                logError("No available options to interact with for SceneObject: " + closestObject.getName());
            }
        } else {
            logError("No SceneObject found close to the specified coordinates.");
        }
    }
}


