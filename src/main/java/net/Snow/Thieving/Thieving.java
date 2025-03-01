package net.Snow.Thieving;

import net.Snow.AbstractScript;
import net.Snow.Banking.BankLocations;
import net.Snow.ImGui.SnowsTemplateGraphics;
import net.Snow.Snow;
import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.rs3.events.impl.ChatMessageEvent;
import net.botwithus.rs3.events.impl.InventoryUpdateEvent;
import net.botwithus.rs3.game.Coordinate;
import net.botwithus.rs3.game.queries.builders.characters.NpcQuery;
import net.botwithus.rs3.game.queries.results.EntityResultSet;
import net.botwithus.rs3.game.scene.entities.characters.npc.Npc;
import net.botwithus.rs3.game.skills.Skills;
import net.botwithus.rs3.script.Execution;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import static net.Snow.Banking.Porters.checkPorters;
import static net.Snow.Banking.Porters.usePorters;
import static net.Snow.Combat.Combat.player;
import static net.Snow.ImGui.CustomLogger.*;
import static net.Snow.ImGui.Experience.inventoryMap;
import static net.Snow.ImGui.Experience.startTracking;
import static net.Snow.Navigation.MoveTo;
import static net.Snow.Snow.random;
import static net.Snow.Spells.CrystalMask.enableCrystalMask;
import static net.Snow.Spells.CrystalMask.useCrystalMask;
import static net.Snow.Spells.Lightform.applyLightForm;
import static net.Snow.Spells.Lightform.useLightForm;
import static net.Snow.Thieving.Food.eatFood;

public class Thieving implements AbstractScript {

    Snow script;

    @Override
    public void initialize() {
        startingThievingXP.set(Skills.THIEVING.getSkill().getExperience());
        startingThievingLevel.set(Skills.THIEVING.getSkill().getActualLevel());
        startTracking();

    }

    public AtomicInteger startingThievingXP = new AtomicInteger(0);
    public AtomicInteger startingThievingLevel = new AtomicInteger(0);


    @Override
    public void draw(SnowsTemplateGraphics sgc) throws IOException {
        ThievingGraphics.draw(sgc, script, startingThievingLevel, startingThievingXP);
    }

    @Override
    public void onInventoryUpdate(InventoryUpdateEvent event) {
        ThievingUpdates.onInventoryUpdate(event, inventoryMap);
    }

    @Override
    public void onChatMessageEvent(ChatMessageEvent event) {
        ThievingUpdates.onChatMessageEvent(event, inventoryMap);
    }

    @Override
    public int getLoopDelay() {
        return 100;
    }

    public static Coordinate DesertPheonixLocation = new Coordinate(3344, 3113, 0);
    public static Coordinate CruxLocation = new Coordinate(3311, 3304, 0);
    public static boolean Phoexnis = false;
    public static boolean Crux = false;


    @Override
    public void run(Snow script) {
        if (player.getAnimationId() == 424) {
            logError("player is stunned, waiting.");
            Execution.delay(random.nextLong(2000, 2500));
            return;
        }
        handleDesertPheonix();
    }


    private static void handleDesertPheonix() {

        if (!Snow.isAnimating()) {

            if (Backpack.isFull()) {
                logWarning("Backpack is full, moving to bank.");
                BankLocations.goToBankToLoadLastPresetfrom();
            }

            applyBuffs();

            eatFood(player);

            if (usePorters) {
                checkPorters();
            }

            EntityResultSet<Npc> results = NpcQuery.newQuery().id(23956).option("Pickpocket").results();
            Npc crux = results.nearest();
            logSuccess("Interacted with Crux: " + crux.interact("Pickpocket"));
        }
    }

    private static boolean isPhoenixSquawking() {
        return NpcQuery.newQuery().overheadText("Squawk!").results().isEmpty();
    }

    private static void handlePhoenixInteraction() {
        EntityResultSet<Npc> results = NpcQuery.newQuery().name("Desert Phoenix").option("Grab-feather").results();
        if (results.isEmpty()) {
            logError("Phoenix not found, moving to location.");
            MoveTo(DesertPheonixLocation);
        } else {
            applyBuffs();
            interactWithPhoenix(results);
        }
    }

    private static void applyBuffs() {
        if (useLightForm) {
            applyLightForm(player);
        }

        if (useCrystalMask) {
            enableCrystalMask();
        }
    }

    private static void interactWithPhoenix(EntityResultSet<Npc> results) {
        if (isPhoenixSquawking()) {
            Npc desert = results.nearest();
            if (player.getAnimationId() == -1) {
                logSuccess("Interacted with Phoenix: " + desert.interact("Grab-feather"));
            }
        } else {
            logBlue("Phoenix is squawking, waiting.");
            Execution.delay(random.nextLong(4500, 6000));
        }
    }

    private static boolean isCruxShouting() {
        return NpcQuery.newQuery().id(23961).overheadText("What do you think you're doing?").results().isEmpty();
    }

    private static void handleCrux() {
        EntityResultSet<Npc> results = NpcQuery.newQuery().id(23961).option("Pickpocket").results();
        if (results.isEmpty()) {
            logError("Crux not found, moving to location.");
            MoveTo(CruxLocation);
        } else {
            applyBuffs();
            interactWithCrux(results);
        }
    }

    private static void interactWithCrux(EntityResultSet<Npc> results) {
        if (player.getAnimationId() == 424) {
            Execution.delay(random.nextLong(4000, 4500));
            logError("player is stunned, waiting.");
        } else {
            Npc crux = results.nearest();
            logSuccess("Interacted with Crux: " + crux.interact("Pickpocket"));
            Execution.delayUntil(30000, () -> player.getAnimationId() == 424);

        }
    }
}
