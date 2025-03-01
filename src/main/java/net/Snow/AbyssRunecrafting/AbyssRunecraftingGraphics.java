package net.Snow.AbyssRunecrafting;

import net.Snow.ImGui.SnowsTemplateGraphics;
import net.Snow.Runecrafting.Runecrafting;
import net.Snow.Runecrafting.WorldHop;
import net.Snow.Snow;
import net.botwithus.rs3.game.skills.Skills;
import net.botwithus.rs3.imgui.ImGui;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static net.Snow.AbyssRunecrafting.Variables.*;
import static net.Snow.ImGui.CustomLogger.showTooltip;
import static net.Snow.ImGui.Experience.displaySkillStats;
import static net.Snow.ImGui.ImGuiCol.Text;
import static net.Snow.ImGui.ImGuiCol.setStyleColor;
import static net.Snow.ImGui.LogsInterface.createCenteredButton;
import static net.Snow.ImGui.SnowsTemplateGraphics.scriptStartTime;
import static net.Snow.LevelLogout.CheckLevel.checkThreshold;
import static net.Snow.LevelLogout.CheckLevel.skillThresholdGraphics;
import static net.Snow.Runecrafting.Variables.*;

public class AbyssRunecraftingGraphics {

    public static boolean useFamiliar = false;


    public static void draw(SnowsTemplateGraphics sgc, Snow script, AtomicInteger startingRunecraftingLevel, AtomicInteger startingRunecraftingXP, Map<String, Integer> inventoryMap, Map<String, Integer> itemPrices) {
        booleanOptions();
        skillThresholdGraphics(Skills.RUNECRAFTING.getSkill());
        ImGui.Text("Botstate: " + Runecrafting.getBotState());
        displaySkillStats(Skills.RUNECRAFTING, startingRunecraftingLevel.get(), startingRunecraftingXP.get(), inventoryMap);

        ImGui.SeparatorText("Runs Achieved: " + loopCounter + " (" + getRunsPerHour() + " per hour)");

        if (worldHop) {
            Duration timeUntilNextHop = WorldHop.getTimeUntilNextHop();
            ImGui.Text("Time until next world hop: " + timeUntilNextHop.toMinutes() + " minutes");
        }

        // Display total item values for "Nature rune" and "Magical thread"
        ImGui.Separator();
        ImGui.Text("Inventory Value:");
        int totalValue = 0;

        // Display rune value based on the altar type
        String runeName = getAltarName().replace(" altar", " rune");
        int runeCount = inventoryMap.getOrDefault(runeName, 0);
        int runePrice = itemPrices.getOrDefault(runeName, 0);

        if (runeCount > 0) {
            int runeValue = runeCount * runePrice;
            totalValue += runeValue;

            // Display rune details
            ImGui.Text(runeName + ": " + runeCount + " x " + formatPrice(runePrice) + " = " + formatPrice(runeValue) + " coins");
        }

        // Display "Magical thread" value
        String magicalThread = "Magical thread";
        int magicalThreadCount = inventoryMap.getOrDefault(magicalThread, 0);
        int magicalThreadPrice = itemPrices.getOrDefault(magicalThread, 0);

        if (magicalThreadCount > 0) {
            int magicalThreadValue = magicalThreadCount * magicalThreadPrice;
            totalValue += magicalThreadValue;

            // Display "Magical thread" details
            ImGui.Text(magicalThread + ": " + magicalThreadCount + " x " + formatPrice(magicalThreadPrice) + " = " + formatPrice(magicalThreadValue) + " coins");
        }

        // Display total value
        ImGui.Separator();
        ImGui.Text("Total Value: " + formatPrice(totalValue) + " coins");

        // Approximate hourly value calculation
        double hoursElapsed = (System.currentTimeMillis() - scriptStartTime) / 3600000.0;
        double valuePerHour = (hoursElapsed > 0) ? (totalValue / hoursElapsed) : 0;
        setValueColor(valuePerHour);
        ImGui.Text("Approximate hourly value: " + formatPrice((int) valuePerHour) + " coins/hour");
        ImGui.PopStyleColor(); // Reset the color style to default
    }

    // Helper method to format prices in a shorter format
    private static String formatPrice(int price) {
        if (price >= 1_000_000) {
            return price / 1_000_000 + "M";
        } else if (price >= 1_000) {
            return price / 1_000 + "k";
        } else {
            return String.valueOf(price);
        }
    }

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
        createCenteredButton("World hop", () -> worldHop = !worldHop, worldHop, 138.0f);
        showTooltip("Automatically world hop when a player is nearby, can only world hop every 5-10 minutes");

        ImGui.SameLine();

        createCenteredButton("Threshold", () -> checkThreshold = !checkThreshold, checkThreshold, 138.0f);
        showTooltip("Enable or disable the Level to Shutdown once level has been reached.");

        createCenteredButton("Familiar", () -> useFamiliar = !useFamiliar, useFamiliar, 138.0f);
        showTooltip("Enable or disable the use of a familiar.");

        createCenteredButton("Craft Nature Runes", () -> craftNatureRunes = !craftNatureRunes, craftNatureRunes, 138.0f);
        showTooltip("Enable or disable the crafting of Nature Runes.");

        ImGui.SameLine();

//        createCenteredButton("Craft Blood Runes", () -> craftBloodRunes = !craftBloodRunes, craftBloodRunes, 138.0f);
//        showTooltip("Enable or disable the crafting of Blood Runes.");

        createCenteredButton("Craft Cosmic Runes", () -> craftCosmicRunes = !craftCosmicRunes, craftCosmicRunes, 138.0f);
        showTooltip("Enable or disable the crafting of Cosmic Runes.");


        createCenteredButton("Craft Fire Runes", () -> craftFireRunes = !craftFireRunes, craftFireRunes, 138.0f);
        showTooltip("Enable or disable the crafting of Fire Runes.");

        ImGui.SameLine();

        createCenteredButton("Craft Earth Runes", () -> craftEarthRunes = !craftEarthRunes, craftEarthRunes, 138.0f);
        showTooltip("Enable or disable the crafting of Earth Runes.");



        createCenteredButton("Craft Mind Runes", () -> craftMindRunes = !craftMindRunes, craftMindRunes, 138.0f);
        showTooltip("Enable or disable the crafting of Mind Runes.");

        ImGui.SameLine();

        createCenteredButton("Craft Air Runes", () -> craftAirRunes = !craftAirRunes, craftAirRunes, 138.0f);
        showTooltip("Enable or disable the crafting of Air Runes.");



        createCenteredButton("Craft Water Runes", () -> craftWaterRunes = !craftWaterRunes, craftWaterRunes, 138.0f);
        showTooltip("Enable or disable the crafting of Water Runes.");

//        createCenteredButton("Craft Death Runes", () -> craftDeathRunes = !craftDeathRunes, craftDeathRunes, 138.0f);
//        showTooltip("Enable or disable the crafting of Death Runes.");

        ImGui.SameLine();

//        createCenteredButton("Craft Law Runes", () -> craftLawRunes = !craftLawRunes, craftLawRunes, 138.0f);
//        showTooltip("Enable or disable the crafting of Law Runes.");

        createCenteredButton("Craft Chaos Runes", () -> craftChaosRunes = !craftChaosRunes, craftChaosRunes, 138.0f);
        showTooltip("Enable or disable the crafting of Chaos Runes.");
    }
}
