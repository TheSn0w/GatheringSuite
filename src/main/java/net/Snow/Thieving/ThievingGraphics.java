package net.Snow.Thieving;

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
import static net.Snow.Spells.CrystalMask.useCrystalMask;
import static net.Snow.Spells.Lightform.useLightForm;
import static net.Snow.Thieving.Food.healthThreshold;

public class ThievingGraphics {

    public static void draw(SnowsTemplateGraphics sgc, Snow script, AtomicInteger startingThievingLevel, AtomicInteger startingThievingXP) throws IOException {
        ImGui.SetItemWidth(285.0F);
        bankingLocationsImGui();

        booleanOptions();

        ImGui.SeparatorText("Set Health Threshold");
        ImGui.SetItemWidth(280);
        int newHealthThreshold = ImGui.Slider("##HealthThreshold", healthThreshold, 0, 100, 1);
        if (newHealthThreshold != healthThreshold) {
            healthThreshold = newHealthThreshold;
            logBlue("User set Health threshold to: " + healthThreshold);
        }
        showTooltip("Set the Health Threshold in the range 0 to 100");


        // Display the skill stats
        ImGui.SeparatorText("Thieving Stats");
        displaySkillStats(Skills.THIEVING, startingThievingLevel.get(), startingThievingXP.get(),  inventoryMap);

    }

    private static void booleanOptions() {

        createCenteredButton("Crystal Mask", () -> {
            useCrystalMask = !useCrystalMask;
        }, useCrystalMask, 138.0f);
        showTooltip("Enable or disable the use of Crystal Mask.");

        ImGui.SameLine();

        createCenteredButton("Light Form", () -> {
            useLightForm = !useLightForm;
        }, useLightForm, 138.0f);
        showTooltip("Enable or disable the use of Lightform");

        createCenteredButton("Porters", () -> usePorters = !usePorters, usePorters, 138.0f);
        showTooltip("Enable or disable the use of porters.");

//        createCenteredButton("Phoenix", () -> Phoexnis = !Phoexnis, Phoexnis, 138.0f);
//        showTooltip("Enable Phoenix Feathers.");
//
//        ImGui.SameLine();
//
//        createCenteredButton("Crux Druids", () -> Crux = !Crux, Crux, 138.0f);
//        showTooltip("Enable Crux Druids.");

    }
}
