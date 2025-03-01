package net.Snow.Runecrafting;

import net.Snow.AbyssRunecrafting.AbyssRunecraftingGraphics;
import net.Snow.ImGui.SnowsTemplateGraphics;
import net.Snow.Snow;
import net.botwithus.rs3.game.login.LoginManager;
import net.botwithus.rs3.game.skills.Skills;
import net.botwithus.rs3.imgui.ImGui;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static net.Snow.Combat.Combat.player;
import static net.Snow.ImGui.CustomLogger.showTooltip;
import static net.Snow.ImGui.Experience.displaySkillStats;
import static net.Snow.ImGui.ImGuiCol.Text;
import static net.Snow.ImGui.ImGuiCol.setStyleColor;
import static net.Snow.ImGui.LogsInterface.createCenteredButton;
import static net.Snow.LevelLogout.CheckLevel.checkThreshold;
import static net.Snow.LevelLogout.CheckLevel.skillThresholdGraphics;
import static net.Snow.Runecrafting.AutoChange.*;
import static net.Snow.Runecrafting.Variables.*;

public class RunecraftingGraphics {

    public static boolean fleshAltar = false;
    public static boolean spiritAltar = false;
    public static boolean miasmaAltar = false;
    public static boolean boneAltar = false;
    public static boolean autoLevelling = false;
    public static boolean soulAltar = false;

    public static void draw(SnowsTemplateGraphics sgc, Snow script, AtomicInteger startingRunecraftingLevel, AtomicInteger startingRunecraftingXP, Map<String, Integer> inventoryMap, Map<String, Integer> itemPrices, boolean fleshAltar, boolean miasmaAltar, boolean spiritAltar, boolean boneAltar, long scriptStartTime) {
        booleanOptions();
        skillThresholdGraphics(Skills.RUNECRAFTING.getSkill());
        ImGui.Text("Botstate: " + Runecrafting.getBotState());
        setStyleColor(Text, 255, 0, 0, 255); // Set text color to white
        ImGui.Text("Name: " + player.getName() + " | World: " + LoginManager.getWorld());
        ImGui.PopStyleColor(1);
        displaySkillStats(Skills.RUNECRAFTING, startingRunecraftingLevel.get(), startingRunecraftingXP.get(), inventoryMap);
        ImGui.SeparatorText("Runs Achieved: " + loopCounter + " (" + getRunsPerHour() + " per hour)");

        int totalValue = 0; // Initialize the total value of all runes

        // Calculate and display the value of each rune type based on the altar type and inventory
        if (fleshAltar) {
            totalValue = displayRuneValue("Flesh rune", inventoryMap, itemPrices, totalValue, scriptStartTime);
        }
        if (miasmaAltar) {
            totalValue = displayRuneValue("Miasma rune", inventoryMap, itemPrices, totalValue, scriptStartTime);
        }
        if (spiritAltar) {
            totalValue = displayRuneValue("Spirit rune", inventoryMap, itemPrices, totalValue, scriptStartTime);
        }
        if (boneAltar) {
            totalValue = displayRuneValue("Bone rune", inventoryMap, itemPrices, totalValue, scriptStartTime);
        }

        // Calculate approximate hourly value
        double hoursElapsed = (System.currentTimeMillis() - scriptStartTime) / 3600000.0;
        double valuePerHour = (hoursElapsed > 0) ? (totalValue / hoursElapsed) : 0;

        // Set text color based on value per hour
        setValueColor(valuePerHour);

        ImGui.Text("Approximate hourly value: " + formatPrice((int) valuePerHour) + " coins/hour");

        ImGui.Separator();
        ImGui.PopStyleColor(); // Reset the color style to default

        if (autoChange) {
            Duration timeUntilChange = Duration.ofMinutes(nextChangeDuration).minus(Duration.between(lastChangeTime, Instant.now()));
            ImGui.Text("Time until next rune change: " + timeUntilChange.toMinutes() + " minutes. Next rune type: " + nextRuneType);
        }

        if (worldHop) {
            Duration timeUntilNextHop = WorldHop.getTimeUntilNextHop();
            ImGui.Text("Time until next world hop: " + timeUntilNextHop.toMinutes() + " minutes");
        }
    }

