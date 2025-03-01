package net.Snow.Runecrafting;

import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.rs3.game.Client;
import net.botwithus.rs3.game.queries.builders.items.InventoryItemQuery;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.game.skills.Skills;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static net.Snow.ImGui.CustomLogger.logGrey;
import static net.Snow.Runecrafting.Runecrafting.setBotState;
import static net.Snow.Runecrafting.RunecraftingGraphics.*;
import static net.Snow.Runecrafting.Variables.*;

public class AutoChange {

    public static boolean autoChange = false;
    public static Instant lastChangeTime = Instant.now();
    public static final Random random = new Random();
    public static long nextChangeDuration = getRandomChangeDuration();
    public static String currentRuneType = "None";
    public static String nextRuneType = "None";

    public static long getRandomChangeDuration() {
        return 60 + random.nextInt(121); // Random duration between 60 and 180 minutes
    }

    public static boolean shouldChangeRunetype() {
        Duration elapsed = Duration.between(lastChangeTime, Instant.now());
        return elapsed.toMinutes() >= nextChangeDuration;
    }

    public static void updateChangeTime() {
        lastChangeTime = Instant.now();
        nextChangeDuration = getRandomChangeDuration();
    }

    public static void autoChange() {
        if (autoChange && shouldChangeRunetype()) {
            updateChangeTime();

            // Set the current rune type boolean to false before changing
            switch (currentRuneType) {
                case "Spirit":
                    spiritAltar = false;
                    break;
                case "Bone":
                    boneAltar = false;
                    break;
                case "Flesh":
                    fleshAltar = false;
                    break;
                case "Miasma":
                    miasmaAltar = false;
                    break;
            }

            int runecraftingLevel = Skills.RUNECRAFTING.getSkill().getLevel();
            List<String> availableRunes = new ArrayList<>();

            // Add runes to the available options based on the player's Runecrafting level
            if (runecraftingLevel >= 1 && !currentRuneType.equals("Spirit")) {
                availableRunes.add("Spirit");
            }
            if (runecraftingLevel >= 20 && !currentRuneType.equals("Bone")) {
                availableRunes.add("Bone");
            }
            if (runecraftingLevel >= 40 && !currentRuneType.equals("Flesh")) {
                availableRunes.add("Flesh");
            }
            if (runecraftingLevel >= 60 && !currentRuneType.equals("Miasma")) {
                availableRunes.add("Miasma");
            }

            // If there's no other option available (e.g., all possible options were filtered out), keep the current rune type
            if (availableRunes.isEmpty()) {
                availableRunes.add(currentRuneType);
            }

            // Randomly select the next rune type from the available options
            String selectedRune = availableRunes.get(random.nextInt(availableRunes.size()));

            // Set the selected rune type boolean to true and update the current and next rune types
            switch (selectedRune) {
                case "Spirit":
                    currentRuneType = "Spirit";
                    spiritAltar = true;
                    break;
                case "Bone":
                    currentRuneType = "Bone";
                    boneAltar = true;
                    break;
                case "Flesh":
                    currentRuneType = "Flesh";
                    fleshAltar = true;
                    break;
                case "Miasma":
                    currentRuneType = "Miasma";
                    miasmaAltar = true;
                    break;
            }

            // Update the next rune type
            nextRuneType = availableRunes.get(random.nextInt(availableRunes.size()));

            logGrey("Rune type changed to: " + currentRuneType + ". Next rune type: " + nextRuneType);
        }
    }

    public static void selectRune() {
        // Check if no rune type is currently selected
        if (!fleshAltar && !spiritAltar && !miasmaAltar && !boneAltar) {
            int runecraftingLevel = Skills.RUNECRAFTING.getSkill().getLevel();
            List<String> availableRunes = new ArrayList<>();

            // Add runes to the available options based on the player's Runecrafting level
            if (runecraftingLevel >= 1) {
                availableRunes.add("Spirit");
            }
            if (runecraftingLevel >= 20) {
                availableRunes.add("Bone");
            }
            if (runecraftingLevel >= 40) {
                availableRunes.add("Flesh");
            }
            if (runecraftingLevel >= 60) {
                availableRunes.add("Miasma");
            }

            // Randomly select a rune type from the available options
            String selectedRune = availableRunes.get(random.nextInt(availableRunes.size()));

            // Set the selected rune type boolean to true and update the current rune type
            switch (selectedRune) {
                case "Spirit":
                    spiritAltar = true;
                    currentRuneType = "Spirit";
                    break;
                case "Bone":
                    boneAltar = true;
                    currentRuneType = "Bone";
                    break;
                case "Flesh":
                    fleshAltar = true;
                    currentRuneType = "Flesh";
                    break;
                case "Miasma":
                    miasmaAltar = true;
                    currentRuneType = "Miasma";
                    break;
            }

            // Set the next rune type to "None" initially
            nextRuneType = "None";
            logGrey("Initial rune type selected: " + currentRuneType);
        }
    }

    public static void determineStart() {
        LocalPlayer player = Client.getLocalPlayer();
        // State handling based on player location and inventory
        if ((HAUNTED_HILL_AREA.contains(player) && Backpack.contains(55667))) {
            logGrey("Player is in HAUNTED_HILL_AREA or ALTAR_AREA with item 55667. Setting state to DARKPORTAL.");
            setBotState(Runestate.DARKPORTAL);
        } else if (HAUNTED_HILL_AREA.contains(player) && !Backpack.contains(55667)) {
            logGrey("Player is in HAUNTED_HILL_AREA without item 55667. Setting state to TELEPORTING.");
            setBotState(Runestate.TELEPORTING);
        } else if (ALTAR_AREA.contains(player) && Backpack.contains(55667)) {
            logGrey("Player is in ALTAR_AREA with item 55667. Setting state to ALTAR.");
            setBotState(Runestate.ALTAR);
        } else if (ALTAR_AREA.contains(player) && !Backpack.contains(55667)) {
            logGrey("Player is in ALTAR_AREA without item 55667. Setting state to TELEPORTING.");
            setBotState(Runestate.TELEPORTING);
        } else if (BANK_AREA.contains(player) && Backpack.contains(55667) && !(InventoryItemQuery.newQuery(94).name("Games", String::contains)).results().isEmpty()) {
            logGrey("Player is in BANK_AREA with item 55667. Setting state to TELEPORTING.");
            setBotState(Runestate.PASSINGBRACELET);
        } else if (BANK_AREA.contains(player) && !Backpack.contains(55667)) {
            logGrey("Player is in BANK_AREA without item 55667. Setting state to BANKING.");
            setBotState(Runestate.BANKING);
        }
    }

}
