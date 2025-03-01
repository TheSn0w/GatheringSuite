package net.Snow.Fletching;

import net.Snow.ImGui.SnowsTemplateGraphics;
import net.Snow.Snow;
import net.botwithus.rs3.game.skills.Skills;
import net.botwithus.rs3.imgui.ImGui;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import static net.Snow.ImGui.CustomLogger.logOrange;
import static net.Snow.ImGui.CustomLogger.showTooltip;
import static net.Snow.ImGui.Experience.displaySkillStats;
import static net.Snow.ImGui.Experience.inventoryMap;
import static net.Snow.ImGui.ImGuiCol.renderCenteredText;

public class FletchingGraphics {

    public static void draw(SnowsTemplateGraphics sgc, Snow script, AtomicInteger startingFletchingLevel, AtomicInteger startingFletchingXP) throws IOException {
        renderCenteredText("Will work anywhere", 255, 0, 0, 255);
        renderCenteredText("Will fletch anything", 255, 0, 0, 255);
        renderCenteredText("Choose option before", 255, 0, 0, 255);

        ImGui.SetItemWidth(285.0F);

        // Add the combo box for selecting the fletching type
        int fletchingResult = ImGui.Combo(
                "##FletchingType",
                FletchingTypes.selectedFletchingIndex,
                FletchingTypes.fletchingOptions.toArray(new String[0])
        );

        // Show a tooltip for the combo box
        showTooltip("Select the fletching type to use.");

        // Update the selected fletching type if the selection changes
        if (fletchingResult != FletchingTypes.selectedFletchingIndex) {
            FletchingTypes.setSelectedFletchingIndex(fletchingResult);
            String selectedFletching = FletchingTypes.getSelectedFletching();

            if (!selectedFletching.equals("Disabled")) {
                logOrange("Fletching selected: " + selectedFletching);
                script.saveConfiguration();
            } else {
                logOrange("Fletching selection disabled.");
            }
        }

        // Display the skill stats
        ImGui.SeparatorText("Fletching Stats");
        displaySkillStats(Skills.FLETCHING, startingFletchingLevel.get(), startingFletchingXP.get(), inventoryMap);
    }
}
