package net.Snow.Fishing;

import net.Snow.ImGui.SnowsTemplateGraphics;
import net.Snow.Snow;
import net.botwithus.rs3.game.skills.Skills;
import net.botwithus.rs3.imgui.ImGui;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import static net.Snow.Banking.Porters.usePorters;
import static net.Snow.ImGui.BankingImGui.bankingLocationsImGui;
import static net.Snow.ImGui.CustomLogger.logBlue;
import static net.Snow.ImGui.CustomLogger.showTooltip;
import static net.Snow.ImGui.Experience.displaySkillStats;
import static net.Snow.ImGui.Experience.inventoryMap;
import static net.Snow.ImGui.LogsInterface.createCenteredButton;
import static net.Snow.LevelLogout.CheckLevel.checkThreshold;
import static net.Snow.LevelLogout.CheckLevel.skillThresholdGraphics;
import static net.Snow.Mining.Familiar.useFamiliar;
import static net.Snow.Mining.MiningVariables.doAutomaticLevelling;
import static net.Snow.Mining.MiningVariables.useJujuPotion;
import static net.Snow.Spells.Lightform.useLightForm;
import static net.Snow.Woodcutting.Crystallise.useCrystallise;

public class FishingGraphics {

    public static void draw(SnowsTemplateGraphics sgc, Snow script, AtomicInteger startingFishingLevel, AtomicInteger startingFishingXP) throws IOException {
        ImGui.SetItemWidth(285.0F);
        int fishingResult = ImGui.Combo("##FishingSpot", FishingTypes.selectedFishIndex,
                FishingTypes.fishingSpots.stream()
                        .map(spot -> spot.getSpotName().equals("Disabled")
                                ? "Disabled"
                                : spot.getLocationName() + " - " + spot.getFishType())
                        .toArray(String[]::new));

        showTooltip("Select the fishing spot and method to use. Will move to the spot if not nearby.");

        if (fishingResult != FishingTypes.selectedFishIndex) {
            FishingTypes.selectedFishIndex = fishingResult;

            if (FishingTypes.selectedFishIndex > 0) {
                FishingSpot selectedFishingSpot = FishingTypes.fishingSpots.get(FishingTypes.selectedFishIndex);
                String selectedSpotName = selectedFishingSpot.getSpotName();
                String selectedMethod = selectedFishingSpot.getFishingMethod();

                logBlue("Fishing spot selected: " + selectedSpotName + " using " + selectedMethod);
                script.saveConfiguration();
            } else {
                logBlue("Fishing spot selection disabled.");
            }
        }



        bankingLocationsImGui();
        booleanOptions();
        skillThresholdGraphics(Skills.FISHING.getSkill());
        displaySkillStats(Skills.FISHING, startingFishingLevel.get(), startingFishingXP.get(),  inventoryMap);
    }

    private static void booleanOptions() {
        createCenteredButton("AIO", () -> doAutomaticLevelling = !doAutomaticLevelling, doAutomaticLevelling, 138.0f);
        showTooltip("Enable or disable the Auto Levelling.");

        ImGui.SameLine();

        createCenteredButton("Porters", () -> usePorters = !usePorters, usePorters, 138.0f);
        showTooltip("Enable or disable the use of porters.");

        createCenteredButton("Juju Potions", () -> useJujuPotion = !useJujuPotion, useJujuPotion, 138.0f);
        showTooltip("Enable or disable the use of Juju Potions Drinking and withdrawing from Bank.");

        ImGui.SameLine();

        createCenteredButton("Familiar", () -> useFamiliar = !useFamiliar, useFamiliar, 138.0f);
        showTooltip("Enable or disable the use of a familiar.");

        createCenteredButton("Light Form", () -> useLightForm = !useLightForm, useLightForm, 138.0f);
        showTooltip("Enable or disable the use of Light Form.");

        ImGui.SameLine();

        createCenteredButton("Crystallise", () -> useCrystallise = !useCrystallise, useCrystallise, 138.0f);
        showTooltip("Enable or disable the use of Crystallise.");

        createCenteredButton("Threshold", () -> checkThreshold = !checkThreshold, checkThreshold, 138.0f);
        showTooltip("Enable or disable the Level to Shutdown once level has been reached.");
    }
}
