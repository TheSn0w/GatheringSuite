package net.Snow.Combat.Graphics;

import net.Snow.Banking.BankLocations;
import net.Snow.Banking.Banking;
import net.Snow.ImGui.ImGuiCol;
import net.Snow.ImGui.SnowsTemplateGraphics;
import net.Snow.Snow;
import net.botwithus.rs3.game.skills.Skills;
import net.botwithus.rs3.imgui.ImGui;
import net.botwithus.rs3.imgui.ImGuiWindowFlag;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import static net.Snow.Combat.Abilities.*;
import static net.Snow.Combat.Combat.scrimshawStatus;
import static net.Snow.Combat.Combat.useBank;
import static net.Snow.Combat.Graphics.LootingGraphics.renderLootingWindow;
import static net.Snow.Combat.Graphics.NPCScanGraphics.showNearbyNPCs;
import static net.Snow.Combat.Health.Potions.*;
import static net.Snow.Combat.Health.Scrimshaws.*;
import static net.Snow.Combat.Variables.*;
import static net.Snow.ImGui.CustomLogger.*;
import static net.Snow.ImGui.Experience.displaySkillStats;
import static net.Snow.ImGui.Experience.inventoryMap;
import static net.Snow.ImGui.ImGuiCol.setStyleColor;
import static net.Snow.ImGui.LogsInterface.createCenteredButton;

public class CombatGraphics {

    public static boolean showNearbyNpcs = false;
    public static boolean showAbilitiesWindow = false;
    public static boolean showLootOptions = false;
    public static boolean showPotionsWindow = false;
    public static boolean showScrimshawWindow = false;

