package net.Snow.Smelting;

import net.Snow.ImGui.SnowsTemplateGraphics;
import net.Snow.Snow;
import net.botwithus.rs3.game.skills.Skills;
import net.botwithus.rs3.imgui.ImGui;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import static net.Snow.ImGui.CustomLogger.logBlue;
import static net.Snow.ImGui.CustomLogger.showTooltip;
import static net.Snow.ImGui.Experience.displaySkillStats;
import static net.Snow.ImGui.Experience.inventoryMap;
import static net.Snow.ImGui.ImGuiCol.renderCenteredText;

public class SmeltingGraphics {
    public static void draw(SnowsTemplateGraphics sgc, Snow script, AtomicInteger startingSmithingLevel, AtomicInteger startingSmithingXP) throws IOException {
        // Render centered text using the helper method with a fixed window width
        renderCenteredText("Tested at Fort", 255, 0, 0, 255);
        renderCenteredText("Choose Option in smelter before starting", 255, 0, 0, 255);

        ImGui.SetItemWidth(285.0F);

        // Populate the combo box with smithing options
        int smithingResult = ImGui.Combo(
                "##SmithingType",
                SelectedType.selectedSmithingIndex,
                SelectedType.smithingOptions.toArray(new String[0])
        );

        showTooltip("Select the smithing type to use.");

        if (smithingResult != SelectedType.selectedSmithingIndex) {
            SelectedType.setSelectedSmithingIndex(smithingResult);

            if (SelectedType.selectedSmithingIndex >= 0 && SelectedType.selectedSmithingIndex < SelectedType.smithingOptions.size()) {
                String selectedSmithing = SelectedType.getSelectedSkill();
                logBlue("Smithing selected: " + selectedSmithing);
                script.saveConfiguration();
            } else {
                logBlue("Smithing selection disabled.");
            }
        }

        // Display the skill stats
        ImGui.SeparatorText("Smithing Stats");
        displaySkillStats(Skills.SMITHING, startingSmithingLevel.get(), startingSmithingXP.get(),  inventoryMap);
    }
}
