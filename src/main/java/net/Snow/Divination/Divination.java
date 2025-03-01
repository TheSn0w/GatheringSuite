package net.Snow.Divination;

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
import net.botwithus.rs3.game.login.LoginManager;
import net.botwithus.rs3.game.login.World;
import net.botwithus.rs3.game.minimenu.actions.NPCAction;
import net.botwithus.rs3.game.movement.Movement;
import net.botwithus.rs3.game.queries.builders.characters.NpcQuery;
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery;
import net.botwithus.rs3.game.queries.builders.worlds.WorldQuery;
import net.botwithus.rs3.game.queries.results.EntityResultSet;
import net.botwithus.rs3.game.scene.entities.characters.npc.Npc;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.game.scene.entities.object.SceneObject;
import net.botwithus.rs3.game.skills.Skills;
import net.botwithus.rs3.game.vars.VarManager;
import net.botwithus.rs3.script.Execution;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static net.Snow.Divination.DivinationGraphics.*;
import static net.Snow.Divination.DivineOMatic.useDivineoMatic;
import static net.Snow.Divination.PriceChecker.energyPrice;
import static net.Snow.ImGui.CustomLogger.*;
import static net.Snow.ImGui.Experience.startTracking;
import static net.Snow.ImGui.SnowsTemplateGraphics.scriptStartTime;
import static net.Snow.LevelLogout.CheckLevel.checkLevelForSkill;
import static net.Snow.Mining.Familiar.useFamiliar;
import static net.Snow.Navigation.MoveTo;
import static net.Snow.Snow.*;

public class Divination implements AbstractScript {

    Snow script;

    LocalPlayer player = Client.getLocalPlayer();


    @Override
    public void initialize() {
        startingDivinationXP.set(Skills.DIVINATION.getSkill().getExperience());
        startingDivinationLevel.set(Skills.DIVINATION.getSkill().getLevel());
        startingHunterXP.set(Skills.HUNTER.getSkill().getExperience());
        startingHunterLevel.set(Skills.HUNTER.getSkill().getLevel());
        startTracking();

    }

    public AtomicInteger startingDivinationXP = new AtomicInteger(0);
    public AtomicInteger startingDivinationLevel = new AtomicInteger(0);
    public AtomicInteger startingHunterXP = new AtomicInteger(0);
    public AtomicInteger startingHunterLevel = new AtomicInteger(0);

    private final Map<String, Integer> itemPrices = new ConcurrentHashMap<>(); // The item prices map
    private final Map<String, Integer> inventoryMap = new ConcurrentHashMap<>(); // The inventory map


    @Override
    public void draw(SnowsTemplateGraphics sgc) throws IOException {
        DivinationGraphics.draw(sgc, script, startingDivinationLevel, startingDivinationXP, startingHunterLevel, startingHunterXP, inventoryMap, itemPrices, scriptStartTime, harvestChronicles);
    }

    @Override
    public void onInventoryUpdate(InventoryUpdateEvent event) {
        DivinationUpdates.onInventoryUpdate(event, inventoryMap);
    }

    @Override
    public void onChatMessageEvent(ChatMessageEvent event) {
        DivinationUpdates.onChatMessageEvent(event, inventoryMap, itemPrices, scriptStartTime);
    }

    @Override
    public int getLoopDelay() {
        return random.nextInt(50, 200);
    }

    public static final Area INCANDESCENT_WISP = new Area.Rectangular(
            new Coordinate(2269, 3039, 0), // Bottom-left coordinate of the area
            new Coordinate(2291, 3065, 0)  // Top-right coordinate of the area
    );

    public enum AccountType {
        IRONMAN,
        HARDCORE,
        REGULAR,
        HARDIRON
    }

    public static boolean checkAccountType(AccountType type) {
        int ironman = VarManager.getVarbitValue(20806);
        int hardcore = VarManager.getVarbitValue(20807);

        switch (type) {
            case IRONMAN:
                return ironman == 1 && hardcore == 0;
            case HARDCORE:
                return hardcore == 1;
            case REGULAR:
                return ironman == 0 && hardcore == 0;
            case HARDIRON:
                return ironman == 1 && hardcore == 1;
        }
        return false;
    }

    private volatile boolean isPlayerAnimating = false;
    private static boolean hasChecked = false;
    public static boolean hasDowser = false;

