package net.Snow.Combat;

import net.Snow.AbstractScript;
import net.Snow.Banking.BankLocations;
import net.Snow.Combat.Graphics.CombatGraphics;
import net.Snow.Combat.Health.PlayerHealth;
import net.Snow.Combat.Health.Potions;
import net.Snow.Combat.NPCLocation.CombatNavigation;
import net.Snow.Combat.NPCLocation.NPCType;
import net.Snow.ImGui.SnowsTemplateGraphics;
import net.Snow.Snow;
import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.rs3.events.impl.ChatMessageEvent;
import net.botwithus.rs3.events.impl.InventoryUpdateEvent;
import net.botwithus.rs3.game.Area;
import net.botwithus.rs3.game.Client;
import net.botwithus.rs3.game.Coordinate;
import net.botwithus.rs3.game.Item;
import net.botwithus.rs3.game.actionbar.ActionBar;
import net.botwithus.rs3.game.queries.builders.characters.NpcQuery;
import net.botwithus.rs3.game.queries.builders.items.InventoryItemQuery;
import net.botwithus.rs3.game.scene.entities.characters.npc.Npc;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.game.skills.Skills;
import net.botwithus.rs3.game.vars.VarManager;
import net.botwithus.rs3.script.Execution;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

import static net.Snow.Combat.Abilities.manageCombatAbilities;
import static net.Snow.Combat.Graphics.NPCScanGraphics.selectedNpcNames;
import static net.Snow.Combat.Health.Scrimshaws.manageScripturesAndScrimshaws;
import static net.Snow.Combat.Looting.LootManager.manageLoot;
import static net.Snow.Combat.NPCLocation.CombatNavigation.MoveToRandom;
import static net.Snow.Combat.NPCScanner.updateNpcTableData;
import static net.Snow.ImGui.CustomLogger.*;
import static net.Snow.ImGui.Experience.inventoryMap;
import static net.Snow.ImGui.Experience.startTracking;
import static net.Snow.Snow.random;

public class Combat implements AbstractScript {


    private Snow script;
    public static LocalPlayer player = Client.getLocalPlayer();

    @Override
    public void initialize() {
        startingNecromancyXP.set(Skills.SLAYER.getSkill().getExperience());
        startingNecromancyLevel.set(Skills.SLAYER.getSkill().getLevel());
        startTracking();
    }

    public AtomicInteger startingNecromancyXP = new AtomicInteger(0);
    public AtomicInteger startingNecromancyLevel = new AtomicInteger(0);

    @Override
    public void draw(SnowsTemplateGraphics sgc) throws IOException {
        CombatGraphics.draw(sgc, script, startingNecromancyLevel, startingNecromancyXP);
    }

    @Override
    public void onInventoryUpdate(InventoryUpdateEvent event) {
        CombatUpdates.onInventoryUpdate(event, inventoryMap);
    }

    @Override
    public void onChatMessageEvent(ChatMessageEvent event) {
        CombatUpdates.onChatMessageEvent(event, inventoryMap);
    }

    @Override
    public int getLoopDelay() {
        return 100;
    }

    public static String scrimshawStatus = "No scrimshaw status available.";

    public static boolean useBank = false;


    public void run(Snow script) {
        if (handleFullBackpack() && useBank) {
            return;
        }

        updateNpcTableData(player);

        if (!checkAndUsePotions()) {
            return;
        }

        manageScripturesAndScrimshaws(player);
        PlayerHealth.eatFood(player);
        manageLoot();
        manageCombatAbilities();
        handleCombat(player);

        scrimshawStatus = getScrimshawStatus();
    }

    private boolean handleFullBackpack() {
        if (Backpack.isFull()) {
            if (ActionBar.containsAbility("Max guild Teleport")) {
                ActionBar.useAbility("Max guild Teleport");
                Execution.delay(random.nextLong(4000, 5000));
            }
            BankLocations.goToBankToLoadLastPresetfrom();
            return true;
        }
        return false;
    }

    private boolean checkAndUsePotions() {
        return Potions.drinkOverloads(player) &&
               Potions.useAggression(player) &&
               Potions.usePrayerOrRestorePots(player) &&
               Potions.useWeaponPoison(player);
    }

