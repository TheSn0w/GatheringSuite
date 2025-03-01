package net.Snow.Rituals;

import net.Snow.ImGui.SnowsTemplateGraphics;
import net.Snow.Snow;
import net.botwithus.rs3.game.skills.Skills;
import net.botwithus.rs3.imgui.ImGui;

import java.util.concurrent.atomic.AtomicInteger;

import static net.Snow.ImGui.CustomLogger.logBlue;
import static net.Snow.ImGui.CustomLogger.showTooltip;
import static net.Snow.ImGui.Experience.displaySkillStats;
import static net.Snow.ImGui.Experience.inventoryMap;
import static net.Snow.ImGui.LogsInterface.createCenteredButton;
import static net.botwithus.rs3.imgui.ImGui.SetTooltip;

public class NecromancyGraphics {

    public static boolean enableDisturbances = false;
    public static boolean useNecromancyPotion = false;
    public static boolean useNecroOverloads = false;


    public static int necroThreshold;
    public static int getNecroThreshold() {
        return necroThreshold;
    }
    public static void setNecroThreshold(int threshold) {
        necroThreshold = threshold;
    }

    public static void draw(SnowsTemplateGraphics sgc, Snow script, AtomicInteger startingNecromancyLevel, AtomicInteger startingNecromancyXP) {
        createCenteredButton("Disturbances", () -> enableDisturbances = !enableDisturbances, enableDisturbances, 138.0f);
        showTooltip("Enable or disable gathering disturbances.");

        ImGui.SameLine();

        createCenteredButton("Necromancy Potion", () -> useNecromancyPotion = !useNecromancyPotion, useNecromancyPotion, 138.0f);
        showTooltip("Enable or disable the use of Necromancy Potion, you need a Necromancy Potion.");

        createCenteredButton("Overloads", () -> useNecroOverloads = !useNecroOverloads, useNecroOverloads, 138.0f);
        showTooltip("Enable or disable the use of Overloads, you need an Overload Potion.");

        if (useNecromancyPotion) {
            ImGui.SeparatorText("Set Required Necromancy Level");
            ImGui.SetItemWidth(100.0F);
            int newThreshold = getNecroThreshold();
            newThreshold = ImGui.InputInt("##ChargeThreshold", newThreshold);
            if (newThreshold < 0) {
                newThreshold = 0;
            } else if (newThreshold > 145) {
                newThreshold = 145;
            }
            setNecroThreshold(newThreshold);
            SetTooltip("Set the required necromancy level within the range 0 to 145");
            ImGui.SameLine();
            if (ImGui.Button("Set")) {
                setNecroThreshold(newThreshold);
                logBlue("User set required necromancy level to: " + newThreshold);
            }
        }
        displaySkillStats(Skills.NECROMANCY, startingNecromancyLevel.get(), startingNecromancyXP.get(), inventoryMap);
    }
}