    public void run(Snow script) {

        checkLevelForSkill(Skills.DIVINATION.getSkill());


        if (hasDowser) {
            SceneObject nearestRift = findNearestRift();
            if (nearestRift != null) {
                Coordinate playerCoordinate = player.getCoordinate();
                Coordinate riftCoordinate = nearestRift.getCoordinate();

//                logLime("Rift is at: " + riftCoordinate);

                int tileDistance = Math.max(
                        Math.abs(riftCoordinate.getX() - playerCoordinate.getX()),
                        Math.abs(riftCoordinate.getY() - playerCoordinate.getY())
                );

//                logLime("Tiles between player and rift: " + tileDistance);

                if (tileDistance > 3) {
                    logMagenta("Player is not within 3 tiles of the rift. Moving to the rift.");
                    Movement.walkTo(2282, 3050, true);
                    Execution.delayUntil(15000, () -> player.getCoordinate().distanceTo(nearestRift) <= 3);
                    return;
                }
            } else {
                logError("No rift found.");
            }
        }



        if (player.isMoving()) {
            return;
        }

        if (!hasChecked && Backpack.contains("Incandescent energy")) {
            energyPrice();
            hasChecked = true;
        }

        capturestuff();

        // Start a virtual thread for monitoring player animation
        Thread.ofVirtual().start(() -> {
            while (script.isActive()) {
                isPlayerAnimating = isAnimating();
                Execution.delay(1000);
            }
        });

        // Main loop logic
        if (!INCANDESCENT_WISP.contains(player.getCoordinate())) {
            logBlue("Player is not in the incandescent wisp area.");
            MoveTo(INCANDESCENT_WISP.getRandomWalkableCoordinate());
        }


        if (useFamiliar) {
            Familiar.checkAndPerformActions(player);
        }

        if (useDivineoMatic) {
            DivineOMatic.divineoMatic();
        }

//        // Log the player's animation status
//        if (player.getAnimationId() == -1) {
//            if (isAnimationStoppedForLong() || Interfaces.isOpen(1189)) {
//                hopworldsDivinationAny();
//                Execution.delay(15000);
//                hopworldsDivination79();
//                resetAnimationTimer();
//            }
//        } else {
//            resetAnimationTimer();
//        }

        if (harvestChronicles) {
            List<Integer> npcTypeIds = new ArrayList<>();
            if (checkAccountType(AccountType.REGULAR)) {
                npcTypeIds.add(18205);
                npcTypeIds.add(18204);
            } else {
                npcTypeIds.add(18204);
            }
            if (depositChronicles) {
                int chronicleCount = Backpack.getQuantity("Chronicle fragment");

                // Check if the current chronicle count is greater than 25
                if (chronicleCount > 25) {
                    logBlue("Backpack contains " + chronicleCount + " Chronicle fragments, which is more than 25. Empowering at the nearest rift.");
                    empowerChroniclesAtRift();
                }
            }

            for (Integer npcTypeId : npcTypeIds) {
                EntityResultSet<Npc> chronicles = NpcQuery.newQuery().byParentType(npcTypeId).results();
                if (!chronicles.isEmpty()) {
                    interactWithChronicle(chronicles);
                    return;
                }
            }

        }

        if (onlyFarmChronicles) {
            return; // Exit if only farming chronicles
        }

        if (Interfaces.isOpen(1186)) {
            convertMemoriesAtRift();
        }

        if (isPlayerAnimating) {
            logGrey("Player is animating, waiting for the animation to finish.");
            return;
        }

        // Handle full backpack - priority on converting memories
        if (Backpack.isFull()) {
            convertMemoriesAtRift();
        } else {
            handleEnrichedSpringHarvesting();
        }
    }


    public static void interactWithChronicle(EntityResultSet<Npc> chronicles) {
        Npc nearestChronicle = chronicles.random();
        log("Interacted with Chronicle: " + nearestChronicle.interact("Capture"));
        Execution.delayUntil(random.nextLong(10000, 15000), () -> !nearestChronicle.validate());

    }

    // To track the time the animation has been stopped
    private long animationStopTime = -1;

    // Check if the animation is stopped for more than 1 minute
    private boolean isAnimationStoppedForLong() {
        if (animationStopTime == -1) {
            animationStopTime = System.currentTimeMillis();
        } else {
            long elapsed = System.currentTimeMillis() - animationStopTime;
            long secondsElapsed = elapsed / 1000;
            logGrey("Animation has been -1 for " + secondsElapsed + " seconds.");
        }
        return System.currentTimeMillis() - animationStopTime > 600000; // 1 minute in milliseconds
    }

    // Reset the timer when the animation is no longer -1
    private void resetAnimationTimer() {
        if (animationStopTime != -1) {
            logBlue("Animation stop timer reset.");
        }
        animationStopTime = -1;
    }

    private static void hopworldsDivination79() {
        if (LoginManager.isLoginInProgress()) {
            return;
        }
        final WorldQuery worlds = WorldQuery.newQuery().activity("Community Divination Training").mark();
        for (World result : worlds.results()) {
            logOrange("World: " + result.getId() + " " + result.getActivity() + " " + result.getPopulation());
            LoginManager.hopWorld(Objects.requireNonNull(worlds.results().first()));
        }
    }

    private static void hopworldsDivinationAny() {
        if (LoginManager.isLoginInProgress()) {
            return;
        }
        final WorldQuery worlds = WorldQuery.newQuery().members().mark();
        for (World result : worlds.results()) {
            logOrange("World: " + result.getId() + " " + result.getActivity() + " " + result.getPopulation());
            LoginManager.hopWorld(result);
        }
    }