   public static String getScrimshawStatus() {
        Pattern itemPattern = Pattern.compile("scrimshaw|scripture of jas|scripture of wen", Pattern.CASE_INSENSITIVE);
        Item item = InventoryItemQuery.newQuery(94).name(itemPattern).results().first();

        if (item == null) {
            return "No scrimshaw or scripture in pocket slot.";
        }

        boolean isActive = VarManager.getInvVarbit(item.getInventoryType().getId(), item.getSlot(), 17232) == 1;
        int ticksRemaining = VarManager.getVarbitValue(30604);
        int timeRemaining = (int) (ticksRemaining * 0.6); // Convert ticks to seconds

        // Convert timeRemaining to hours, minutes, and seconds
        int hours = timeRemaining / 3600;
        int minutes = (timeRemaining % 3600) / 60;
        int seconds = timeRemaining % 60;

        return String.format("%s: %s (%s) - Time remaining: %dh %dm %ds",
                item.getName(),
                item.getName(),
                isActive ? "Active" : "Non-Active",
                hours, minutes, seconds);
    }

    public static void handleCombat(LocalPlayer player) {
        CombatNavigation selectedNpc = NPCType.NpcTypes.get(NPCType.selectedNpcIndex);

        Npc monster = findNearestMonster(selectedNpc, player);

        if (monster == null) {
            logGrey("Selected NPC name " + String.join(", ", selectedNpcNames + " not found."));
            return;
        }

        if (!player.hasTarget() && !player.isMoving()) {
            logGrey("NPC is reachable and player has no target. Attempting to attack.");
            boolean attack = monster.interact("Attack");
            if (attack) {
                logSuccess("Successfully attacked: " + monster.getName());
                Execution.delay(random.nextLong(600, 750));
            } else {
                logError("Failed to interact with NPC: " + monster.getName());
            }
        } else {
            if (player.hasTarget()) {
                logGrey("Player already has a target.");
            }
        }
    }

    private static Npc findNearestMonster(CombatNavigation selectedNpc, LocalPlayer player) {
        if (selectedNpcNames.isEmpty()) {
            logError("No valid target selected inside nearestmonster.");
            return null;
        }

        Pattern monsterPattern = generateRegexPattern(selectedNpcNames);

        Npc nearestNpc = NpcQuery.newQuery()
                .name(monsterPattern)
                .health(1, 1_000_000)
                .option("Attack")
                .isReachable()
                .results()
                .stream()
                .min(Comparator.comparingDouble(npc -> npc.getCoordinate().distanceTo(player.getCoordinate())))
                .orElse(null);

        return nearestNpc;
    }

    public static Pattern generateRegexPattern(List<String> names) {
        return Pattern.compile(
                names.stream()
                        .map(Pattern::quote)
                        .reduce((name1, name2) -> name1 + "|" + name2)
                        .orElse(""),
                Pattern.CASE_INSENSITIVE
        );
    }

    private static void moveToNpcLocation(CombatNavigation selectedNpc) {
        if (selectedNpc.getCoordinate() != null) {
            boolean moved = MoveToRandom(selectedNpc.getCoordinate());
            if (moved) {
                logOrange("Moved to " + selectedNpc.getName() + " location at " + selectedNpc.getCoordinate());
            } else {
                logError("Failed to move to " + selectedNpc.getName() + " location.");
            }
        }
    }

    private static boolean isNpcReachable(Npc npc, Coordinate playerCoordinate) {
        if (npc.getCoordinate().distanceTo(playerCoordinate) <= 25) {
            logGrey("NPC within 25 tiles found at: " + npc.getCoordinate());
            Set<Coordinate> surroundingTiles = getSurroundingTiles(npc);
            for (Coordinate tile : surroundingTiles) {
                if (tile.isReachable()) {
                    logGrey("Reachable tile found at: " + tile);
                    return true;
                }
            }
        }
        return false;
    }

    public static Set<Coordinate> getSurroundingTiles(Npc npc) {
        logGrey("Calculating surrounding tiles for NPC: " + npc.getName());
        Area area;
        if (npc == null || (area = npc.getArea()) == null) {
            logGrey("NPC or area is null, returning empty set.");
            return Collections.emptySet();
        }

        Set<Coordinate> result = new HashSet<>();
        for (Coordinate tile : area.getCoordinates()) {
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    result.add(tile.derive(i, j, 0));
                }
            }
        }
        return result;
    }
}