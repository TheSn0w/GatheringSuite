package net.Snow.Combat;

import net.Snow.Banking.BankLocations;
import net.Snow.Banking.Banking;
import net.Snow.Combat.NPCLocation.NPCType;
import net.Snow.Snow;
import net.Snow.ImGui.SnowsTemplateGraphics;
import net.botwithus.rs3.game.skills.Skills;
import net.botwithus.rs3.imgui.ImGui;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import static net.Snow.Combat.Variables.*;
import static net.Snow.ImGui.CustomLogger.*;
import static net.Snow.ImGui.Experience.displaySkillStats;
import static net.Snow.ImGui.Experience.inventoryMap;

public class CombatGraphics {

    public static void draw(SnowsTemplateGraphics sgc, Snow script, AtomicInteger startingNecromancyLevel, AtomicInteger startingNecromancyXP) throws IOException {
        // NPC Type Combo
        ImGui.SetItemWidth(285.0F);
        int npcResult = ImGui.Combo("##NpcType", NPCType.selectedNpcIndex, NPCType.NpcTypes.stream().map(npc -> npc.getName() + " (" + npc.getLocationName() + ")").toArray(String[]::new));
        showTooltip("Select the NPC type to engage with, will move to if not nearby.");
        if (npcResult != NPCType.selectedNpcIndex) {
            NPCType.selectedNpcIndex = npcResult;
            if (NPCType.selectedNpcIndex > 0) {
                String selectedNpc = NPCType.NpcTypes.get(NPCType.selectedNpcIndex).getName();
                logBlue("NPC selected: " + selectedNpc);
                script.saveConfiguration(); // Assuming you want to save the configuration after selection
            } else {
                logBlue("NPC selection disabled.");
            }
        }
        ImGui.SeparatorText("Banking Location");

        // Bank Location Combo
        ImGui.SetItemWidth(285.0F);
        int bankResult = ImGui.Combo("##BankLocation", BankLocations.selectedBankIndex, BankLocations.bankings.stream().map(Banking::getName).toArray(String[]::new));
        showTooltip("Select the bank location to use for banking.");
        if (bankResult != BankLocations.selectedBankIndex) {
            BankLocations.selectedBankIndex = bankResult;
            if (bankResult > 0) {
                String selectedBank = BankLocations.bankings.get(bankResult).getName();
                logBlue("Bank selected: " + selectedBank);
                script.saveConfiguration();
            } else {
                logBlue("Bank selection disabled.");
            }
        }
        healthPointsThresholdGraphics();
        prayerPointsThresholdGraphics();
        displaySkillStats(Skills.NECROMANCY, startingNecromancyLevel.get(), startingNecromancyXP.get(),  inventoryMap);

    }


    public static void healthPointsThresholdGraphics() {
        // Start the UI element
        ImGui.SeparatorText("Set Health Points Threshold");
        ImGui.SetItemWidth(280);

        // Get the current health points threshold
        int displayedHealthThreshold = Math.max(1, getHealthPointsThreshold());  // Ensure the minimum is 1

        // Use the ImGui slider to adjust the health points threshold
        int newDisplayedHealthThreshold = ImGui.Slider("##HealthThreshold", displayedHealthThreshold, 1, 100, 1);

        // Check if the health points threshold has changed and set the new value
        if (newDisplayedHealthThreshold != displayedHealthThreshold) {
            if (newDisplayedHealthThreshold == 1) {
                setHealthThreshold(0);  // Treat 1 as "disabled" by setting it to 0 internally
                logOrange("Health points threshold disabled.");
            } else {
                setHealthThreshold(newDisplayedHealthThreshold);
                logBlue("User set health points threshold to: " + newDisplayedHealthThreshold);
            }
        }

        // Set a tooltip to explain the range and the disable option
        ImGui.SetTooltip("Set the required health points threshold within the range 1 to 100, where 1 means disabled.");
    }

    public static void prayerPointsThresholdGraphics() {
        // Start the UI element
        ImGui.SeparatorText("Set Prayer Points Threshold");
        ImGui.SetItemWidth(280);

        // Get the current prayer points threshold and convert it to a 1-990 range for the slider
        int displayedPrayerThreshold = Math.max(10, getPrayerPointsThreshold()) / 10;  // Ensure the minimum is 10

        // Use the ImGui slider to adjust the prayer points threshold
        int newDisplayedPrayerThreshold = ImGui.Slider("##PrayerThreshold", displayedPrayerThreshold, 10, 990, 1);

        // Convert the displayed threshold back to the 0-9900 scale
        int newPrayerPointsThreshold = newDisplayedPrayerThreshold * 10;

        // Check if the prayer points threshold has changed and set the new value
        if (newPrayerPointsThreshold != getPrayerPointsThreshold()) {
            if (newDisplayedPrayerThreshold == 100) {
                setPrayerPointsThreshold(0);  // Treat 10 as "disabled" by setting it to 0 internally
                logOrange("Prayer points threshold disabled.");
            } else {
                setPrayerPointsThreshold(newPrayerPointsThreshold);
                logBlue("User set prayer points threshold to: " + newDisplayedPrayerThreshold);
            }
        }

        // Set a tooltip to explain the range and the disable option
        ImGui.SetTooltip("Set the required prayer points threshold within the range 10 to 990, where 10 means disabled.");
    }





}