    private void convertMemoriesAtRift() {
        SceneObject nearestRift = findNearestRift();

        if (nearestRift == null) {
            logError("No nearby rift found to convert memories.");
        } else if (nearestRift.interact("Convert memories")) {
            logBlue("Interacting with rift to convert memories.");
            Execution.delayUntil(30000, () -> !Backpack.containsItemByCategory(3030) && !Backpack.containsItemByCategory(3031));
        }
    }

    private void empowerChroniclesAtRift() {
        SceneObject nearestRift = findNearestRift();

        if (nearestRift == null) {
            logError("No nearby rift found to empower chronicle fragments.");
        } else if (nearestRift.interact("Empower")) {
            logBlue("Interacting with rift to empower chronicle fragments.");
            Execution.delayUntil(10000, () -> !Backpack.contains("Chronicle fragment"));
        }
    }

    private static SceneObject findNearestRift() {
        int[] riftIds = {93489, 87306}; // IDs for rift objects

        for (int riftId : riftIds) {
            SceneObject nearestRift = SceneObjectQuery.newQuery().id(riftId).results().nearest();

            if (nearestRift != null) {
                return nearestRift;
            }
        }
        return null;
    }

//    private void handleEnrichedSpringHarvesting() {
//
//        EntityResultSet<SceneObject> results = SceneObjectQuery.newQuery().id(87306).option("Convert memories").results();
//
//        if (player.getServerCoordinate().distanceTo(new Coordinate(2281, 3047, 0)) > 3) {
//            MoveTo(new Coordinate(2280, 3052, 0));
//        }
//        EntityResultSet<Npc> enrichedSprings = NpcQuery.newQuery().byType(18195).results();
//        EntityResultSet<Npc> lesserSprings = NpcQuery.newQuery().byType(18194).results();
//
//        Npc enrichedSpring = enrichedSprings.nearest();
//        if (enrichedSpring != null) {
//            double distanceToEnrichedSpring = player.getCoordinate().distanceTo(enrichedSpring.getCoordinate());
//
//            if (distanceToEnrichedSpring > 3 || (distanceToEnrichedSpring <= 3 && player.getAnimationId() == -1)) {
//                logOrange("Interacting with enriched spring: " + enrichedSpring.getName());
//                enrichedSpring.interact(NPCAction.NPC1);
//            }

    /// /        } else {
    /// /            Npc lesserSpring = lesserSprings.nearest();
    /// /            if (lesserSpring != null) {
    /// /                double distanceToLesserSpring = player.getCoordinate().distanceTo(lesserSpring.getCoordinate());
    /// /
    /// /                if (distanceToLesserSpring > 3 || (distanceToLesserSpring <= 3 && player.getAnimationId() == -1) && player.getAnimationId() == -1 && !enrichedSpring.validate()) {
    /// /                    logOrange("Interacting with lesser spring: " + lesserSpring.getName());
    /// /                    lesserSpring.interact(NPCAction.NPC1);
    /// /                }
    /// /            }
//        }
//    }
    private void handleEnrichedSpringHarvesting() {
        EntityResultSet<SceneObject> results = SceneObjectQuery.newQuery().id(87306).option("Convert memories").results();
        EntityResultSet<Npc> enrichedSprings = NpcQuery.newQuery().byType(18195).results();
        Npc enrichedSpring = enrichedSprings.nearest();

        if (enrichedSpring != null) {
            Coordinate enrichedSpringCoordinate = enrichedSpring.getCoordinate();
            Coordinate targetCoordinate = new Coordinate(2281, 3047, 0);
            double distanceToTarget = player.getCoordinate().distanceTo(targetCoordinate);

            if (distanceToTarget > 3) {
                logOrange("Player is outside the 3-tile radius. Moving closer to the enriched spring.");
                MoveTo(getClosestPointWithinIntersection(enrichedSpringCoordinate, targetCoordinate, 3));
            } else if (player.getAnimationId() == -1) {
                logOrange("Interacting with enriched spring: " + enrichedSpring.getName());
                enrichedSpring.interact(NPCAction.NPC1);
            }
        }
    }

    private Coordinate getClosestPointWithinIntersection(Coordinate springCoordinate, Coordinate center, double radius) {
        Area.Circular springArea = new Area.Circular(springCoordinate, radius);
        Area.Circular centerArea = new Area.Circular(center, radius);

        List<Coordinate> intersectionPoints = new ArrayList<>();
        for (Coordinate coord : springArea.getCoordinates()) {
            if (centerArea.contains(coord)) {
                intersectionPoints.add(coord);
            }
        }

        return intersectionPoints.stream()
                .min(Comparator.comparingDouble(coord -> coord.distanceTo(player.getCoordinate())))
                .orElse(center);
    }
}