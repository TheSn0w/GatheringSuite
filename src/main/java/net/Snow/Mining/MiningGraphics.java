// MiningGraphics.java
package net.Snow.Mining;

import net.Snow.ImGui.SnowsTemplateGraphics;
import net.Snow.Navigation;
import net.Snow.Snow;
import net.botwithus.rs3.game.Coordinate;
import net.botwithus.rs3.game.skills.Skills;
import net.botwithus.rs3.imgui.ImGui;
import net.botwithus.rs3.imgui.ImGuiWindowFlag;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static net.Snow.Banking.Porters.usePorters;
import static net.Snow.ImGui.BankingImGui.bankingLocationsImGui;
import static net.Snow.ImGui.CustomLogger.*;
import static net.Snow.ImGui.Experience.displaySkillStats;
import static net.Snow.ImGui.Experience.inventoryMap;
import static net.Snow.ImGui.LogsInterface.createCenteredButton;
import static net.Snow.Mining.Familiar.useFamiliar;
import static net.Snow.Mining.MiningVariables.*;
import static net.Snow.Mining.RockTypes.rockTypes;
import static net.Snow.Mining.RockTypes.selectedRockIndex;
import static net.Snow.Mining.Scanner.getSceneObjectTableData;
import static net.Snow.Spells.Lightform.useLightForm;
import static net.Snow.Woodcutting.Crystallise.useCrystallise;

public class MiningGraphics {

    public static int headbarWidthThreshold = 235; // Default value


    public static void draw(SnowsTemplateGraphics sgc, Snow script, AtomicInteger startingMiningLevel, AtomicInteger startingMiningXP) throws IOException {
        // Rock type combo
        ImGui.SetItemWidth(285.0F);
        int rockResult = ImGui.Combo("##RockType", selectedRockIndex, rockTypes.stream().map(rock -> rock.getName() + " (" + rock.getLocationName() + ")").toArray(String[]::new));
        showTooltip("Select the rock type to mine, will move to if not nearby.");
        if (rockResult != selectedRockIndex) {
            selectedRockIndex = rockResult;
            if (selectedRockIndex > 0) {
                String selectedRock = rockTypes.get(selectedRockIndex).getName();
                logBlue("Rock selected: " + selectedRock);
                script.saveConfiguration();
            } else {
                logBlue("Rock selection disabled.");
            }
        }
        bankingLocationsImGui();
        booleanOptions();

        ImGui.SeparatorText("Set Stamina Threshold");
        ImGui.SetItemWidth(280);
        int newHeadbarWidthThreshold = ImGui.Slider("##HeadbarWidth", headbarWidthThreshold, 0, 250, 1);
        if (newHeadbarWidthThreshold != headbarWidthThreshold) {
            headbarWidthThreshold = newHeadbarWidthThreshold;
            logGrey("User set Stamina threshold to: " + headbarWidthThreshold);
        }
        showTooltip("Set the Stamina Threshold width in the range 0 to 250");


        displaySkillStats(Skills.MINING, startingMiningLevel.get(), startingMiningXP.get(),  inventoryMap);
    }

    public static boolean showNearbyRocks = false;

    private static void booleanOptions() {
        createCenteredButton("AIO Mining", () -> doAutomaticLevelling = !doAutomaticLevelling, doAutomaticLevelling, 138.0f);
        showTooltip("Enable or disable the Auto Levelling.");

        ImGui.SameLine();

        createCenteredButton("Rockertunity", () -> useRockertunities = !useRockertunities, useRockertunities, 138.0f);
        showTooltip("Enable or disable the interaction of Rockertunities.");

        createCenteredButton("Nearby Rocks", () -> showNearbyRocks = !showNearbyRocks, showNearbyRocks, 138.0f);
        showTooltip("Show nearby rocks in a new Window.");

        ImGui.SameLine();

        createCenteredButton("Porters", () -> usePorters = !usePorters, usePorters, 138.0f);
        showTooltip("Enable or disable the use of porters.");

        createCenteredButton("Juju Potions", () -> useJujuPotion = !useJujuPotion, useJujuPotion, 138.0f);
        showTooltip("Enable or disable the use of Juju Potions Drinking and withdrawing from Bank.");

        ImGui.SameLine();

        createCenteredButton("Familiar", () -> useFamiliar = !useFamiliar, useFamiliar, 138.0f);
        showTooltip("Enable or disable the use of Familiars, you need a Super Restore in Bank and Supports Lava Titan or Binding Contract (Gargoyle).");

        createCenteredButton("Crystallise", () -> useCrystallise = !useCrystallise, useCrystallise, 138.0f);
        showTooltip("Enable or disable the use of Crystallise.");

        ImGui.SameLine();

        createCenteredButton("Lightform", () -> useLightForm = !useLightForm, useLightForm, 138.0f);
        showTooltip("Enable or disable the use of Lightform.");

        if (showNearbyRocks) {
            if (ImGui.Begin("Nearby Rocks", ImGuiWindowFlag.NoDecoration.getValue())) {
                ImGui.SetWindowSize((float) 400, (float) 175);
                List<List<String>> tableData = getSceneObjectTableData();

                ImGui.SetItemWidth(600);

                if (ImGui.ListBoxHeader("", 385, 160)) {
                    ImGui.Columns(1, "SceneObject names", true);
                    for (int i = 0; i < tableData.size(); i++) {
                        List<String> row = tableData.get(i);
                        String rockName = row.get(0);

                        String sceneObjectIdentifier = rockName + "##" + i;

                        ImGui.Selectable(sceneObjectIdentifier, false, 0);
                        if (ImGui.IsItemClicked(ImGui.MouseButton.LEFT_BUTTON)) {
                            rockTypes.add(new Navigation(rockName, new Coordinate(0, 0, 0), "Unknown Location", 0)); // Default minimum level
                            logBlue("Rock added: " + rockName);
                        }

                        ImGui.NextColumn();
                    }
                    ImGui.Columns(1, "Column", false);
                    ImGui.ListBoxFooter();
                }
            }
            ImGui.End();
        }
    }
}