    public static void draw(SnowsTemplateGraphics sgc, Snow script, AtomicInteger startingNecromancyLevel, AtomicInteger startingNecromancyXP) throws IOException {
//        // NPC Type Combo
//        ImGui.SetItemWidth(285.0F);
//        int npcResult = ImGui.Combo("##NpcType", NPCType.selectedNpcIndex, NPCType.NpcTypes.stream().map(npc -> npc.getName() + " (" + npc.getLocationName() + ")").toArray(String[]::new));
//        showTooltip("Select the NPC type to engage with, will move to if not nearby.");
//        if (npcResult != NPCType.selectedNpcIndex) {
//            NPCType.selectedNpcIndex = npcResult;
//            if (NPCType.selectedNpcIndex > 0) {
//                String selectedNpc = NPCType.NpcTypes.get(NPCType.selectedNpcIndex).getName();
//                logBlue("NPC selected: " + selectedNpc);
//                script.saveConfiguration(); // Assuming you want to save the configuration after selection
//            } else {
//                logBlue("NPC selection disabled.");
//            }
//        }


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
        booleanOptions();
        displaySkillStats(Skills.SLAYER, startingNecromancyLevel.get(), startingNecromancyXP.get(), inventoryMap);

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


    private static void booleanOptions() {
        createCenteredButton("NPC Scan", () -> showNearbyNpcs = !showNearbyNpcs, showNearbyNpcs, 138.0f);
        showTooltip("NPCs Scan.");

        ImGui.SameLine();

        createCenteredButton("Abilities", () -> showAbilitiesWindow = !showAbilitiesWindow, showAbilitiesWindow, 138.0f);
        showTooltip("Open abilities window.");

        createCenteredButton("Loot Options", () -> showLootOptions = !showLootOptions, showLootOptions, 138.0f);
        showTooltip("Open Loot Options window.");

        ImGui.SameLine();

        createCenteredButton("Potions", () -> showPotionsWindow = !showPotionsWindow, showPotionsWindow, 138.0f);
        showTooltip("Open Potions window.");

        createCenteredButton("Scrimshaws", () -> showScrimshawWindow = !showScrimshawWindow, showScrimshawWindow, 138.0f);
        showTooltip("Open Scrimshaws window.");

        ImGui.SameLine();

        createCenteredButton("Use Bank", () -> useBank = !useBank, useBank, 138.0f);
        showTooltip("Will teleport to Bank once Backpack is full");



        if (showAbilitiesWindow) {
            renderAbilitiesWindow();
        }

        if (showLootOptions) {
            renderLootingWindow();
        }

        if (showPotionsWindow) {
            renderPotionsWindow();
        }

        if (showNearbyNpcs) {
            showNearbyNPCs();
        }

        if (showScrimshawWindow) {
            renderScrimshawsWindow();
        }

    }


    private static void renderAbilitiesWindow() {
        setStyleColor(ImGuiCol.WindowBg, 0, 0, 0, 200);
        if (ImGui.Begin("Abilities", ImGuiWindowFlag.NoDecoration.getValue())) {
            ImGui.SetWindowSize((float) 300, (float) 150);

            createCenteredButton("Invoke Death", () -> useInvokeDeath = !useInvokeDeath, useInvokeDeath, 138.0f);
            showTooltip("Enable or disable Invoke Death.");

            ImGui.SameLine();

            createCenteredButton("Volley of Souls", () -> useVolleyofSouls = !useVolleyofSouls, useVolleyofSouls, 138.0f);
            showTooltip("Enable or disable Volley of Souls.");

            createCenteredButton("Essence of Finality", () -> useEssenceofFinality = !useEssenceofFinality, useEssenceofFinality, 138.0f);
            showTooltip("Enable or disable Essence of Finality.");

            ImGui.SameLine();

            createCenteredButton("Weapon Special", () -> useWeaponSpecialAttack = !useWeaponSpecialAttack, useWeaponSpecialAttack, 138.0f);
            showTooltip("Enable or disable Weapon Special Attack.");

            createCenteredButton("Defensives", () -> useDefensives = !useDefensives, useDefensives, 138.0f);
            showTooltip("Enable or disable Defensives.");

        }
        ImGui.PopStyleColor();
        ImGui.End();
    }

    private static void renderPotionsWindow() {
        setStyleColor(ImGuiCol.WindowBg, 0, 0, 0, 200);
        if (ImGui.Begin("Potions", ImGuiWindowFlag.NoDecoration.getValue())) {
            ImGui.SetWindowSize((float) 300, (float) 150);

            createCenteredButton("Overloads", () -> useOverloads = !useOverloads, useOverloads, 138.0f);
            showTooltip("Enable or disable Overloads.");

            ImGui.SameLine();

            createCenteredButton("Prayer Pots", () -> usePrayerPotions = !usePrayerPotions, usePrayerPotions, 138.0f);
            showTooltip("Enable of disable Prayer potions/Restore potions.");


            createCenteredButton("Aggression Pots", () -> useAgressionPotions = !useAgressionPotions, useAgressionPotions, 138.0f);
            showTooltip("Enable or disable the use of Aggression Potions/Flasks.");

            ImGui.SameLine();

            createCenteredButton("Weapon Posion", () -> useWeaponPoison = !useWeaponPoison, useWeaponPoison, 138.0f);
            showTooltip("Enable or disable the use of Aggression Potions/Flasks.");


        }
        ImGui.PopStyleColor();
        ImGui.End();
    }

    // Display the scrimshaw status in the renderScrimshawsWindow method
    private static void renderScrimshawsWindow() {

        setStyleColor(ImGuiCol.WindowBg, 0, 0, 0, 200);
        if (ImGui.Begin("Scrimshaws", ImGuiWindowFlag.NoDecoration.getValue())) {
            ImGui.SetWindowSize((float) 300, (float) 150);

            // Display the scrimshaw status with color based on active status
            boolean isActive = scrimshawStatus.contains("Non") || scrimshawStatus.contains("No");
            if (isActive) {
                    ImGui.PushStyleColor(ImGuiCol.Text, 255, 0, 0, 1.0f); // Green for active
                } else {
                    ImGui.PushStyleColor(ImGuiCol.Text, 0, 255, 0, 1.0f); // Red for inactive
                }

            ImGui.Text(scrimshawStatus);
            ImGui.PopStyleColor();

            createCenteredButton("Book of Jas", () -> scriptureofJas = !scriptureofJas, scriptureofJas, 138.0f);
            showTooltip("Enable or disable Scrimshaw of Jas.");

            ImGui.SameLine();

            createCenteredButton("Book of Wen", () -> scriptureofWen = !scriptureofWen, scriptureofWen, 138.0f);
            showTooltip("Enable or disable Scrimshaw of Wen.");

            createCenteredButton("Auto Detect", () -> useScrimshaws = !useScrimshaws, useScrimshaws, 138.0f);
            showTooltip("Auto Detects any scrimshaws in the inventory.");
        }
        ImGui.PopStyleColor();
        ImGui.End();
    }

}
