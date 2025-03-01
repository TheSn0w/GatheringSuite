// WoodcuttingGraphics.java
package net.Snow.Woodcutting;

import net.Snow.ImGui.ImGuiCol;
import net.Snow.ImGui.SnowsTemplateGraphics;
import net.Snow.Snow;
import net.botwithus.rs3.game.skills.Skills;
import net.botwithus.rs3.imgui.ImGui;
import net.botwithus.rs3.imgui.ImGuiWindowFlag;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import static net.Snow.Banking.Porters.usePorters;
import static net.Snow.ImGui.BankingImGui.bankingLocationsImGui;
import static net.Snow.ImGui.CustomLogger.logBlue;
import static net.Snow.ImGui.CustomLogger.showTooltip;
import static net.Snow.ImGui.Experience.displaySkillStats;
import static net.Snow.ImGui.Experience.inventoryMap;
import static net.Snow.ImGui.ImGuiCol.setStyleColor;
import static net.Snow.ImGui.LogsInterface.createCenteredButton;
import static net.Snow.Mining.Familiar.useFamiliar;
import static net.Snow.Mining.MiningVariables.doAutomaticLevelling;
import static net.Snow.Mining.MiningVariables.useJujuPotion;
import static net.Snow.Spells.Lightform.useLightForm;
import static net.Snow.Woodcutting.BirdsNests.pickupNests;
import static net.Snow.Woodcutting.Crystallise.useCrystallise;
import static net.Snow.Woodcutting.Firemaking.doFiremaking;
import static net.Snow.Woodcutting.Fletching.*;
import static net.Snow.Woodcutting.IncenseSticks.doIncenseSticks;
import static net.Snow.Woodcutting.TreeTypes.selectedTreeIndex;
import static net.Snow.Woodcutting.TreeTypes.treeTypes;

public class WoodcuttingGraphics {

    public static void draw(SnowsTemplateGraphics sgc, Snow script, AtomicInteger startingWoodcuttingLevel, AtomicInteger startingWoodcuttingXP, AtomicInteger startingFiremakingLevel, AtomicInteger startingFiremakingXP) throws IOException {
        ImGui.SetItemWidth(285.0F);

        // Tree type combo
        int treeResult = ImGui.Combo("##TreeType", selectedTreeIndex, treeTypes.stream().map(tree -> tree.getName() + " (" + tree.getLocationName() + ")").toArray(String[]::new));
        showTooltip("Select the tree type to chop, will move to if not nearby.");
        if (treeResult != selectedTreeIndex) {
            selectedTreeIndex = treeResult;
            if (selectedTreeIndex > 0) {
                String selectedTree = treeTypes.get(selectedTreeIndex).getName();
                logBlue("Tree selected: " + selectedTree);
                script.saveConfiguration();
            } else {
                logBlue("Tree selection disabled.");
            }
        }
        bankingLocationsImGui();
        // Display all of our buttons
        booleanChecks();

        // Display the skill stats
        ImGui.SeparatorText("Woodcutting Stats");
        displaySkillStats(Skills.WOODCUTTING, startingWoodcuttingLevel.get(), startingWoodcuttingXP.get(),  inventoryMap);

        if (doFiremaking) {
            ImGui.SeparatorText("Firemaking Stats");
            displaySkillStats(Skills.FIREMAKING, startingFiremakingLevel.get(), startingFiremakingXP.get(),  inventoryMap);
        }
    }

    private static boolean showFletchingWindow = false;
    private static int clickCount = 0;

