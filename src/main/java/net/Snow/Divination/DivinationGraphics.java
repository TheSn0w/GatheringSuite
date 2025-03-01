package net.Snow.Divination;

import net.Snow.ImGui.SnowsTemplateGraphics;
import net.Snow.Snow;
import net.botwithus.rs3.game.login.LoginManager;
import net.botwithus.rs3.game.skills.Skills;
import net.botwithus.rs3.imgui.ImGui;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static net.Snow.Combat.Combat.player;
import static net.Snow.Divination.Divination.hasDowser;
import static net.Snow.ImGui.BankingImGui.bankingLocationsImGui;
import static net.Snow.ImGui.CustomLogger.showTooltip;
import static net.Snow.ImGui.Experience.displaySkillStats;
import static net.Snow.ImGui.ImGuiCol.Text;
import static net.Snow.ImGui.ImGuiCol.setStyleColor;
import static net.Snow.ImGui.LogsInterface.createCenteredButton;
import static net.Snow.LevelLogout.CheckLevel.checkThreshold;
import static net.Snow.LevelLogout.CheckLevel.skillThresholdGraphics;
import static net.Snow.Mining.Familiar.useFamiliar;

public class DivinationGraphics {

    public static boolean harvestChronicles = false;
    public static boolean onlyFarmChronicles = false;
    public static boolean depositChronicles = false;

    public static void draw(SnowsTemplateGraphics sgc, Snow script, AtomicInteger startingDivinationLevel, AtomicInteger startingDivinationXP, AtomicInteger startingHunterLevel, AtomicInteger startingHunterXP, Map<String, Integer> inventoryMap, Map<String, Integer> itemPrices, long scriptStartTime, boolean harvestChronicles) throws IOException {
        bankingLocationsImGui();
        booleanOptions();
        setStyleColor(Text, 255, 0, 0, 255); // Set text color to white
        ImGui.Text("Name: " + player.getName() + " | World: " + LoginManager.getWorld());
        ImGui.PopStyleColor(1);
        skillThresholdGraphics(Skills.DIVINATION.getSkill());
        displaySkillStats(Skills.DIVINATION, startingDivinationLevel.get(), startingDivinationXP.get(), inventoryMap);

        if (harvestChronicles) {
            ImGui.SeparatorText("Hunter Stats");
            displaySkillStats(Skills.HUNTER, startingHunterLevel.get(), startingHunterXP.get(), inventoryMap);
        }

        // Calculate and display the value of Incandescent energy
        int energyCount = inventoryMap.getOrDefault("Incandescent energy", 0);
        int energyPrice = itemPrices.getOrDefault("Incandescent energy", 0);
        int totalValue = energyCount * energyPrice;

        // Calculate hours elapsed and energy per hour
        double elapsedMillis = System.currentTimeMillis() - scriptStartTime;
        double hoursElapsed = elapsedMillis / 3600000.0;
        int energyPerHour = (hoursElapsed > 0) ? (int) (energyCount / hoursElapsed) : 0;

        // Calculate value earned per hour
        double valuePerHour = (hoursElapsed > 0) ? (totalValue / hoursElapsed) : 0;

        // Display energy stats in a formatted manner
        ImGui.SeparatorText("Inventory Value:");
        ImGui.Text("Incandescent energy: " + energyCount + " x " + formatPrice(energyPrice) + " = " + formatPrice(totalValue) + " coins");

        // Display total value
        ImGui.Separator();
        ImGui.Text("Total Value: " + formatPrice(totalValue) + " coins");

        // Display energy per hour
        ImGui.SeparatorText("Efficiency Stats:");
        ImGui.Text("Incandescent energy per hour: " + energyPerHour + " units/hour");

        setValueColor(valuePerHour);
        ImGui.Text("Approximate hourly value: " + formatPrice((int) valuePerHour) + " coins/hour");

        ImGui.Separator();
        ImGui.PopStyleColor(); // Reset the color style to default
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

    private static void setValueColor(double valuePerHour) {
        if (valuePerHour < 5_000_000) {
            setStyleColor(Text, 255, 0, 0, 255); // Red color
        } else if (valuePerHour < 10_000_000) {
            setStyleColor(Text, 255, 165, 0, 255); // Orange color
        } else { // 25M and above
            setStyleColor(Text, 0, 128, 0, 255); // Green color
        }
    }


    private static void booleanOptions() {
        createCenteredButton("Chronicles", () -> harvestChronicles = !harvestChronicles, harvestChronicles, 138.0f);
        showTooltip("Prioritise Chronicles over wisps.");

        ImGui.SameLine();

        createCenteredButton("Deposit Chronicles", () -> depositChronicles = !depositChronicles, depositChronicles, 138.0f);
        showTooltip("Will deposit Chronicles at the rift when the inventory contains between 10 to 25 chronicles.");

        createCenteredButton("Only Farm Chronicles", () -> onlyFarmChronicles = !onlyFarmChronicles, onlyFarmChronicles, 138.0f);
        showTooltip("Will only farm Chronicles and not harvest wisps, must enable Chronicles option above");

        ImGui.SameLine();

        createCenteredButton("Familiar", () -> useFamiliar = !useFamiliar, useFamiliar, 138.0f);
        showTooltip("Use a familiar to help with gathering energy.");

        createCenteredButton("Divine O Matic", () -> DivineOMatic.useDivineoMatic = !DivineOMatic.useDivineoMatic, DivineOMatic.useDivineoMatic, 138.0f);
        showTooltip("Automatically withdraw and fill the Divine-o-matic when it is full.");

        ImGui.SameLine();

        createCenteredButton("Dowser Support", () -> hasDowser = !hasDowser, hasDowser, 138.0f);
        showTooltip("Use the Dowser to find the nearest rift.");

        createCenteredButton("Threshold", () -> checkThreshold = !checkThreshold, checkThreshold, 138.0f);
        showTooltip("Enable or disable the Level to Shutdown once level has been reached.");

    }
}