    private static int displayRuneValue(String runeName, Map<String, Integer> inventoryMap, Map<String, Integer> itemPrices, int totalValue, long scriptStartTime) {
        int runeAmount = inventoryMap.getOrDefault(runeName, 0);
        double hoursElapsed = (System.currentTimeMillis() - scriptStartTime) / 3600000.0;
        int runesPerHour = (hoursElapsed > 0) ? (int) (runeAmount / hoursElapsed) : 0; // Calculate runes per hour

        if (itemPrices.containsKey(runeName)) {
            int runePrice = itemPrices.get(runeName);
            int runeValue = runeAmount * runePrice;

            // Improved display formatting with runes per hour
            ImGui.SeparatorText(runeName + " Price Guide:");
            ImGui.Text("Cost per " + runeName + ": " + formatPrice(runePrice) + " coins");
            ImGui.Text(runeName + " amount: " + runeAmount + " (" + runesPerHour + " per hour)"); // Display rune amount with per hour rate
            ImGui.Text("Total " + runeName + " value: " + formatPrice(runeValue) + " coins");
            ImGui.Text("Total runes and total value: " + runeAmount + " | " + formatPrice(runeValue));

            totalValue += runeValue; // Accumulate the total value
        }
        return totalValue; // Return the updated total value
    }

    // Helper method to format prices in a shorter format with decimal points
    private static String formatPrice(int price) {
        if (price >= 1_000_000) {
            return String.format("%.1fM", price / 1_000_000.0); // Format as millions with one decimal
        } else if (price >= 1_000) {
            return String.format("%.1fk", price / 1_000.0); // Format as thousands with one decimal
        } else {
            return String.valueOf(price);
        }
    }

    // Helper method to set text color based on value per hour
    private static void setValueColor(double valuePerHour) {
        if (valuePerHour < 15_000_000) { // Less than 15M
            setStyleColor(Text, 255, 0, 0, 255); // Red color
        } else if (valuePerHour < 25_000_000) { // Between 15M and 25M
            setStyleColor(Text, 255, 165, 0, 255); // Orange color
        } else { // 25M and above
            setStyleColor(Text, 0, 128, 0, 255); // Green color
        }
    }

    private static void booleanOptions() {
        createCenteredButton("Flesh", () -> fleshAltar = !fleshAltar, fleshAltar, 138.0f);
        showTooltip("Crafts Flesh Runes.");

        ImGui.SameLine();

        createCenteredButton("Spirit", () -> spiritAltar = !spiritAltar, spiritAltar, 138.0f);
        showTooltip("Crafts Spirit Runes.");


        createCenteredButton("Miasma", () -> miasmaAltar = !miasmaAltar, miasmaAltar, 138.0f);
        showTooltip("Crafts Miasma Runes.");

        ImGui.SameLine();

        createCenteredButton("Bone", () -> boneAltar = !boneAltar, boneAltar, 138.0f);
        showTooltip("Crafts Bone Runes.");

        createCenteredButton("Auto Levelling", () -> autoLevelling = !autoLevelling, autoLevelling, 138.0f);
        showTooltip("Automatically crafts runes to level up.");

        ImGui.SameLine();

        createCenteredButton("Auto Change", () -> autoChange = !autoChange, autoChange, 138.0f);
        showTooltip("Automatically change Rune type every few hours");

        createCenteredButton("World hop", () -> worldHop = !worldHop, worldHop, 138.0f);
        showTooltip("Automatically world hop when a player is nearby, can only world hop every 5-10 minutes");

        ImGui.SameLine();

        createCenteredButton("Threshold", () -> checkThreshold = !checkThreshold, checkThreshold, 138.0f);
        showTooltip("Enable or disable the Level to Shutdown once level has been reached.");

        createCenteredButton("Familiar", () -> AbyssRunecraftingGraphics.useFamiliar = !AbyssRunecraftingGraphics.useFamiliar, AbyssRunecraftingGraphics.useFamiliar, 138.0f);
        showTooltip("Enable or disable the use of a familiar.");

        ImGui.SameLine();

        createCenteredButton("Soul", () -> soulAltar = !soulAltar, soulAltar, 138.0f);
        showTooltip("Crafts Soul Runes.");
    }
}