    private static void booleanChecks() {
        createCenteredButton("AIO", () -> {
            doAutomaticLevelling = !doAutomaticLevelling;
        }, doAutomaticLevelling, 138.0f);
        showTooltip("Enable or disable the Auto Levelling.");

        ImGui.SameLine();

        createCenteredButton("Porters", () -> {
            usePorters = !usePorters;
        }, usePorters, 138.0f);
        showTooltip("Enable or disable the use of porters, either equipping them or with Grace of the elves.");

        createCenteredButton("Nests", () -> {
            pickupNests = !pickupNests;
        }, pickupNests, 138.0f);
        showTooltip("Enable or disable the use of Bird's Nests.");

        ImGui.SameLine();

        createCenteredButton("Firemaking", () -> {
            doFiremaking = !doFiremaking;
        }, doFiremaking, 138.0f);
        showTooltip("Enable or disable the use of Firemaking, when Inventory is full, will not work with Porters or crystallise.");

        String buttonText;
        if (clickCount == 0 || clickCount == 4) {
            buttonText = "Fletching";
        } else if (clickCount == 1) {
            buttonText = "Show Settings";
        } else if (clickCount == 2) {
            buttonText = "Hide Settings";
        } else {
            buttonText = "Fletching";
        }

        createCenteredButton(buttonText, () -> {
            clickCount++;
            if (clickCount == 1) {
                doFletching = true;
            } else if (clickCount == 2) {
                showFletchingWindow = true;
            } else if (clickCount == 3) {
                showFletchingWindow = false;
            } else if (clickCount == 4) {
                doFletching = false;
                clickCount = 0; // Reset the click count
            }
        }, doFletching, 138.0f);

        showTooltip(buttonText + ", when Inventory is full, will not work with Porters or crystallise.");

        ImGui.SameLine();

        createCenteredButton("Incense Sticks", () -> {
            doIncenseSticks = !doIncenseSticks;
        }, doIncenseSticks, 138.0f);
        showTooltip("Enable or disable the use of Incense Sticks, when Inventory is full, will not work with Porters or crystallise.");

        createCenteredButton("Juju Potions", () -> {
            useJujuPotion = !useJujuPotion;
        }, useJujuPotion, 138.0f);
        showTooltip("Enable or disable the use of Juju Potions Drinking and withdrawing from Bank.");

        ImGui.SameLine();

        createCenteredButton("Familiar", () -> {
            useFamiliar = !useFamiliar;
        }, useFamiliar, 138.0f);
        showTooltip("Enable or disable the use of Familiars, you need a Super Restore in Bank and only supports Beaver Pouch.");

        createCenteredButton("Crystallise", () -> {
            useCrystallise = !useCrystallise;
        }, useCrystallise, 138.0f);
        showTooltip("Enable or disable the use of Crystallise, must have required runes to cast and ability must be on action bar.");

        ImGui.SameLine();

        createCenteredButton("Lightform", () -> {
            useLightForm = !useLightForm;
        }, useLightForm, 138.0f);
        showTooltip("Enable or disable the use of Lightform, will bank for restore potion and will drink if prayer points are low.");


        if (showFletchingWindow) {
            setStyleColor(ImGuiCol.WindowBg, 0, 0, 0, 200);
            if (ImGui.Begin("Incense Sticks Settings", ImGuiWindowFlag.NoDecoration.getValue())) {
                ImGui.SetWindowSize((float) 300, (float) 90);

                createCenteredButton("Arrow Shafts", () -> {
                    if (arrowShafts) {
                        arrowShafts = false;
                    } else {
                        resetFletchingOptions();
                        arrowShafts = true;
                    }
                }, arrowShafts, 138.0f);

                ImGui.SameLine();

                createCenteredButton("Short Bow", () -> {
                    if (shortBow) {
                        shortBow = false;
                    } else {
                        resetFletchingOptions();
                        shortBow = true;
                    }
                }, shortBow, 138.0f);

                createCenteredButton("Stocks", () -> {
                    if (woodenStock) {
                        woodenStock = false;
                    } else {
                        resetFletchingOptions();
                        woodenStock = true;
                    }
                }, woodenStock, 138.0f);

                ImGui.SameLine();

                createCenteredButton("Shield Bow", () -> {
                    if (shieldBow) {
                        shieldBow = false;
                    } else {
                        resetFletchingOptions();
                        shieldBow = true;
                    }
                }, shieldBow, 138.0f);
            }

            ImGui.PopStyleColor(1);
            ImGui.End();
        }
    }

    private static void resetFletchingOptions() {
        arrowShafts = false;
        shortBow = false;
        woodenStock = false;
        shieldBow = false;
    }
}